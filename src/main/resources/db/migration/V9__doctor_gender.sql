-- V9__doctor_gender.sql
-- Purpose: Add gender field to doctors for demographics and filtering.
ALTER TABLE doctors ADD COLUMN gender VARCHAR(32) NULL;
