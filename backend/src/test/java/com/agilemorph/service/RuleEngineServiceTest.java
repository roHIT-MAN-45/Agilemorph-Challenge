package com.agilemorph.service;

import com.agilemorph.dto.ProviderDto;
import com.agilemorph.dto.RuleEvaluationRequest;
import com.agilemorph.dto.RuleEvaluationResponse;
import com.agilemorph.model.License;
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
public class RuleEngineServiceTest {
    
    @Inject
    RuleEngineService ruleEngineService;
    
    @Inject
    ProviderService providerService;
    
    private ProviderDto providerWithExpiredLicense;
    private ProviderDto providerWithValidLicense;
    
    @BeforeEach
    void setUp() {
        // Provider with expired license (should trigger license expiry rule)
        providerWithExpiredLicense = new ProviderDto();
        providerWithExpiredLicense.npi = "1234567890";
        providerWithExpiredLicense.firstName = "John";
        providerWithExpiredLicense.lastName = "Smith";
        providerWithExpiredLicense.dateOfBirth = LocalDate.of(1980, 5, 15);
        providerWithExpiredLicense.email = "john.smith@example.com";
        
        ProviderDto.LicenseDto expiredLicense = new ProviderDto.LicenseDto();
        expiredLicense.licenseNumber = "MD123456";
        expiredLicense.state = "CA";
        expiredLicense.licenseType = "Medical Doctor";
        expiredLicense.issueDate = LocalDate.now().minusYears(5);
        expiredLicense.expiryDate = LocalDate.now().minusDays(1);
        expiredLicense.status = License.LicenseStatus.EXPIRED;
        expiredLicense.expired = true;
        expiredLicense.expiringSoon = false;
        expiredLicense.daysUntilExpiry = -1; // clearly expired
        
        providerWithExpiredLicense.licenses = List.of(expiredLicense);
        
        // Provider with valid license (should not trigger license expiry rule)
        providerWithValidLicense = new ProviderDto();
        providerWithValidLicense.npi = "2345678901";
        providerWithValidLicense.firstName = "Jane";
        providerWithValidLicense.lastName = "Doe";
        providerWithValidLicense.dateOfBirth = LocalDate.of(1975, 8, 22);
        providerWithValidLicense.email = "jane.doe@example.com";
        
        ProviderDto.LicenseDto validLicense = new ProviderDto.LicenseDto();
        validLicense.licenseNumber = "MD234567";
        validLicense.state = "NY";
        validLicense.licenseType = "Medical Doctor";
        validLicense.issueDate = LocalDate.of(2015, 3, 1);
        validLicense.expiryDate = LocalDate.of(2025, 3, 1); // Valid
        validLicense.status = License.LicenseStatus.ACTIVE;
        validLicense.expired = false;
        validLicense.expiringSoon = false;
        validLicense.daysUntilExpiry = 365; // Valid for 1 year
        
        providerWithValidLicense.licenses = List.of(validLicense);
    }
    
    @Test
    void testRuleEngineInitialization() {
        ruleEngineService.initializeRuleEngine();
        
        assertTrue(ruleEngineService.isRuleEngineInitialized());
        
        List<String> loadedRules = ruleEngineService.getLoadedRules();
        assertFalse(loadedRules.isEmpty());
        assertTrue(loadedRules.contains("license-expiry-rule"));
        assertTrue(loadedRules.contains("duplicate-detection-rule"));
    }
    
    @Test
    void testEvaluateRulesWithExpiredLicense() {
        ruleEngineService.initializeRuleEngine();
        
        RuleEvaluationRequest request = new RuleEvaluationRequest();
        request.provider = providerWithExpiredLicense;
        request.includeFacts = true;
        
        RuleEvaluationResponse response = ruleEngineService.evaluateRules(request);
        
        assertTrue(response.success);
        assertNotNull(response.results);
        assertFalse(response.results.isEmpty());
        
        // Should have triggered license expiry rule
        boolean licenseRuleTriggered = response.results.stream()
            .anyMatch(result -> result.ruleName.equals("license-expiry-rule") && result.triggered);
        
        assertTrue(licenseRuleTriggered, "License expiry rule should have been triggered for expired license");
    }
    
    @Test
    void testEvaluateRulesWithValidLicense() {
        ruleEngineService.initializeRuleEngine();
        
        RuleEvaluationRequest request = new RuleEvaluationRequest();
        request.provider = providerWithValidLicense;
        request.includeFacts = true;
        
        RuleEvaluationResponse response = ruleEngineService.evaluateRules(request);
        
        assertTrue(response.success);
        assertNotNull(response.results);
        
        // Should not have triggered license expiry rule
        boolean licenseRuleTriggered = response.results.stream()
            .anyMatch(result -> result.ruleName.equals("license-expiry-rule") && result.triggered);
        
        assertFalse(licenseRuleTriggered, "License expiry rule should not have been triggered for valid license");
    }
    
    @Test
    void testEvaluateRulesForProviderById() {
        // Create provider in database
        ProviderDto createdProvider = providerService.createProvider(providerWithExpiredLicense);
        
        ruleEngineService.initializeRuleEngine();
        
        RuleEvaluationResponse response = ruleEngineService.evaluateRulesForProvider(createdProvider.id);
        
        assertTrue(response.success);
        assertNotNull(response.results);
        
        // Should have triggered license expiry rule
        boolean licenseRuleTriggered = response.results.stream()
            .anyMatch(result -> result.ruleName.equals("license-expiry-rule") && result.triggered);
        
        assertTrue(licenseRuleTriggered, "License expiry rule should have been triggered for provider with expired license");
    }
    
    @Test
    void testRuleEngineStatus() {
        ruleEngineService = new RuleEngineService();
    
        assertFalse(ruleEngineService.isRuleEngineInitialized());
        
        ruleEngineService.initializeRuleEngine();
        
        assertTrue(ruleEngineService.isRuleEngineInitialized());
    }
    
    @Test
    void testGetLoadedRules() {
        List<String> rules = ruleEngineService.getLoadedRules();
        
        assertNotNull(rules);
        assertFalse(rules.isEmpty());
        assertTrue(rules.contains("license-expiry-rule"));
        assertTrue(rules.contains("duplicate-detection-rule"));
    }
}
