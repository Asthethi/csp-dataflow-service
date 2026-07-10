/*
 * ============================================================================
 * CSP - File Tracking & Error Schema
 * ============================================================================
 *
 * This schema provides:
 *   - A single table for tracking all files (ZIP and child files)
 *   - A separate normalized table for capturing one or more errors per file
 *   - A generic status model applicable to all file types
 *   - UUID-based primary and foreign keys
 *
 * ============================================================================
 */

-- -----------------------------------------------------------------------------
-- 1. Extension & Schema
-- -----------------------------------------------------------------------------

CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE SCHEMA IF NOT EXISTS core;

create type core.stakeholder_type_enum as enum ('TERMINAL_SUPPLIER','ACQUIRER','ISSUER','RETAILER','THIRD_PARTY_PROCESSOR',
'PCD_SUPPLIER','SERVICE_PROVIDER','BUREAU');

create table core.stakeholder_type (id uuid not null primary key, type core.stakeholder_type_enum not null unique);

create table core.stakeholder(id uuid not null primary key, name varchar(255), website varchar(255), stakeholder_type_id uuid references core.stakeholder_type, type core.stakeholder_type_enum);


-- -----------------------------------------------------------------------------
-- 2. Drop Tables
-- -----------------------------------------------------------------------------

DROP TABLE IF EXISTS core.file_error;
DROP TABLE IF EXISTS core.file_metadata;

-- -----------------------------------------------------------------------------
-- 3. Drop Enums
-- -----------------------------------------------------------------------------

DROP TYPE IF EXISTS core.file_error_severity;
DROP TYPE IF EXISTS core.file_error_type;
DROP TYPE IF EXISTS core.file_status;

-- -----------------------------------------------------------------------------
-- 4. Enums
-- -----------------------------------------------------------------------------

CREATE TYPE core.file_status AS ENUM (
    'SUBMITTED',
    'PROCESSING',
    'SUCCESSFUL',
    'FAILED'
    );

CREATE TYPE core.file_error_type AS ENUM (
    'FILE_FORMAT_ERROR',
    'FILE_UPLOAD_RECORD_FORMAT_ERROR',
    'MISSING_FILE_ERROR',
    'CPV_REPORT_SIGNATURE_ERROR'
    );

CREATE TYPE core.file_error_severity AS ENUM (
    'WARNING',
    'ERROR'
    );

-- -----------------------------------------------------------------------------
-- 5. Table: core.file_metadata
-- -----------------------------------------------------------------------------

CREATE TABLE core.file_metadata
(
    id               UUID                     PRIMARY KEY DEFAULT gen_random_uuid(),
    parent_id        UUID                     NULL,
    stakeholder_id   UUID                     NULL,
    name             VARCHAR(255)             NOT NULL,
    type             VARCHAR(50)              NOT NULL,
    status           core.file_status         NOT NULL,
    checksum         VARCHAR(128)             NULL,
    size             BIGINT                   NULL,
    submitted_at     TIMESTAMP                NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_file_metadata_parent
        FOREIGN KEY (parent_id)
            REFERENCES core.file_metadata (id),

    CONSTRAINT fk_file_metadata_stakeholder
        FOREIGN KEY (stakeholder_id)
            REFERENCES core.stakeholder (id)
);

COMMENT ON TABLE core.file_metadata IS
    'Stores metadata for every file processed by CSP including top-level ZIP files and child files.';

COMMENT ON COLUMN core.file_metadata.id IS
    'Unique identifier for the file metadata record.';

COMMENT ON COLUMN core.file_metadata.parent_id IS
    'Self-reference to the parent file. NULL for top-level files such as ZIP files and populated for child files.';

COMMENT ON COLUMN core.file_metadata.stakeholder_id IS
    'Reference to the stakeholder associated with the file.';

COMMENT ON COLUMN core.file_metadata.name IS
    'Original file name.';

COMMENT ON COLUMN core.file_metadata.type IS
    'Logical file type such as ZIP, CSV, PDF, XML or LOA.';

COMMENT ON COLUMN core.file_metadata.status IS
    'Current processing status of the file.';

COMMENT ON COLUMN core.file_metadata.checksum IS
    'Checksum of the file such as SHA-256 or SHA-512.';

COMMENT ON COLUMN core.file_metadata.size IS
    'File size in bytes.';

COMMENT ON COLUMN core.file_metadata.submitted_at IS
    'Timestamp when the file was submitted into the system.';

CREATE INDEX idx_file_metadata_parent_id
    ON core.file_metadata (parent_id);

CREATE INDEX idx_file_metadata_stakeholder_id
    ON core.file_metadata (stakeholder_id);

CREATE INDEX idx_file_metadata_status
    ON core.file_metadata (status);

CREATE INDEX idx_file_metadata_type
    ON core.file_metadata (type);

CREATE INDEX idx_file_metadata_name
    ON core.file_metadata (name);

CREATE INDEX idx_file_metadata_submitted_at
    ON core.file_metadata (submitted_at);

-- -----------------------------------------------------------------------------
-- 6. Table: core.file_error
-- -----------------------------------------------------------------------------

CREATE TABLE core.file_error
(
    id                    UUID                           PRIMARY KEY DEFAULT gen_random_uuid(),
    file_id               UUID                           NOT NULL,
    type                  core.file_error_type           NOT NULL,
    severity              core.file_error_severity       NULL,
    record_id             VARCHAR(100)                   NULL,
    service_number        VARCHAR(100)                   NULL,
    description           TEXT                           NOT NULL,
    creation_datetime     TIMESTAMP                      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sent_to_stakeholder   BOOLEAN                        NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_file_error_file
        FOREIGN KEY (file_id)
            REFERENCES core.file_metadata (id)
);

COMMENT ON TABLE core.file_error IS
    'Stores one or more errors associated with a file.';

COMMENT ON COLUMN core.file_error.id IS
    'Unique identifier for the file error record.';

COMMENT ON COLUMN core.file_error.file_id IS
    'Reference to the file associated with the error.';

COMMENT ON COLUMN core.file_error.type IS
    'Categorization of the file error.';

COMMENT ON COLUMN core.file_error.severity IS
    'Severity level of the error.';

COMMENT ON COLUMN core.file_error.record_id IS
    'Identifier of the record inside the file when the error is record-specific.';

COMMENT ON COLUMN core.file_error.service_number IS
    'Service number associated with the error when applicable.';

COMMENT ON COLUMN core.file_error.description IS
    'Detailed description of the error.';

COMMENT ON COLUMN core.file_error.creation_datetime IS
    'Timestamp when the error record was created.';

COMMENT ON COLUMN core.file_error.sent_to_stakeholder IS
    'Indicates whether the error has been sent to the stakeholder.';

CREATE INDEX idx_file_error_file_id
    ON core.file_error (file_id);

CREATE INDEX idx_file_error_type
    ON core.file_error (type);

CREATE INDEX idx_file_error_severity
    ON core.file_error (severity);

CREATE INDEX idx_file_error_creation_datetime
    ON core.file_error (creation_datetime);

CREATE INDEX idx_file_error_sent_to_stakeholder
    ON core.file_error (sent_to_stakeholder);

-- -----------------------------------------------------------------------------
-- End of script
-- -----------------------------------------------------------------------------