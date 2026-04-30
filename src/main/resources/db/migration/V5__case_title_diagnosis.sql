-- V5__case_title_diagnosis.sql
-- Purpose: Add case title and long-form diagnosis text to medical_cases.
ALTER TABLE medical_cases ADD COLUMN title VARCHAR(255) NULL, ADD COLUMN diagnosis LONGTEXT NULL;
