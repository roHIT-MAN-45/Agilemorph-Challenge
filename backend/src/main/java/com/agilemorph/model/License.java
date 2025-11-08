package com.agilemorph.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "licenses")
public class License extends PanacheEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    public Provider provider;
    
    @NotBlank
    @Column(name = "license_number", nullable = false)
    public String licenseNumber;
    
    @NotBlank
    @Column(name = "state", nullable = false)
    public String state;
    
    @NotBlank
    @Column(name = "license_type", nullable = false)
    public String licenseType;
    
    @NotNull
    @Column(name = "issue_date", nullable = false)
    public LocalDate issueDate;
    
    @NotNull
    @Column(name = "expiry_date", nullable = false)
    public LocalDate expiryDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    public LicenseStatus status = LicenseStatus.ACTIVE;
    
    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    public LocalDateTime updatedAt;
    
    public enum LicenseStatus {
        ACTIVE, EXPIRED, SUSPENDED, REVOKED
    }
    
    public boolean isExpired() {
        return LocalDate.now().isAfter(expiryDate);
    }
    
    public boolean isExpiringSoon(int daysThreshold) {
        return LocalDate.now().plusDays(daysThreshold).isAfter(expiryDate) && !isExpired();
    }
    
    public long getDaysUntilExpiry() {
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
