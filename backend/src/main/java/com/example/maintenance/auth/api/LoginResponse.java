package com.example.maintenance.auth.api;
public record LoginResponse(String token,String tokenType,long expiresInSeconds,UserSummaryDto user){}
