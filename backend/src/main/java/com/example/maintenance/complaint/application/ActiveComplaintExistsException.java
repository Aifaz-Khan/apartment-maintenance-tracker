package com.example.maintenance.complaint.application;

public class ActiveComplaintExistsException extends RuntimeException {
    public ActiveComplaintExistsException(){super("An active complaint already exists for this apartment unit.");}
}
