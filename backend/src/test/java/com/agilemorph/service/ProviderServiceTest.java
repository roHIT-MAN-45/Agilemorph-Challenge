package com.agilemorph.service;

import com.agilemorph.dto.ProviderDto;
import com.agilemorph.model.Provider;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@Transactional
public class ProviderServiceTest {
    
    @Inject
    ProviderService providerService;
    
    private ProviderDto sampleProvider;
    
    @BeforeEach
    void setUp() {
        sampleProvider = new ProviderDto();
        sampleProvider.npi = "1234567890";
        sampleProvider.firstName = "John";
        sampleProvider.lastName = "Smith";
        sampleProvider.middleName = "Michael";
        sampleProvider.email = "john.smith@example.com";
        sampleProvider.phone = "555-123-4567";
        sampleProvider.dateOfBirth = LocalDate.of(1980, 5, 15);
        sampleProvider.specialty = "Internal Medicine";
        sampleProvider.taxonomyCode = "207R00000X";
        sampleProvider.verificationStatus = Provider.VerificationStatus.PENDING;
    }
    
    @Test
    void testCreateProvider() {
        ProviderDto createdProvider = providerService.createProvider(sampleProvider);
        
        assertNotNull(createdProvider);
        assertNotNull(createdProvider.id);
        assertEquals(sampleProvider.npi, createdProvider.npi);
        assertEquals(sampleProvider.firstName, createdProvider.firstName);
        assertEquals(sampleProvider.lastName, createdProvider.lastName);
        assertEquals(sampleProvider.email, createdProvider.email);
    }
    
    @Test
    void testGetProvider() {
        ProviderDto createdProvider = providerService.createProvider(sampleProvider);
        ProviderDto retrievedProvider = providerService.getProvider(createdProvider.id);
        
        assertNotNull(retrievedProvider);
        assertEquals(createdProvider.id, retrievedProvider.id);
        assertEquals(createdProvider.npi, retrievedProvider.npi);
    }
    
    @Test
    void testGetProviderByNpi() {
        ProviderDto createdProvider = providerService.createProvider(sampleProvider);
        ProviderDto retrievedProvider = providerService.getProviderByNpi(createdProvider.npi);
        
        assertNotNull(retrievedProvider);
        assertEquals(createdProvider.id, retrievedProvider.id);
        assertEquals(createdProvider.npi, retrievedProvider.npi);
    }
    
    @Test
    void testUpdateProvider() {
        ProviderDto createdProvider = providerService.createProvider(sampleProvider);
        
        createdProvider.firstName = "Jane";
        createdProvider.lastName = "Doe";
        createdProvider.email = "jane.doe@example.com";
        
        ProviderDto updatedProvider = providerService.updateProvider(createdProvider.id, createdProvider);
        
        assertEquals("Jane", updatedProvider.firstName);
        assertEquals("Doe", updatedProvider.lastName);
        assertEquals("jane.doe@example.com", updatedProvider.email);
    }
    
    @Test
    void testDeleteProvider() {
        ProviderDto createdProvider = providerService.createProvider(sampleProvider);
        Long providerId = createdProvider.id;
        
        providerService.deleteProvider(providerId);
        
        assertThrows(jakarta.ws.rs.NotFoundException.class, () -> {
            providerService.getProvider(providerId);
        });
    }
    
    @Test
    void testNormalizeProvider() {
        ProviderDto providerWithSpaces = new ProviderDto();
        providerWithSpaces.firstName = "  John  ";
        providerWithSpaces.lastName = "  Smith  ";
        providerWithSpaces.npi = "123-456-7890";
        
        ProviderDto normalizedProvider = providerService.normalizeProvider(providerWithSpaces);
        
        assertEquals("John", normalizedProvider.firstName);
        assertEquals("Smith", normalizedProvider.lastName);
        assertEquals("1234567890", normalizedProvider.npi);
    }
    
    @Test
    void testFindPotentialDuplicates() {
        // Create first provider
        ProviderDto provider1 = providerService.createProvider(sampleProvider);
        
        // Create second provider with same name and DOB
        ProviderDto duplicateProvider = new ProviderDto();
        duplicateProvider.npi = "2345678901";
        duplicateProvider.firstName = "John";
        duplicateProvider.lastName = "Smith";
        duplicateProvider.dateOfBirth = LocalDate.of(1980, 5, 15);
        duplicateProvider.email = "john.smith.duplicate@example.com";
        
        List<ProviderDto> duplicates = providerService.findPotentialDuplicates(duplicateProvider);
        
        assertFalse(duplicates.isEmpty());
        assertEquals(1, duplicates.size());
        assertEquals(provider1.id, duplicates.get(0).id);
    }
    
    @Test
    void testGetAllProviders() {
        providerService.createProvider(sampleProvider);
        
        List<ProviderDto> providers = providerService.getAllProviders();
        
        assertFalse(providers.isEmpty());
        assertTrue(providers.stream().anyMatch(p -> p.npi.equals(sampleProvider.npi)));
    }
    
    @Test
    void testGetProvidersByStatus() {
        ProviderDto createdProvider = providerService.createProvider(sampleProvider);
        
        List<ProviderDto> pendingProviders = providerService.getProvidersByStatus(Provider.VerificationStatus.PENDING);
        
        assertFalse(pendingProviders.isEmpty());
        assertTrue(pendingProviders.stream().anyMatch(p -> p.id.equals(createdProvider.id)));
    }
}
