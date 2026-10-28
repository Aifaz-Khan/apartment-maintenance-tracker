package com.example.maintenance.attachment.application;

import com.example.maintenance.attachment.api.AttachmentResponse;
import com.example.maintenance.attachment.domain.*;
import com.example.maintenance.attachment.infrastructure.*;
import com.example.maintenance.complaint.domain.Complaint;
import com.example.maintenance.complaint.infrastructure.ComplaintRepository;
import com.example.maintenance.residency.domain.*;
import com.example.maintenance.residency.infrastructure.UnitResidencyRepository;
import com.example.maintenance.society.application.*;
import com.example.maintenance.user.domain.*;
import com.example.maintenance.user.infrastructure.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.*;

@Service
public class AttachmentService {
    private final AttachmentRepository attachments;
    private final ComplaintRepository complaints;
    private final UserRepository users;
    private final UnitResidencyRepository residencies;
    private final SocietyAccessService societyAccess;
    private final PrivateAttachmentStorage storage;

    public AttachmentService(AttachmentRepository attachments, ComplaintRepository complaints, UserRepository users,
        UnitResidencyRepository residencies, SocietyAccessService societyAccess, PrivateAttachmentStorage storage) {
        this.attachments=attachments; this.complaints=complaints; this.users=users; this.residencies=residencies;
        this.societyAccess=societyAccess; this.storage=storage;
    }

    @Value("${app.attachments.max-image-bytes:5242880}") private long imageLimit;
    @Value("${app.attachments.max-audio-bytes:10485760}") private long audioLimit;

    @Transactional
    public AttachmentResponse upload(Long userId, Long complaintId, MultipartFile file) {
        User user=activeUser(userId); Complaint complaint=findComplaint(complaintId); authorize(user,complaint);
        if(file==null||file.isEmpty())throw new IllegalArgumentException("Choose a non-empty image or audio file.");
        String contentType=Optional.ofNullable(file.getContentType()).orElse("").toLowerCase(Locale.ROOT);
        AttachmentKind kind=kind(contentType);
        long limit=kind==AttachmentKind.IMAGE?imageLimit:audioLimit;
        if(file.getSize()>limit)throw new IllegalArgumentException(kind==AttachmentKind.IMAGE?"Images must be 5 MB or smaller.":"Audio files must be 10 MB or smaller.");
        byte[] header;
        try(java.io.InputStream input=file.getInputStream()) { header=input.readNBytes(16); }
        catch(IOException ex){throw new IllegalArgumentException("The uploaded file could not be read.",ex);}
        if(!matches(contentType,header,kind))throw new IllegalArgumentException("The file content does not match an allowed image or audio format.");
        String key=UUID.randomUUID().toString(); String filename=safeFilename(file.getOriginalFilename());
        try {
            storage.store(key,file.getInputStream());
            ComplaintAttachment entity=attachments.saveAndFlush(new ComplaintAttachment(complaint,user,key,filename,canonicalType(contentType),file.getSize(),kind));
            return AttachmentResponse.from(entity);
        } catch(IOException ex) {
            try { storage.delete(key); } catch(IOException ignored) { }
            throw new IllegalArgumentException("The attachment could not be stored.",ex);
        } catch(RuntimeException ex) {
            try { storage.delete(key); } catch(IOException ignored) { }
            throw ex;
        }
    }

    @Transactional(readOnly=true)
    public List<AttachmentResponse> list(Long userId,Long complaintId) {
        authorize(activeUser(userId),findComplaint(complaintId));
        return attachments.findAllByComplaintIdOrderByCreatedAtAsc(complaintId).stream().map(AttachmentResponse::from).toList();
    }

    @Transactional(readOnly=true)
    public Download download(Long userId,Long complaintId,Long attachmentId) {
        authorize(activeUser(userId),findComplaint(complaintId));
        ComplaintAttachment attachment=attachments.findByIdAndComplaintId(attachmentId,complaintId)
            .orElseThrow(()->new ResourceNotFoundException("Attachment not found."));
        try { return new Download(attachment,storage.read(attachment.getStorageKey())); }
        catch(IOException ex) { throw new ResourceNotFoundException("Attachment file not found."); }
    }

    private User activeUser(Long id){return users.findById(id).filter(User::isActive).orElseThrow(()->new ResourceNotFoundException("Active user account not found."));}
    private Complaint findComplaint(Long id){return complaints.findById(id).orElseThrow(()->new ResourceNotFoundException("Complaint not found."));}
    private void authorize(User user,Complaint complaint){
        if(user.getRole()==UserRole.SOCIETY_ADMIN){societyAccess.requireSocietyScope(user.getId(),complaint.getSociety().getId());return;}
        if(user.getRole()==UserRole.RESIDENT){
            UnitResidency residency=residencies.findByResidentUserIdAndStatus(user.getId(),ResidencyStatus.ACTIVE)
                .orElseThrow(()->new AccessDeniedException("An active apartment residency is required."));
            if(residency.getApartmentUnit().getId().equals(complaint.getApartmentUnit().getId()))return;
        }
        throw new AccessDeniedException("This account cannot access this complaint attachment.");
    }
    private AttachmentKind kind(String type){
        if(Set.of("image/jpeg","image/png","image/webp").contains(type))return AttachmentKind.IMAGE;
        if(Set.of("audio/mpeg","audio/mp3","audio/wav","audio/x-wav","audio/mp4","audio/m4a","audio/webm").contains(type))return AttachmentKind.AUDIO;
        throw new IllegalArgumentException("Allowed formats: JPEG, PNG, WebP, MP3, WAV, M4A, and WebM audio.");
    }
    private boolean matches(String type,byte[] b,AttachmentKind kind){
        if(kind==AttachmentKind.IMAGE){
            if(type.equals("image/jpeg"))return b.length>=3&&(b[0]&255)==255&&(b[1]&255)==216&&(b[2]&255)==255;
            if(type.equals("image/png"))return b.length>=8&&(b[0]&255)==137&&b[1]==80&&b[2]==78&&b[3]==71&&b[4]==13&&b[5]==10&&b[6]==26&&b[7]==10;
            return b.length>=12&&ascii(b,0,"RIFF")&&ascii(b,8,"WEBP");
        }
        if(type.equals("audio/wav")||type.equals("audio/x-wav"))return b.length>=12&&ascii(b,0,"RIFF")&&ascii(b,8,"WAVE");
        if(type.equals("audio/mp4")||type.equals("audio/m4a"))return b.length>=8&&ascii(b,4,"ftyp");
        if(type.equals("audio/webm"))return b.length>=4&&(b[0]&255)==0x1a&&(b[1]&255)==0x45&&(b[2]&255)==0xdf&&(b[3]&255)==0xa3;
        return b.length>=3&&(ascii(b,0,"ID3")||((b[0]&255)==255&&((b[1]&0xe0)==0xe0)));
    }
    private boolean ascii(byte[] b,int offset,String value){if(b.length<offset+value.length())return false;for(int i=0;i<value.length();i++)if(b[offset+i]!=(byte)value.charAt(i))return false;return true;}
    private String canonicalType(String type){return type.equals("audio/mp3")?"audio/mpeg":type.equals("audio/x-wav")?"audio/wav":type.equals("audio/m4a")?"audio/mp4":type;}
    private String safeFilename(String original){String name=original==null?"attachment":original.replace('\\','/');name=name.substring(name.lastIndexOf('/')+1).replaceAll("[\\r\\n\\p{Cntrl}]","_").trim();if(name.isBlank())name="attachment";return name.length()>255?name.substring(name.length()-255):name;}

    public record Download(ComplaintAttachment attachment,byte[] bytes){}
}
