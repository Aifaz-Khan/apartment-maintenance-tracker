package com.example.maintenance.notification.application;

import com.example.maintenance.complaint.domain.Complaint;
import com.example.maintenance.complaint.infrastructure.ComplaintRepository;
import com.example.maintenance.society.domain.SocietyAdmin;
import com.example.maintenance.society.infrastructure.SocietyAdminRepository;
import com.example.maintenance.society.application.ResourceNotFoundException;
import com.example.maintenance.user.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@ConditionalOnProperty(prefix="app.notifications.email",name="enabled",havingValue="true")
public class ComplaintNotificationService {
    private static final Logger log=LoggerFactory.getLogger(ComplaintNotificationService.class);
    private final ComplaintRepository complaints;
    private final SocietyAdminRepository admins;
    private final JavaMailSender mailSender;
    private final String from;

    public ComplaintNotificationService(ComplaintRepository complaints,SocietyAdminRepository admins,
        JavaMailSender mailSender,@Value("${app.notifications.email.from}") String from){
        this.complaints=complaints;this.admins=admins;this.mailSender=mailSender;this.from=from;
    }

    @Transactional(readOnly=true)
    public void sendComplaintSubmitted(Long complaintId){
        Complaint complaint=complaints.findById(complaintId).orElseThrow(()->new ResourceNotFoundException("Complaint not found."));
        List<SocietyAdmin> recipients=admins.findAllBySocietyId(complaint.getSociety().getId());
        for(SocietyAdmin assignment:recipients){
            User admin=assignment.getUser();
            if(!admin.isActive()||admin.getEmail()==null||admin.getEmail().isBlank())continue;
            SimpleMailMessage message=new SimpleMailMessage();
            message.setFrom(from);message.setTo(admin.getEmail());
            message.setSubject("New maintenance request #"+complaint.getId()+" - "+complaint.getSociety().getName());
            message.setText(body(complaint));
            try { mailSender.send(message); }
            catch(MailException ex){log.warn("Could not send new complaint notification for complaint {} to assigned admin.",complaintId,ex);}
        }
    }

    private String body(Complaint complaint){
        return "A resident submitted a new maintenance request.\n\n"+
            "Request: #"+complaint.getId()+" - "+complaint.getTitle()+"\n"+
            "Society: "+complaint.getSociety().getName()+"\n"+
            "Unit: "+complaint.getApartmentUnit().getBuildingName()+" / "+complaint.getApartmentUnit().getUnitNumber()+"\n"+
            "Category: "+complaint.getCategory().getName()+"\n"+
            "Priority: "+complaint.getPriority().name()+"\n"+
            "Location: "+complaint.getLocationArea()+(complaint.getLocationDetails()==null?"":" - "+complaint.getLocationDetails())+"\n\n"+
            complaint.getDescription()+"\n\nSign in to the maintenance tracker to review and update this request.";
    }
}
