package com.agilemorph.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "audit_logs_seq")
    @SequenceGenerator(name = "audit_logs_seq", sequenceName = "audit_logs_seq", allocationSize = 1)
    public Long id;

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    public Provider provider;
    
    @NotBlank
    @Column(name = "action", nullable = false)
    public String action;
    
    @Column(name = "details", columnDefinition = "TEXT")
    public String details;
    
    @NotNull
    @Column(name = "timestamp", nullable = false)
    public LocalDateTime timestamp;
    
    @Column(name = "user_id")
    public String userId;
    
    @Column(name = "ip_address")
    public String ipAddress;
    
    public AuditLog() {
        this.timestamp = LocalDateTime.now();
    }
}
