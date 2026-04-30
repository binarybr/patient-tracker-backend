-- V6__patient_gender.sql
-- Purpose: Add gender field to patients for demographics and filtering.
ALTER TABLE patients ADD COLUMN gender VARCHAR(32) NULL;
