-- V2__adjust_lob_columns.sql
-- Purpose: Adjust medical_cases text columns to LONGTEXT and allow NULLs for larger payloads.
ALTER TABLE medical_cases MODIFY symptoms LONGTEXT NULL, MODIFY medicines LONGTEXT NULL;
