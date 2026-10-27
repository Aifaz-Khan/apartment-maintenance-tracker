package com.example.maintenance.apartment.application;

import com.example.maintenance.apartment.api.*;
import com.example.maintenance.apartment.domain.ApartmentUnit;
import com.example.maintenance.apartment.infrastructure.ApartmentUnitRepository;
import com.example.maintenance.society.api.PageResponse;
import com.example.maintenance.society.application.*;
import com.example.maintenance.society.domain.Society;
import com.example.maintenance.society.infrastructure.SocietyRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApartmentUnitService {
    private final ApartmentUnitRepository units;
    private final SocietyRepository societies;
    private final SocietyAccessService access;
    public ApartmentUnitService(ApartmentUnitRepository units,SocietyRepository societies,SocietyAccessService access){this.units=units;this.societies=societies;this.access=access;}

    @Transactional(readOnly=true) public PageResponse<ApartmentUnitResponse> list(Long adminId,Long societyId,int page,int size){
        access.requireSocietyScope(adminId,societyId); validatePage(page,size);
        return PageResponse.from(units.findAllBySocietyId(societyId,PageRequest.of(page,size,Sort.by("buildingName").ascending().and(Sort.by("unitNumber").ascending()))),ApartmentUnitResponse::from);
    }
    @Transactional public ApartmentUnitResponse create(Long adminId,Long societyId,ApartmentUnitRequest request){
        access.requireSocietyScope(adminId,societyId);
        Society society=societies.findById(societyId).orElseThrow(()->new ResourceNotFoundException("Society not found."));
        if(!"ACTIVE".equals(society.getStatus()))throw new ResourceConflictException("Units cannot be added to an inactive society.");
        String building=request.buildingName().trim(),number=request.unitNumber().trim();
        if(units.existsBySocietyIdAndBuildingNameIgnoreCaseAndUnitNumberIgnoreCase(societyId,building,number))throw new ResourceConflictException("This unit already exists in the society.");
        ApartmentUnit unit=new ApartmentUnit(society,building,number,request.floorNumber(),clean(request.unitLabel()));
        return ApartmentUnitResponse.from(units.save(unit));
    }
    private void validatePage(int page,int size){if(page<0||size<1||size>100)throw new IllegalArgumentException("Page must be non-negative and size must be between 1 and 100.");}
    private String clean(String value){return value==null||value.isBlank()?null:value.trim();}
}
