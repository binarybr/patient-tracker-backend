-- V8__soft_delete_doctor_patient.sql
-- Purpose: Enable soft-deletes on doctors and patients to preserve historical relations.
ALTER TABLE doctors ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE patients ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE;
