package com.agilemorph.resource;

import com.agilemorph.dto.ProviderDto;
import com.agilemorph.model.License;
import com.agilemorph.model.Provider;
import com.agilemorph.service.ProviderService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/api/seed")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Seed Data", description = "Operations for seeding test data")
public class SeedResource {
    
    @Inject
    ProviderService providerService;
    
    @POST
    @Path("/providers")
    @Operation(summary = "Seed providers", description = "Creates sample provider data for testing")
    @Transactional
    public Response seedProviders() {
        try {
            List<ProviderDto> existingProviders = providerService.getAllProviders();

            // If already seeded, return gracefully
            if (existingProviders != null && !existingProviders.isEmpty()) {
                return Response.ok(Map.of(
                        "message", "Sample providers already exist",
                        "count", existingProviders.size(),
                        "providers", existingProviders
                )).build();
            }

            List<ProviderDto> sampleProviders = createSampleProviders();
            List<ProviderDto> createdProviders = providerService.createProvidersBulk(sampleProviders);
            
            return Response.ok(Map.of(
                "message", "Sample providers created successfully",
                "count", createdProviders.size(),
                "providers", createdProviders
            )).build();
            
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
    
    private List<ProviderDto> createSampleProviders() {
        List<ProviderDto> providers = new ArrayList<>();
        
        // Provider 1 - Valid provider with active license
        ProviderDto provider1 = new ProviderDto();
        provider1.npi = "1234567890";
        provider1.firstName = "John";
        provider1.lastName = "Smith";
        provider1.middleName = "Michael";
        provider1.email = "john.smith@example.com";
        provider1.phone = "555-123-4567";
        provider1.dateOfBirth = LocalDate.of(1980, 5, 15);
        provider1.specialty = "Internal Medicine";
        provider1.taxonomyCode = "207R00000X";
        provider1.verificationStatus = Provider.VerificationStatus.VERIFIED;
        
        // Add license
        ProviderDto.LicenseDto license1 = new ProviderDto.LicenseDto();
        license1.licenseNumber = "MD123456";
        license1.state = "CA";
        license1.licenseType = "Medical Doctor";
        license1.issueDate = LocalDate.of(2010, 6, 1);
        license1.expiryDate = LocalDate.of(2025, 6, 1);
        license1.status = License.LicenseStatus.ACTIVE;
        provider1.licenses = List.of(license1);
        
        // Add practice location
        ProviderDto.PracticeLocationDto location1 = new ProviderDto.PracticeLocationDto();
        location1.name = "Smith Medical Center";
        location1.addressLine1 = "123 Main St";
        location1.city = "San Francisco";
        location1.state = "CA";
        location1.zipCode = "94102";
        location1.phone = "555-123-4567";
        location1.taxonomyCode = "207R00000X";
        location1.isPrimary = true;
        provider1.practiceLocations = List.of(location1);
        
        providers.add(provider1);
        
        // Provider 2 - Provider with expired license (should be flagged)
        ProviderDto provider2 = new ProviderDto();
        provider2.npi = "2345678901";
        provider2.firstName = "Jane";
        provider2.lastName = "Doe";
        provider2.email = "jane.doe@example.com";
        provider2.phone = "555-234-5678";
        provider2.dateOfBirth = LocalDate.of(1975, 8, 22);
        provider2.specialty = "Cardiology";
        provider2.taxonomyCode = "207RC0000X";
        provider2.verificationStatus = Provider.VerificationStatus.PENDING;
        
        // Add expired license
        ProviderDto.LicenseDto license2 = new ProviderDto.LicenseDto();
        license2.licenseNumber = "MD234567";
        license2.state = "NY";
        license2.licenseType = "Medical Doctor";
        license2.issueDate = LocalDate.of(2015, 3, 1);
        license2.expiryDate = LocalDate.of(2020, 3, 1); // Expired
        license2.status = License.LicenseStatus.EXPIRED;
        provider2.licenses = List.of(license2);
        
        // Add practice location
        ProviderDto.PracticeLocationDto location2 = new ProviderDto.PracticeLocationDto();
        location2.name = "Doe Cardiology Clinic";
        location2.addressLine1 = "456 Oak Ave";
        location2.city = "New York";
        location2.state = "NY";
        location2.zipCode = "10001";
        location2.phone = "555-234-5678";
        location2.taxonomyCode = "207RC0000X";
        location2.isPrimary = true;
        provider2.practiceLocations = List.of(location2);
        
        providers.add(provider2);
        
        // Provider 3 - Provider with multiple locations (potential duplicate scenario)
        ProviderDto provider3 = new ProviderDto();
        provider3.npi = "3456789012";
        provider3.firstName = "Robert";
        provider3.lastName = "Johnson";
        provider3.email = "robert.johnson@example.com";
        provider3.phone = "555-345-6789";
        provider3.dateOfBirth = LocalDate.of(1982, 12, 10);
        provider3.specialty = "Family Medicine";
        provider3.taxonomyCode = "207Q00000X";
        provider3.verificationStatus = Provider.VerificationStatus.VERIFIED;
        
        // Add license
        ProviderDto.LicenseDto license3 = new ProviderDto.LicenseDto();
        license3.licenseNumber = "MD345678";
        license3.state = "TX";
        license3.licenseType = "Medical Doctor";
        license3.issueDate = LocalDate.of(2012, 1, 15);
        license3.expiryDate = LocalDate.of(2026, 1, 15);
        license3.status = License.LicenseStatus.ACTIVE;
        provider3.licenses = List.of(license3);
        
        // Add multiple practice locations
        ProviderDto.PracticeLocationDto location3a = new ProviderDto.PracticeLocationDto();
        location3a.name = "Johnson Family Medicine";
        location3a.addressLine1 = "789 Pine St";
        location3a.city = "Houston";
        location3a.state = "TX";
        location3a.zipCode = "77001";
        location3a.phone = "555-345-6789";
        location3a.taxonomyCode = "207Q00000X";
        location3a.isPrimary = true;
        
        ProviderDto.PracticeLocationDto location3b = new ProviderDto.PracticeLocationDto();
        location3b.name = "Johnson Family Medicine - Branch";
        location3b.addressLine1 = "321 Elm St";
        location3b.city = "Austin";
        location3b.state = "TX";
        location3b.zipCode = "73301";
        location3b.phone = "555-345-6790";
        location3b.taxonomyCode = "207Q00000X";
        location3b.isPrimary = false;
        
        provider3.practiceLocations = List.of(location3a, location3b);
        
        providers.add(provider3);
        
        return providers;
    }
}
