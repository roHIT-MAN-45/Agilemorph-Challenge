package com.agilemorph.dto;

import com.agilemorph.model.License;
import com.agilemorph.model.Provider;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public class ProviderDto {
    
    public Long id;
    
    @NotBlank
    public String npi;
    
    @NotBlank
    public String firstName;
    
    @NotBlank
    public String lastName;
    
    public String middleName;
    
    @Email
    public String email;
    
    public String phone;
    
    @NotNull
    public LocalDate dateOfBirth;
    
    public String specialty;
    
    public String taxonomyCode;
    
    public Provider.VerificationStatus verificationStatus;
    
    public List<LicenseDto> licenses;
    
    public List<PracticeLocationDto> practiceLocations;
    
    public List<RuleEvaluationDto> ruleEvaluations;
    
    public static class LicenseDto {
        public Long id;
        public String licenseNumber;
        public String state;
        public String licenseType;
        public LocalDate issueDate;
        public LocalDate expiryDate;
        public License.LicenseStatus status;
        public boolean expired;
        public boolean expiringSoon;
        public long daysUntilExpiry;
    }
    
    public static class PracticeLocationDto {
        public Long id;
        public String name;
        public String addressLine1;
        public String addressLine2;
        public String city;
        public String state;
        public String zipCode;
        public String phone;
        public String taxonomyCode;
        public boolean isPrimary;
        public String fullAddress;
    }
    
    public static class RuleEvaluationDto {
        public Long id;
        public String ruleName;
        public boolean triggered;
        public String severity;
        public String message;
        public String metadata;
        public LocalDate evaluatedAt;
        public List<String> facts;
    }
}
