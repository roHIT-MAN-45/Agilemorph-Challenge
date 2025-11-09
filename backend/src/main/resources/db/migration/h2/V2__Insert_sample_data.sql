-- Insert sample providers (H2 compatible, no ON CONFLICT)
INSERT INTO providers (
    npi, first_name, last_name, middle_name, email, phone, date_of_birth,
    specialty, taxonomy_code, verification_status
) VALUES
('1234567890', 'John', 'Smith', 'Michael', 'john.smith@example.com', '555-123-4567',
 DATE '1980-05-15', 'Internal Medicine', '207R00000X', 'VERIFIED'),
('2345678901', 'Jane', 'Doe', NULL, 'jane.doe@example.com', '555-234-5678',
 DATE '1975-08-22', 'Cardiology', '207RC0000X', 'PENDING'),
('3456789012', 'Robert', 'Johnson', NULL, 'robert.johnson@example.com', '555-345-6789',
 DATE '1982-12-10', 'Family Medicine', '207Q00000X', 'VERIFIED'),
('4567890123', 'Sarah', 'Williams', 'Elizabeth', 'sarah.williams@example.com', '555-456-7890',
 DATE '1978-03-25', 'Pediatrics', '208000000X', 'FLAGGED'),
('5678901234', 'Michael', 'Brown', 'David', 'michael.brown@example.com', '555-567-8901',
 DATE '1985-11-08', 'Orthopedic Surgery', '207X00000X', 'VERIFIED');

-- Insert sample licenses
INSERT INTO licenses (
    provider_id, license_number, state, license_type, issue_date, expiry_date, status
) VALUES
(1, 'MD123456', 'CA', 'Medical Doctor', DATE '2010-06-01', DATE '2025-06-01', 'ACTIVE'),
(1, 'MD123457', 'NY', 'Medical Doctor', DATE '2012-03-15', DATE '2024-03-15', 'ACTIVE'),
(2, 'MD234567', 'NY', 'Medical Doctor', DATE '2015-03-01', DATE '2020-03-01', 'EXPIRED'),
(3, 'MD345678', 'TX', 'Medical Doctor', DATE '2012-01-15', DATE '2026-01-15', 'ACTIVE'),
(4, 'MD456789', 'FL', 'Medical Doctor', DATE '2018-07-01', DATE '2023-07-01', 'EXPIRED'),
(5, 'MD567890', 'CA', 'Medical Doctor', DATE '2015-09-01', DATE '2025-09-01', 'ACTIVE');

-- Insert sample practice locations
INSERT INTO practice_locations (
    provider_id, name, address_line1, address_line2, city, state, zip_code, phone, taxonomy_code, is_primary
) VALUES
(1, 'Smith Medical Center', '123 Main St', NULL, 'San Francisco', 'CA', '94102', '555-123-4567', '207R00000X', TRUE),
(1, 'Smith Medical Center - Branch', '456 Oak Ave', 'Suite 200', 'Oakland', 'CA', '94601', '555-123-4568', '207R00000X', FALSE),
(2, 'Doe Cardiology Clinic', '456 Oak Ave', NULL, 'New York', 'NY', '10001', '555-234-5678', '207RC0000X', TRUE),
(3, 'Johnson Family Medicine', '789 Pine St', NULL, 'Houston', 'TX', '77001', '555-345-6789', '207Q00000X', TRUE),
(3, 'Johnson Family Medicine - Branch', '321 Elm St', NULL, 'Austin', 'TX', '73301', '555-345-6790', '207Q00000X', FALSE),
(4, 'Williams Pediatrics', '654 Maple Dr', 'Suite 100', 'Miami', 'FL', '33101', '555-456-7890', '208000000X', TRUE),
(5, 'Brown Orthopedics', '987 Cedar Ln', NULL, 'Los Angeles', 'CA', '90210', '555-567-8901', '207X00000X', TRUE);

-- Insert sample audit logs
INSERT INTO audit_logs (
    provider_id, action, details, timestamp, user_id, ip_address
) VALUES
(1, 'PROVIDER_CREATED', 'Provider created with NPI: 1234567890', TIMESTAMP '2024-01-15 10:00:00', 'system', '127.0.0.1'),
(1, 'LICENSE_ADDED', 'License MD123456 added for state CA', TIMESTAMP '2024-01-15 10:05:00', 'admin', '127.0.0.1'),
(2, 'PROVIDER_CREATED', 'Provider created with NPI: 2345678901', TIMESTAMP '2024-01-15 10:10:00', 'system', '127.0.0.1'),
(2, 'LICENSE_EXPIRED', 'License MD234567 expired on 2020-03-01', TIMESTAMP '2024-01-15 10:15:00', 'system', '127.0.0.1'),
(3, 'PROVIDER_CREATED', 'Provider created with NPI: 3456789012', TIMESTAMP '2024-01-15 10:20:00', 'system', '127.0.0.1'),
(4, 'PROVIDER_CREATED', 'Provider created with NPI: 4567890123', TIMESTAMP '2024-01-15 10:25:00', 'system', '127.0.0.1'),
(4, 'PROVIDER_FLAGGED', 'Provider flagged due to expired license', TIMESTAMP '2024-01-15 10:30:00', 'system', '127.0.0.1'),
(5, 'PROVIDER_CREATED', 'Provider created with NPI: 5678901234', TIMESTAMP '2024-01-15 10:35:00', 'system', '127.0.0.1');

-- Insert sample rule evaluations
INSERT INTO rule_evaluations (
    provider_id, rule_name, triggered, severity, message, metadata, evaluated_at
) VALUES
(2, 'license-expiry-rule', TRUE, 'HIGH',
 'Provider has expired license: MD234567 (expired on 2020-03-01)',
 'License State: NY, License Type: Medical Doctor',
 TIMESTAMP '2024-01-15 10:15:00'),
(4, 'license-expiry-rule', TRUE, 'HIGH',
 'Provider has expired license: MD456789 (expired on 2023-07-01)',
 'License State: FL, License Type: Medical Doctor',
 TIMESTAMP '2024-01-15 10:30:00'),
(3, 'mismatched-taxonomy-rule', TRUE, 'HIGH',
 'Provider has multiple practice locations with mismatched taxonomies',
 'Location Count: 2',
 TIMESTAMP '2024-01-15 10:20:00');

-- Insert sample rule evaluation facts
INSERT INTO rule_evaluation_facts (rule_evaluation_id, fact) VALUES
(1, 'License Number: MD234567'),
(1, 'Expiry Date: 2020-03-01'),
(1, 'Days Until Expiry: -1461'),
(2, 'License Number: MD456789'),
(2, 'Expiry Date: 2023-07-01'),
(2, 'Days Until Expiry: -197'),
(3, 'Provider NPI: 3456789012'),
(3, 'Location Count: 2'),
(3, 'Primary Taxonomy: 207Q00000X');
