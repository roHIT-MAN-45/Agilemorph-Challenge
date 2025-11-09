package com.agilemorph.service;

import com.agilemorph.dto.ProviderDto;
import com.agilemorph.model.Provider;
import com.agilemorph.model.License;
import com.agilemorph.model.PracticeLocation;
import com.agilemorph.model.RuleEvaluation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProviderService {
    
    @Transactional
    public ProviderDto createProvider(ProviderDto providerDto) {
        Provider provider = new Provider();
        mapDtoToEntity(providerDto, provider);
        
        provider.persist();
        
        // Add audit log
        provider.addAuditLog("PROVIDER_CREATED", "Provider created with NPI: " + provider.npi);
        
        return mapEntityToDto(provider);
    }
    
    @Transactional
    public List<ProviderDto> createProvidersBulk(List<ProviderDto> providerDtos) {
        return providerDtos.stream()
                .map(this::createProvider)
                .collect(Collectors.toList());
    }
    
    public ProviderDto getProvider(Long id) {
        Provider provider = Provider.findById(id);
        if (provider == null) {
            throw new NotFoundException("Provider not found with id: " + id);
        }
        return mapEntityToDto(provider);
    }
    
    public ProviderDto getProviderByNpi(String npi) {
        Provider provider = Provider.find("npi", npi).firstResult();
        if (provider == null) {
            throw new NotFoundException("Provider not found with NPI: " + npi);
        }
        return mapEntityToDto(provider);
    }
    
    public List<ProviderDto> getAllProviders() {
        List<Provider> providers = Provider.listAll();
        return providers.stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }
    
    public List<ProviderDto> getProvidersByStatus(Provider.VerificationStatus status) {
        List<Provider> providers = Provider.find("verificationStatus", status).list();
        return providers.stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public ProviderDto updateProvider(Long id, ProviderDto providerDto) {
        Provider provider = Provider.findById(id);
        if (provider == null) {
            throw new NotFoundException("Provider not found with id: " + id);
        }
        
        String oldNpi = provider.npi;
        mapDtoToEntity(providerDto, provider);
        
        // Add audit log
        provider.addAuditLog("PROVIDER_UPDATED", 
            "Provider updated. Old NPI: " + oldNpi + ", New NPI: " + provider.npi);
        
        return mapEntityToDto(provider);
    }
    
    @Transactional
    public void deleteProvider(Long id) {
        Provider provider = Provider.findById(id);
        if (provider == null) {
            throw new NotFoundException("Provider not found with id: " + id);
        }
        
        // Add audit log before deletion
        provider.addAuditLog("PROVIDER_DELETED", "Provider deleted with NPI: " + provider.npi);
        
        provider.delete();
    }
    
    @Transactional
    public List<ProviderDto> findPotentialDuplicates(ProviderDto providerDto) {
        // Simple deduplication logic based on name and DOB
        String firstName = providerDto.firstName.toLowerCase().trim();
        String lastName = providerDto.lastName.toLowerCase().trim();
        LocalDate dateOfBirth = providerDto.dateOfBirth;
        
        List<Provider> potentialDuplicates = Provider.find(
            "LOWER(firstName) = ?1 AND LOWER(lastName) = ?2 AND dateOfBirth = ?3",
            firstName, lastName, dateOfBirth
        ).list();
        
        return potentialDuplicates.stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public ProviderDto normalizeProvider(ProviderDto providerDto) {
        // Normalize provider data
        providerDto.firstName = normalizeName(providerDto.firstName);
        providerDto.lastName = normalizeName(providerDto.lastName);
        if (providerDto.middleName != null) {
            providerDto.middleName = normalizeName(providerDto.middleName);
        }
        providerDto.npi = normalizeNpi(providerDto.npi);
        
        return providerDto;
    }
    
    private String normalizeName(String name) {
        if (name == null) return null;
        return name.trim().replaceAll("\\s+", " ");
    }
    
    private String normalizeNpi(String npi) {
        if (npi == null) return null;
        return npi.replaceAll("\\D", ""); // Remove non-digits
    }
    
    private void mapDtoToEntity(ProviderDto dto, Provider entity) {
        entity.npi = dto.npi;
        entity.firstName = dto.firstName;
        entity.lastName = dto.lastName;
        entity.middleName = dto.middleName;
        entity.email = dto.email;
        entity.phone = dto.phone;
        entity.dateOfBirth = dto.dateOfBirth;
        entity.specialty = dto.specialty;
        entity.taxonomyCode = dto.taxonomyCode;
        if (dto.verificationStatus != null) {
            entity.verificationStatus = dto.verificationStatus;
        }
        
        // Handle licenses
        if (dto.licenses != null) {
            entity.licenses.clear();
            for (ProviderDto.LicenseDto licenseDto : dto.licenses) {
                License license = new License();
                license.provider = entity;
                license.licenseNumber = licenseDto.licenseNumber;
                license.state = licenseDto.state;
                license.licenseType = licenseDto.licenseType;
                license.issueDate = licenseDto.issueDate;
                license.expiryDate = licenseDto.expiryDate;
                if (licenseDto.status != null) {
                    license.status = licenseDto.status;
                }
                entity.licenses.add(license);
            }
        }
        
        // Handle practice locations
        if (dto.practiceLocations != null) {
            entity.practiceLocations.clear();
            for (ProviderDto.PracticeLocationDto locationDto : dto.practiceLocations) {
                PracticeLocation location = new PracticeLocation();
                location.provider = entity;
                location.name = locationDto.name;
                location.addressLine1 = locationDto.addressLine1;
                location.addressLine2 = locationDto.addressLine2;
                location.city = locationDto.city;
                location.state = locationDto.state;
                location.zipCode = locationDto.zipCode;
                location.phone = locationDto.phone;
                location.taxonomyCode = locationDto.taxonomyCode;
                location.isPrimary = locationDto.isPrimary;
                entity.practiceLocations.add(location);
            }
        }
    }
    
    private ProviderDto mapEntityToDto(Provider entity) {
        ProviderDto dto = new ProviderDto();
        dto.id = entity.id;
        dto.npi = entity.npi;
        dto.firstName = entity.firstName;
        dto.lastName = entity.lastName;
        dto.middleName = entity.middleName;
        dto.email = entity.email;
        dto.phone = entity.phone;
        dto.dateOfBirth = entity.dateOfBirth;
        dto.specialty = entity.specialty;
        dto.taxonomyCode = entity.taxonomyCode;
        dto.verificationStatus = entity.verificationStatus;
        
        // Map licenses
        dto.licenses = entity.licenses.stream()
                .map(this::mapLicenseToDto)
                .collect(Collectors.toList());
        
        // Map practice locations
        dto.practiceLocations = entity.practiceLocations.stream()
                .map(this::mapLocationToDto)
                .collect(Collectors.toList());
        
        // Map rule evaluations
        dto.ruleEvaluations = entity.ruleEvaluations.stream()
                .map(this::mapRuleEvaluationToDto)
                .collect(Collectors.toList());
        
        return dto;
    }
    
    private ProviderDto.LicenseDto mapLicenseToDto(License license) {
        ProviderDto.LicenseDto dto = new ProviderDto.LicenseDto();
        dto.id = license.id;
        dto.licenseNumber = license.licenseNumber;
        dto.state = license.state;
        dto.licenseType = license.licenseType;
        dto.issueDate = license.issueDate;
        dto.expiryDate = license.expiryDate;
        dto.status = license.status;
        dto.expired = license.isExpired();
        dto.expiringSoon = license.isExpiringSoon(30);
        dto.daysUntilExpiry = license.getDaysUntilExpiry();
        return dto;
    }
    
    private ProviderDto.PracticeLocationDto mapLocationToDto(PracticeLocation location) {
        ProviderDto.PracticeLocationDto dto = new ProviderDto.PracticeLocationDto();
        dto.id = location.id;
        dto.name = location.name;
        dto.addressLine1 = location.addressLine1;
        dto.addressLine2 = location.addressLine2;
        dto.city = location.city;
        dto.state = location.state;
        dto.zipCode = location.zipCode;
        dto.phone = location.phone;
        dto.taxonomyCode = location.taxonomyCode;
        dto.isPrimary = location.isPrimary;
        dto.fullAddress = location.getFullAddress();
        return dto;
    }
    
    private ProviderDto.RuleEvaluationDto mapRuleEvaluationToDto(RuleEvaluation evaluation) {
        ProviderDto.RuleEvaluationDto dto = new ProviderDto.RuleEvaluationDto();
        dto.id = evaluation.id;
        dto.ruleName = evaluation.ruleName;
        dto.triggered = evaluation.triggered;
        dto.severity = evaluation.severity;
        dto.message = evaluation.message;
        dto.metadata = evaluation.metadata;
        dto.evaluatedAt = evaluation.evaluatedAt.toLocalDate();
        
        // Safely initialize lazy-loaded collection
        try {
            dto.facts = (evaluation.facts != null)
                ? evaluation.facts.stream().collect(Collectors.toList())
                : List.of();
        } catch (Exception e) {
            dto.facts = List.of();
        }

        return dto;
    }
}
