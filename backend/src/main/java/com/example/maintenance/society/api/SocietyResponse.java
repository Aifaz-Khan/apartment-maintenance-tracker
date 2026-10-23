package com.example.maintenance.society.api;

import com.example.maintenance.society.domain.Society;

public record SocietyResponse(Long id, String name, String code, String addressLine1, String addressLine2,
    String city, String state, String postalCode, String contactEmail, String status) {
    public static SocietyResponse from(Society s) {
        return new SocietyResponse(s.getId(), s.getName(), s.getCode(), s.getAddressLine1(), s.getAddressLine2(),
            s.getCity(), s.getState(), s.getPostalCode(), s.getContactEmail(), s.getStatus());
    }
}
