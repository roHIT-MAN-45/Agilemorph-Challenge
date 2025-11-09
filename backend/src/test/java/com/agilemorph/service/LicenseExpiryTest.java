package com.agilemorph.service;

import com.agilemorph.model.License;
import org.flywaydb.core.Flyway;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LicenseExpiryTest {

    @Inject
    Flyway flyway;

    @BeforeAll
    void init() {
        flyway.clean();
        flyway.migrate();
    }
    
    @Test
    void testLicenseExpiryCalculation() {
        // Create a license that should be expired
        License license = new License();
        license.licenseNumber = "MD123456";
        license.state = "CA";
        license.licenseType = "Medical Doctor";
        license.issueDate = LocalDate.of(2010, 6, 1);
        license.expiryDate = LocalDate.of(2020, 6, 1); // Expired 4 years ago
        
        // This test should pass - the license should be expired
        assertTrue(license.isExpired(), "License should be expired as it expired in 2020");
        
        // Test with a valid license
        License validLicense = new License();
        validLicense.licenseNumber = "MD234567";
        validLicense.state = "NY";
        validLicense.licenseType = "Medical Doctor";
        validLicense.issueDate = LocalDate.of(2015, 3, 1);
        validLicense.expiryDate = LocalDate.of(2026, 3, 1); // Valid for 1 more year
        
        // This test should pass - the license should not be expired
        assertFalse(validLicense.isExpired(), "License should not be expired as it's valid until 2025");
    }
    
    @Test
    void testLicenseExpiringSoon() {
        // Create a license expiring in 25 days
        License expiringLicense = new License();
        expiringLicense.licenseNumber = "MD345678";
        expiringLicense.state = "TX";
        expiringLicense.licenseType = "Medical Doctor";
        expiringLicense.issueDate = LocalDate.of(2020, 1, 1);
        expiringLicense.expiryDate = LocalDate.now().plusDays(25); // Expiring in 25 days
        
        // This test should pass - the license should be expiring soon
        assertTrue(expiringLicense.isExpiringSoon(30), "License should be expiring soon");
        
        // Test with a license expiring in 60 days
        License notExpiringLicense = new License();
        notExpiringLicense.licenseNumber = "MD456789";
        notExpiringLicense.state = "FL";
        notExpiringLicense.licenseType = "Medical Doctor";
        notExpiringLicense.issueDate = LocalDate.of(2020, 1, 1);
        notExpiringLicense.expiryDate = LocalDate.now().plusDays(60); // Expiring in 60 days
        
        // This test should pass - the license should not be expiring soon
        assertFalse(notExpiringLicense.isExpiringSoon(30), "License should not be expiring soon");
    }
    
    @Test
    void testLicenseDaysUntilExpiry() {
        // Create a license expiring in 100 days
        License license = new License();
        license.licenseNumber = "MD567890";
        license.state = "CA";
        license.licenseType = "Medical Doctor";
        license.issueDate = LocalDate.of(2020, 1, 1);
        license.expiryDate = LocalDate.now().plusDays(100);
        
        // This test should pass - the license should have 100 days until expiry
        assertEquals(100, license.getDaysUntilExpiry(), "License should have 100 days until expiry");
        
        // Test with an expired license
        License expiredLicense = new License();
        expiredLicense.licenseNumber = "MD678901";
        expiredLicense.state = "NY";
        expiredLicense.licenseType = "Medical Doctor";
        expiredLicense.issueDate = LocalDate.of(2010, 1, 1);
        expiredLicense.expiryDate = LocalDate.of(2020, 1, 1); // Expired 4 years ago
        
        // This test should pass - the expired license should have negative days
        assertTrue(expiredLicense.getDaysUntilExpiry() < 0, "Expired license should have negative days until expiry");
    }
}
