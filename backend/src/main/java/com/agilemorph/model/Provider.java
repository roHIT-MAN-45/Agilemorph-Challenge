package com.agilemorph.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "providers")
public class Provider extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "providers_seq")
    @SequenceGenerator(name = "providers_seq", sequenceName = "providers_seq", allocationSize = 1)
    public Long id;
    
    @NotBlank
    @Column(name = "npi", unique = true, nullable = false)
    public String npi;
    
    @NotBlank
    @Column(name = "first_name", nullable = false)
    public String firstName;
    
    @NotBlank
    @Column(name = "last_name", nullable = false)
    public String lastName;
    
    @Column(name = "middle_name")
    public String middleName;
    
    @Email
    @Column(name = "email")
    public String email;
    
    @Column(name = "phone")
    public String phone;
    
    @NotNull
    @Column(name = "date_of_birth", nullable = false)
    public LocalDate dateOfBirth;
    
    @Column(name = "specialty")
    public String specialty;
    
    @Column(name = "taxonomy_code")
    public String taxonomyCode;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false)
    public VerificationStatus verificationStatus = VerificationStatus.PENDING;
    
    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    public LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<License> licenses = new ArrayList<>();
    
    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<PracticeLocation> practiceLocations = new ArrayList<>();
    
    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<AuditLog> auditLogs = new ArrayList<>();
    
    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<RuleEvaluation> ruleEvaluations = new ArrayList<>();
    
    public enum VerificationStatus {
        PENDING, VERIFIED, FLAGGED, REJECTED
    }
    
    // Helper methods
    public String getFullName() {
        if (middleName != null && !middleName.trim().isEmpty()) {
            return firstName + " " + middleName + " " + lastName;
        }
        return firstName + " " + lastName;
    }
    
    public boolean hasExpiredLicense() {
        return licenses.stream().anyMatch(license -> license.isExpired());
    }
    
    public boolean hasValidLicense() {
        return licenses.stream().anyMatch(license -> !license.isExpired());
    }
    
    public void addAuditLog(String action, String details) {
        AuditLog auditLog = new AuditLog();
        auditLog.provider = this;
        auditLog.action = action;
        auditLog.details = details;
        auditLog.timestamp = LocalDateTime.now();
        this.auditLogs.add(auditLog);
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
