package com.example.maintenance.auth.api;
public record UserSummaryDto(Long id,String email,String fullName,String phone,String role,Long societyId,String societyName,Long apartmentUnitId,String unitNumber,String buildingName){}
