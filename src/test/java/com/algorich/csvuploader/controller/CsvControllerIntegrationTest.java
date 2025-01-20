package com.algorich.csvuploader.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

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

    @Test
    void shouldReturnErrorForInvalidCsvFormat() throws Exception {
        String invalidContent = "invalid,headers\nZIB,TEST";
        
        given()
            .multiPart("file", "invalid.csv", invalidContent.getBytes(), "text/csv")
            .when()
            .post("/api/csv/upload")
            .then()
            .statusCode(400)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body("message", equalTo("CSV validation failed"))
            .body("errors", hasItem(containsString("Invalid CSV headers")));
    }

    @Test
    void shouldReturnErrorForInvalidDateFormat() throws Exception {
        String content = "source,codeListCode,code,displayValue,longDescription,fromDate,toDate,sortingPriority\n" +
                "\"ZIB\",\"ZIB001\",\"271636001\",\"Test\",\"Description\",\"2019-01-01\",\"\",\"1\"";
        
        given()
            .multiPart("file", "invalid_date.csv", content.getBytes(), "text/csv")
            .when()
            .post("/api/csv/upload")
            .then()
            .statusCode(400)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body("message", equalTo("CSV validation failed"))
            .body("errors", hasItem(containsString("Invalid fromDate format")));
    }
} 