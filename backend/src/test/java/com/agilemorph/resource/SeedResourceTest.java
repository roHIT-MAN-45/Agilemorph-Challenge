package com.agilemorph.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@Transactional
public class SeedResourceTest {
    
    @Test
    void testSeedProviders() {
        given()
        .when()
            .post("/api/seed/providers")
        .then()
            .statusCode(200)
            .body("message", equalTo("Sample providers created successfully"))
            .body("count", greaterThan(0))
            .body("providers", notNullValue())
            .body("providers.size()", greaterThan(0));
    }
    
    @Test
    void testSeedProvidersMultipleTimes() {
        // First seeding
        given()
        .when()
            .post("/api/seed/providers")
        .then()
            .statusCode(200);
        
        // Second seeding should still work
        given()
        .when()
            .post("/api/seed/providers")
        .then()
            .statusCode(200)
            .body("message", equalTo("Sample providers created successfully"))
            .body("count", greaterThan(0));
    }
}
