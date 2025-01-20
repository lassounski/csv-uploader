package com.algorich.csvuploader.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.util.ResourceUtils;

import com.algorich.csvuploader.repository.CsvDataRepository;

import io.restassured.RestAssured;

import java.io.File;
import java.nio.file.Files;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CsvControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private CsvDataRepository repository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        repository.deleteAll();
    }

    @Test
    void uploadAndRetrieveData() throws Exception {
        // Load sample.csv from test resources
        File csvFile = ResourceUtils.getFile("classpath:sample.csv");
        byte[] csvContent = Files.readAllBytes(csvFile.toPath());

        // Upload test
        given()
            .multiPart("file", "sample.csv", csvContent, "text/csv")
            .when()
            .post("/api/csv/upload")
            .then()
            .statusCode(200);

        // Get by code test
        given()
            .when()
            .get("/api/csv/code/271636001")
            .then()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body("code", equalTo("271636001"))
            .body("displayValue", equalTo("Polsslag regelmatig"))
            .body("longDescription", equalTo("The long description is necessary"));

        // Download CSV test
        given()
            .when()
            .get("/api/csv/download")
            .then()
            .statusCode(200)
            .contentType(MediaType.TEXT_PLAIN_VALUE);

        // Delete all test
        given()
            .when()
            .delete("/api/csv")
            .then()
            .statusCode(204);
    }
} 