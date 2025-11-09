package com.agilemorph.resource;

import com.agilemorph.dto.ProviderDto;
import com.agilemorph.model.Provider;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@Transactional
public class ProviderResourceTest {
    
    private ProviderDto sampleProvider;

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
        sampleProvider = new ProviderDto();
        sampleProvider.npi = String.format("%010d", System.nanoTime() % 1_000_000_0000L);
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
        given()
            .contentType(ContentType.JSON)
            .body(sampleProvider)
        .when()
            .post("/api/providers")
        .then()
            .statusCode(201)
            .body("npi", equalTo(sampleProvider.npi))
            .body("firstName", equalTo(sampleProvider.firstName))
            .body("lastName", equalTo(sampleProvider.lastName))
            .body("email", equalTo(sampleProvider.email))
            .body("id", notNullValue());
    }
    
    @Test
    void testCreateProviderWithInvalidData() {
        ProviderDto invalidProvider = new ProviderDto();
        invalidProvider.npi = ""; // Invalid NPI
        invalidProvider.firstName = ""; // Invalid first name
        
        given()
            .contentType(ContentType.JSON)
            .body(invalidProvider)
        .when()
            .post("/api/providers")
        .then()
            .statusCode(400);
    }
    
    @Test
    void testGetAllProviders() {
        // Create a provider first
        given()
            .contentType(ContentType.JSON)
            .body(sampleProvider)
        .when()
            .post("/api/providers");
        
        given()
        .when()
            .get("/api/providers")
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .body("npi", hasItem(sampleProvider.npi));
    }
    
    @Test
    void testGetProviderById() {
        // Create a provider first
        ProviderDto createdProvider = given()
            .contentType(ContentType.JSON)
            .body(sampleProvider)
        .when()
            .post("/api/providers")
        .then()
            .statusCode(201)
            .extract().as(ProviderDto.class);
        
        given()
        .when()
            .get("/api/providers/" + createdProvider.id)
        .then()
            .statusCode(200)
            .body("id", equalTo(createdProvider.id.intValue()))
            .body("npi", equalTo(sampleProvider.npi))
            .body("firstName", equalTo(sampleProvider.firstName));
    }
    
    @Test
    void testGetProviderByNpi() {
        // Create a provider first
        given()
            .contentType(ContentType.JSON)
            .body(sampleProvider)
        .when()
            .post("/api/providers");
        
        given()
        .when()
            .get("/api/providers/npi/" + sampleProvider.npi)
        .then()
            .statusCode(200)
            .body("npi", equalTo(sampleProvider.npi))
            .body("firstName", equalTo(sampleProvider.firstName));
    }
    
    @Test
    void testGetProviderByNpiNotFound() {
        given()
        .when()
            .get("/api/providers/npi/9999999999")
        .then()
            .statusCode(404);
    }
    
    @Test
    void testUpdateProvider() {
        // Create a provider first
        ProviderDto createdProvider = given()
            .contentType(ContentType.JSON)
            .body(sampleProvider)
        .when()
            .post("/api/providers")
        .then()
            .statusCode(201)
            .extract().as(ProviderDto.class);
        
        // Update the provider
        createdProvider.firstName = "Jane";
        createdProvider.lastName = "Doe";
        createdProvider.email = "jane.doe@example.com";
        
        given()
            .contentType(ContentType.JSON)
            .body(createdProvider)
        .when()
            .put("/api/providers/" + createdProvider.id)
        .then()
            .statusCode(200)
            .body("firstName", equalTo("Jane"))
            .body("lastName", equalTo("Doe"))
            .body("email", equalTo("jane.doe@example.com"));
    }
    
    @Test
    void testDeleteProvider() {
        // Create a provider first
        ProviderDto createdProvider = given()
            .contentType(ContentType.JSON)
            .body(sampleProvider)
        .when()
            .post("/api/providers")
        .then()
            .statusCode(201)
            .extract().as(ProviderDto.class);
        
        // Delete the provider
        given()
        .when()
            .delete("/api/providers/" + createdProvider.id)
        .then()
            .statusCode(204);
        
        // Verify provider is deleted
        given()
        .when()
            .get("/api/providers/" + createdProvider.id)
        .then()
            .statusCode(404);
    }
    
    @Test
    void testGetProvidersByStatus() {
        // Create a provider first
        given()
            .contentType(ContentType.JSON)
            .body(sampleProvider)
        .when()
            .post("/api/providers");
        
        given()
        .when()
            .get("/api/providers/status/PENDING")
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .body("verificationStatus", everyItem(equalTo("PENDING")));
    }
    
    @Test
    void testNormalizeProvider() {
        // Create a provider first
        ProviderDto createdProvider = given()
            .contentType(ContentType.JSON)
            .body(sampleProvider)
        .when()
            .post("/api/providers")
        .then()
            .statusCode(201)
            .extract().as(ProviderDto.class);
        
        given()
            .contentType(ContentType.JSON)
        .when()
            .post("/api/providers/" + createdProvider.id + "/normalize")
        .then()
            .statusCode(200)
            .body("id", equalTo(createdProvider.id.intValue()));
    }
    
    @Test
    void testFindPotentialDuplicates() {
        // Create a provider first
        ProviderDto createdProvider = given()
            .contentType(ContentType.JSON)
            .body(sampleProvider)
        .when()
            .post("/api/providers")
        .then()
            .statusCode(201)
            .extract().as(ProviderDto.class);
        
        given()
        .contentType(ContentType.JSON)
        .when()
            .post("/api/providers/" + createdProvider.id + "/duplicates")
        .then()
            .statusCode(200)
            .body("count", greaterThanOrEqualTo(0));
    }
}
