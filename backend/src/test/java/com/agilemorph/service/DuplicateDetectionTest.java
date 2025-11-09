package com.agilemorph.service;

import com.agilemorph.dto.ProviderDto;
import com.agilemorph.model.Provider;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@Transactional
public class DuplicateDetectionTest {
    
    @Inject
    ProviderService providerService;
    
    private ProviderDto baseProvider;

    // Clear provider data before each test
    @BeforeEach
    @Transactional
    void resetDB() {
        // delete dependent audit logs first, then providers
        io.quarkus.hibernate.orm.panache.Panache.getEntityManager()
            .createQuery("DELETE FROM AuditLog").executeUpdate();
        Provider.deleteAll();
    }
    
    @BeforeEach
    void setUp() {
        baseProvider = new ProviderDto();
        baseProvider.npi = String.format("%010d", System.nanoTime() % 1_000_000_0000L);
        baseProvider.firstName = "John";
        baseProvider.lastName = "Smith";
        baseProvider.dateOfBirth = LocalDate.of(1980, 5, 15);
        baseProvider.email = "john.smith@example.com";
        baseProvider.verificationStatus = Provider.VerificationStatus.PENDING;
    }
    
    @Test
    void testExactDuplicateDetection() {
        // Create the base provider
        ProviderDto createdProvider = providerService.createProvider(baseProvider);
        
        // Create a provider with the same name and DOB
        ProviderDto duplicateProvider = new ProviderDto();
        duplicateProvider.npi = "2345678901";
        duplicateProvider.firstName = "John";
        duplicateProvider.lastName = "Smith";
        duplicateProvider.dateOfBirth = LocalDate.of(1980, 5, 15);
        duplicateProvider.email = "john.smith.duplicate@example.com";
        
        // Find potential duplicates
        List<ProviderDto> duplicates = providerService.findPotentialDuplicates(duplicateProvider);
        
        // This test should pass - should find the duplicate
        assertFalse(duplicates.isEmpty(), "Should find potential duplicate for exact name match");
        assertEquals(1, duplicates.size(), "Should find exactly one duplicate");
        assertEquals(createdProvider.id, duplicates.get(0).id, "Should find the original provider as duplicate");
    }
    
    @Test
    void testDuplicateWithTrailingSpaces() {
        // Create the base provider
        ProviderDto createdProvider = providerService.createProvider(baseProvider);
        
        // Create a provider with trailing spaces in the name
        ProviderDto duplicateProvider = new ProviderDto();
        duplicateProvider.npi = "2345678901";
        duplicateProvider.firstName = "John "; // Note the trailing space
        duplicateProvider.lastName = "Smith "; // Note the trailing space
        duplicateProvider.dateOfBirth = LocalDate.of(1980, 5, 15);
        duplicateProvider.email = "john.smith.duplicate@example.com";
        
        // Find potential duplicates
        List<ProviderDto> duplicates = providerService.findPotentialDuplicates(duplicateProvider);
        
        // This test should pass - should find the duplicate despite trailing spaces
        assertFalse(duplicates.isEmpty(), "Should find potential duplicate despite trailing spaces");
        assertEquals(1, duplicates.size(), "Should find exactly one duplicate");
        assertEquals(createdProvider.id, duplicates.get(0).id, "Should find the original provider as duplicate");
    }
    
    @Test
    void testDuplicateWithLeadingSpaces() {
        // Create the base provider
        ProviderDto createdProvider = providerService.createProvider(baseProvider);
        
        // Create a provider with leading spaces in the name
        ProviderDto duplicateProvider = new ProviderDto();
        duplicateProvider.npi = "2345678901";
        duplicateProvider.firstName = " John"; // Note the leading space
        duplicateProvider.lastName = " Smith"; // Note the leading space
        duplicateProvider.dateOfBirth = LocalDate.of(1980, 5, 15);
        duplicateProvider.email = "john.smith.duplicate@example.com";
        
        // Find potential duplicates
        List<ProviderDto> duplicates = providerService.findPotentialDuplicates(duplicateProvider);
        
        // This test should pass - should find the duplicate despite leading spaces
        assertFalse(duplicates.isEmpty(), "Should find potential duplicate despite leading spaces");
        assertEquals(1, duplicates.size(), "Should find exactly one duplicate");
        assertEquals(createdProvider.id, duplicates.get(0).id, "Should find the original provider as duplicate");
    }
    
    @Test
    void testDuplicateWithMultipleSpaces() {
        // Create the base provider
        ProviderDto createdProvider = providerService.createProvider(baseProvider);
        
        // Create a provider with multiple spaces in the name
        ProviderDto duplicateProvider = new ProviderDto();
        duplicateProvider.npi = "2345678901";
        duplicateProvider.firstName = "John  "; // Note the multiple spaces
        duplicateProvider.lastName = "Smith  "; // Note the multiple spaces
        duplicateProvider.dateOfBirth = LocalDate.of(1980, 5, 15);
        duplicateProvider.email = "john.smith.duplicate@example.com";
        
        // Find potential duplicates
        List<ProviderDto> duplicates = providerService.findPotentialDuplicates(duplicateProvider);
        
        // This test should pass - should find the duplicate despite multiple spaces
        assertFalse(duplicates.isEmpty(), "Should find potential duplicate despite multiple spaces");
        assertEquals(1, duplicates.size(), "Should find exactly one duplicate");
        assertEquals(createdProvider.id, duplicates.get(0).id, "Should find the original provider as duplicate");
    }
    
    @Test
    void testNoDuplicateForDifferentName() {
        // Create the base provider
        providerService.createProvider(baseProvider);
        
        // Create a provider with a different name
        ProviderDto differentProvider = new ProviderDto();
        differentProvider.npi = "2345678901";
        differentProvider.firstName = "Jane";
        differentProvider.lastName = "Doe";
        differentProvider.dateOfBirth = LocalDate.of(1980, 5, 15);
        differentProvider.email = "jane.doe@example.com";
        
        // Find potential duplicates
        List<ProviderDto> duplicates = providerService.findPotentialDuplicates(differentProvider);
        
        // This test should pass - should not find any duplicates
        assertTrue(duplicates.isEmpty(), "Should not find duplicates for different names");
    }
    
    @Test
    void testNoDuplicateForDifferentDOB() {
        // Create the base provider
        providerService.createProvider(baseProvider);
        
        // Create a provider with the same name but different DOB
        ProviderDto differentProvider = new ProviderDto();
        differentProvider.npi = "2345678901";
        differentProvider.firstName = "John";
        differentProvider.lastName = "Smith";
        differentProvider.dateOfBirth = LocalDate.of(1985, 5, 15); // Different DOB
        differentProvider.email = "john.smith.different@example.com";
        
        // Find potential duplicates
        List<ProviderDto> duplicates = providerService.findPotentialDuplicates(differentProvider);
        
        // This test should pass - should not find any duplicates
        assertTrue(duplicates.isEmpty(), "Should not find duplicates for different DOB");
    }
}
