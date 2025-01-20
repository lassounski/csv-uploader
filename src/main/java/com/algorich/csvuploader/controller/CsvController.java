package com.algorich.csvuploader.controller;

import com.algorich.csvuploader.model.CsvData;
import com.algorich.csvuploader.service.CsvService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/csv")
@RequiredArgsConstructor
public class CsvController {

    private final CsvService csvService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadCsv(@RequestParam("file") MultipartFile file) {
        csvService.uploadCsv(file);
        return ResponseEntity.ok("File uploaded successfully");
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<CsvData> getByCode(@PathVariable String code) {
        return csvService.getByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAll() {
        csvService.deleteAll();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/download")
    public ResponseEntity<String> downloadCsv() {
        String csvContent = csvService.generateCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=data.csv")
                .contentType(MediaType.TEXT_PLAIN)
                .body(csvContent);
    }
} 