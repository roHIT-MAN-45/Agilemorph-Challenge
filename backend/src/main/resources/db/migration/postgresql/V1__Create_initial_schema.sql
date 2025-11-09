-- Create Sequences
CREATE SEQUENCE IF NOT EXISTS providers_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS licenses_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS practice_locations_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS rule_evaluations_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS audit_logs_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS rule_evaluation_facts_seq START WITH 1 INCREMENT BY 1;

-- Create providers table
CREATE TABLE providers (
    id BIGINT PRIMARY KEY DEFAULT nextval('providers_seq'),
    npi VARCHAR(10) UNIQUE NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    email VARCHAR(255),
    phone VARCHAR(20),
    date_of_birth DATE NOT NULL,
    specialty VARCHAR(100),
    taxonomy_code VARCHAR(20),
    verification_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create licenses table
CREATE TABLE licenses (
    id BIGINT PRIMARY KEY DEFAULT nextval('licenses_seq'),
    provider_id BIGINT NOT NULL REFERENCES providers(id) ON DELETE CASCADE,
    license_number VARCHAR(50) NOT NULL,
    state VARCHAR(2) NOT NULL,
    license_type VARCHAR(50) NOT NULL,
    issue_date DATE NOT NULL,
    expiry_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create practice_locations table
CREATE TABLE practice_locations (
    id BIGINT PRIMARY KEY DEFAULT nextval('practice_locations_seq'),
    provider_id BIGINT NOT NULL REFERENCES providers(id) ON DELETE CASCADE,
    name VARCHAR(200) NOT NULL,
    address_line1 VARCHAR(200) NOT NULL,
    address_line2 VARCHAR(200),
    city VARCHAR(100) NOT NULL,
    state VARCHAR(2) NOT NULL,
    zip_code VARCHAR(10) NOT NULL,
    phone VARCHAR(20),
    taxonomy_code VARCHAR(20),
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create audit_logs table
CREATE TABLE audit_logs (
    id BIGINT PRIMARY KEY DEFAULT nextval('audit_logs_seq'),
    provider_id BIGINT NOT NULL REFERENCES providers(id) ON DELETE CASCADE,
    action VARCHAR(100) NOT NULL,
    details TEXT,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id VARCHAR(100),
    ip_address VARCHAR(45)
);

-- Create rule_evaluations table
CREATE TABLE rule_evaluations (
    id BIGINT PRIMARY KEY DEFAULT nextval('rule_evaluations_seq'),
    provider_id BIGINT NOT NULL REFERENCES providers(id) ON DELETE CASCADE,
    rule_name VARCHAR(100) NOT NULL,
    triggered BOOLEAN NOT NULL DEFAULT FALSE,
    severity VARCHAR(20),
    message TEXT,
    metadata TEXT,
    evaluated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create rule_evaluation_facts table for storing rule facts
CREATE TABLE rule_evaluation_facts (
    id BIGINT PRIMARY KEY DEFAULT nextval('rule_evaluation_facts_seq'),
    rule_evaluation_id BIGINT NOT NULL REFERENCES rule_evaluations(id) ON DELETE CASCADE,
    fact VARCHAR(500)
);

-- Create indexes for better performance
CREATE INDEX idx_providers_npi ON providers(npi);
CREATE INDEX idx_providers_verification_status ON providers(verification_status);
CREATE INDEX idx_licenses_provider_id ON licenses(provider_id);
CREATE INDEX idx_licenses_expiry_date ON licenses(expiry_date);
CREATE INDEX idx_practice_locations_provider_id ON practice_locations(provider_id);
CREATE INDEX idx_audit_logs_provider_id ON audit_logs(provider_id);
CREATE INDEX idx_audit_logs_timestamp ON audit_logs(timestamp);
CREATE INDEX idx_rule_evaluations_provider_id ON rule_evaluations(provider_id);
CREATE INDEX idx_rule_evaluations_rule_name ON rule_evaluations(rule_name);
CREATE INDEX idx_rule_evaluations_triggered ON rule_evaluations(triggered);
