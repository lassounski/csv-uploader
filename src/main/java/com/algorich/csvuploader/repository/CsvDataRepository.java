package com.algorich.csvuploader.repository;

import com.algorich.csvuploader.model.CsvData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CsvDataRepository extends JpaRepository<CsvData, Long> {
    Optional<CsvData> findByCode(String code);
} 