package com.agilemorph.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "practice_locations")
public class PracticeLocation extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "practice_locations_seq")
    @SequenceGenerator(name = "practice_locations_seq", sequenceName = "practice_locations_seq", allocationSize = 1)
    public Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    public Provider provider;
    
    @NotBlank
    @Column(name = "name", nullable = false)
    public String name;
    
    @NotBlank
    @Column(name = "address_line1", nullable = false)
    public String addressLine1;
    
    @Column(name = "address_line2")
    public String addressLine2;
    
    @NotBlank
    @Column(name = "city", nullable = false)
    public String city;
    
    @NotBlank
    @Column(name = "state", nullable = false)
    public String state;
    
    @NotBlank
    @Column(name = "zip_code", nullable = false)
    public String zipCode;
    
    @Column(name = "phone")
    public String phone;
    
    @Column(name = "taxonomy_code")
    public String taxonomyCode;
    
    @Column(name = "is_primary", nullable = false)
    public Boolean isPrimary = false;
    
    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    public LocalDateTime updatedAt;
    
    public String getFullAddress() {
        StringBuilder address = new StringBuilder(addressLine1);
        if (addressLine2 != null && !addressLine2.trim().isEmpty()) {
            address.append(", ").append(addressLine2);
        }
        address.append(", ").append(city).append(", ").append(state).append(" ").append(zipCode);
        return address.toString();
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
