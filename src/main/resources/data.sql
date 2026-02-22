INSERT INTO roles (name, created_at, updated_at, created_by, updated_by)
VALUES 
('SUPER_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
('BRANCH_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
('DOCTOR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
('PATIENT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system')
ON CONFLICT (name) DO NOTHING;