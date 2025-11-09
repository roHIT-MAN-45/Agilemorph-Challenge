package com.agilemorph.resource;

import org.flywaydb.core.Flyway;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;


import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SeedResourceTest {

    @Inject
    Flyway flyway;

    @BeforeAll
    void init() {
        flyway.clean();
        flyway.migrate();
    }
    
    @Test
    void testSeedProviders() {
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
        .when()
            .post("/api/seed/providers")
        .then()
            .statusCode(200)
            .body("message", anyOf(
                equalTo("Sample providers created successfully"),
                equalTo("Sample providers already exist")
            ))
            .body("count", greaterThan(0))
            .body("providers", notNullValue())
            .body("providers.size()", greaterThan(0));
    }
    
    @Test
    void testSeedProvidersMultipleTimes() {
        for (int i = 0; i < 2; i++) {
            given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
            .when()
                .post("/api/seed/providers")
            .then()
                .statusCode(200)
                .body("message", anyOf(
                    equalTo("Sample providers created successfully"),
                    equalTo("Sample providers already exist")
                ))
                .body("count", greaterThan(0));
        }
    }
}
