-- Change createdAt to created_at and updatedAt to updated_at
INSERT INTO doctor(name, specilazation, email, createdAt) VALUES 
('Dr. Aanya Sharma', 'Cardiologist', 'aanya.sharma@hospital.com', CURRENT_TIMESTAMP),
('Dr. Vikram Singh', 'Neurologist', 'vikram.singh.neuro@clinic.net', CURRENT_TIMESTAMP),
('Dr. Priya Patel', 'Dermatologist', 'priya.patel.derma@skincenter.org', CURRENT_TIMESTAMP),
('Dr. Rohan Mehta', 'Orthopedic Surgeon', 'rohan.mehta@orthocare.in', CURRENT_TIMESTAMP),
('Dr. Sneha Desai', 'Pediatrician', 'sneha.desai.peds@childhealth.com', CURRENT_TIMESTAMP);