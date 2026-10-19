package com.example.maintenance.residency.domain;

import com.example.maintenance.apartment.domain.ApartmentUnit;
import com.example.maintenance.user.domain.User;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "residency_transfer_requests")
public class ResidencyTransferRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apartment_unit_id", nullable = false)
    private ApartmentUnit apartmentUnit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_residency_id")
    private UnitResidency currentResidency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposed_user_id", nullable = false)
    private User proposedUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "residency_type", nullable = false, length = 30)
    private ResidencyType residencyType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by", nullable = false)
    private User requestedBy;

    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;

    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TransferStatus status = TransferStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public ResidencyTransferRequest() {
    }

    public ResidencyTransferRequest(ApartmentUnit apartmentUnit, UnitResidency currentResidency,
                                  User proposedUser, ResidencyType residencyType, User requestedBy,
                                  LocalDate effectiveDate, String reason) {
        this.apartmentUnit = apartmentUnit;
        this.currentResidency = currentResidency;
        this.proposedUser = proposedUser;
        this.residencyType = residencyType;
        this.requestedBy = requestedBy;
        this.effectiveDate = effectiveDate;
        this.reason = reason;
        this.status = TransferStatus.PENDING;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    public void onPreUpdate() {
        this.updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ApartmentUnit getApartmentUnit() {
        return apartmentUnit;
    }

    public void setApartmentUnit(ApartmentUnit apartmentUnit) {
        this.apartmentUnit = apartmentUnit;
    }

    public UnitResidency getCurrentResidency() {
        return currentResidency;
    }

    public void setCurrentResidency(UnitResidency currentResidency) {
        this.currentResidency = currentResidency;
    }

    public User getProposedUser() {
        return proposedUser;
    }

    public void setProposedUser(User proposedUser) {
        this.proposedUser = proposedUser;
    }

    public ResidencyType getResidencyType() {
        return residencyType;
    }

    public void setResidencyType(ResidencyType residencyType) {
        this.residencyType = residencyType;
    }

    public User getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(User requestedBy) {
        this.requestedBy = requestedBy;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public TransferStatus getStatus() {
        return status;
    }

    public void setStatus(TransferStatus status) {
        this.status = status;
    }

    public User getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(User reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public Instant getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(Instant reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
