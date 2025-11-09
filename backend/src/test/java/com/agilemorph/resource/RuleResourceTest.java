package com.agilemorph.resource;

import com.agilemorph.dto.ProviderDto;
import com.agilemorph.dto.RuleEvaluationRequest;
import com.agilemorph.dto.RuleEvaluationResponse;
import com.agilemorph.model.License;
import org.flywaydb.core.Flyway;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RuleResourceTest {
    
    private ProviderDto providerWithExpiredLicense;
    private ProviderDto providerWithValidLicense;

    @Inject
    Flyway flyway;

    @BeforeAll
    void init() {
        flyway.clean();
        flyway.migrate();
    }
    
    @BeforeEach
    void setUp() {
        // Provider with expired license (should trigger license expiry rule)
        providerWithExpiredLicense = new ProviderDto();
        providerWithExpiredLicense.npi = String.format("%010d", System.nanoTime() % 1_000_000_0000L);
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

        System.out.println("Creating provider with NPI: " + providerWithExpiredLicense.npi);

    }
    
    @Test
    void testEvaluateRulesWithExpiredLicense() {
        RuleEvaluationRequest request = new RuleEvaluationRequest();
        request.provider = providerWithExpiredLicense;
        request.includeFacts = true;

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/rules/evaluate")
        .then()
            .statusCode(200)
            .body("success", equalTo(true))
            .body("metadata.rulesFired", greaterThan(0))
            .body("metadata.facts.size()", greaterThan(0))
            .body("results", notNullValue())
            .body("results.size()", greaterThan(0))
            .body("metadata.facts.ruleName", hasItem("license-expiry-rule"));
    }
    
    @Test
    void testEvaluateRulesWithValidLicense() {
        RuleEvaluationRequest request = new RuleEvaluationRequest();
        request.provider = providerWithValidLicense;
        request.includeFacts = true;
        
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/rules/evaluate")
        .then()
            .statusCode(200)
            .body("success", equalTo(true))
            .body("results", notNullValue());
    }
    
    @Test
    void testEvaluateRulesForProviderById() {
        var response = given()
            .contentType(ContentType.JSON)
            .body(providerWithExpiredLicense)
        .when()
            .post("/api/providers")
        .then()
            .log().all()
            .statusCode(anyOf(is(201), is(409)))
            .extract();

        ProviderDto createdProvider = response.statusCode() == 201
            ? response.as(ProviderDto.class)
            : response.jsonPath().getList("duplicates", ProviderDto.class).get(0);

        given()
            .contentType(ContentType.JSON)
        .when()
            .post("/api/rules/evaluate/" + createdProvider.id)
        .then()
            .statusCode(200)
            .body("success", equalTo(true))
            .body("results", notNullValue());
    }
    
    @Test
    void testGetLoadedRules() {
        given()
        .when()
            .get("/api/rules")
        .then()
            .statusCode(200)
            .body("rules", notNullValue())
            .body("count", greaterThan(0))
            .body("rules", hasItem("license-expiry-rule"))
            .body("rules", hasItem("duplicate-detection-rule"));
    }
    
    @Test
    void testGetRuleEngineStatus() {
        given()
        .when()
            .get("/api/rules/status")
        .then()
            .statusCode(200)
            .body("initialized", notNullValue());
    }
    
    @Test
    void testEvaluateRulesWithInvalidRequest() {
        // Test with null provider
        RuleEvaluationRequest invalidRequest = new RuleEvaluationRequest();
        invalidRequest.provider = null;
        
        given()
            .contentType(ContentType.JSON)
            .body(invalidRequest)
        .when()
            .post("/api/rules/evaluate")
        .then()
            .statusCode(400);
    }
    
    @Test
    void testEvaluateRulesForNonExistentProvider() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .post("/api/rules/evaluate/99999")
        .then()
            .statusCode(500); // Should return 500 due to exception in service
    }
}
