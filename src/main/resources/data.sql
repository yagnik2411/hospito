-- INSERT INTO patient (name, gender, age, date_of_birth, email, blood_group) VALUES ('John Smith', 'Male', 42, '1983-04-20', 'john.smith@example.com', 'O_POSITIVE');

-- INSERT INTO patient (name, gender, age, date_of_birth, email, blood_group) VALUES ('Emily Jones', 'Female', 28, '1997-08-10', 'emily.j@example.net', 'A_NEGATIVE');

-- INSERT INTO patient (name, gender, age, date_of_birth, email, blood_group) VALUES ('Michael Brown', 'Male', 55, '1970-11-02', 'mbrown@example.org', 'B_POSITIVE');

-- INSERT INTO patient (name, gender, age, date_of_birth, email, blood_group) VALUES ('Sarah Wilson', 'Female', 61, '1964-07-30', 'sarah.wilson@example.com', 'AB_POSITIVE');

-- INSERT INTO doctor(name, specilazation, email) VALUES
-- ('Dr. Aanya Sharma', 'Cardiologist', 'aanya.sharma@hospital.com');
-- INSERT INTO doctor(name, specilazation, email) VALUES
-- ('Dr. Vikram Singh', 'Neurologist', 'vikram.singh.neuro@clinic.net');
-- INSERT INTO doctor(name, specilazation, email) VALUES
-- ('Dr. Priya Patel', 'Dermatologist', 'priya.patel.derma@skincenter.org');
-- INSERT INTO doctor(name, specilazation, email) VALUES
-- ('Dr. Rohan Mehta', 'Orthopedic Surgeon', 'rohan.mehta@orthocare.in');
-- INSERT INTO doctor(name, specilazation, email) VALUES
-- ('Dr. Sneha Desai', 'Pediatrician', 'sneha.desai.peds@childhealth.com');
INSERT INTO doctor(name, specilazation, email, createdAt, updatedAt) VALUES
('Dr. Aanya Sharma', 'Cardiologist', 'aanya.sharma@hospital.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Dr. Vikram Singh', 'Neurologist', 'vikram.singh.neuro@clinic.net', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Dr. Priya Patel', 'Dermatologist', 'priya.patel.derma@skincenter.org', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Dr. Rohan Mehta', 'Orthopedic Surgeon', 'rohan.mehta@orthocare.in', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Dr. Sneha Desai', 'Pediatrician', 'sneha.desai.peds@childhealth.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Adding patient data required for the application tests to run successfully.
INSERT INTO patient(name, dob, createdAt, updatedAt) VALUES
('Amit Kumar', '1990-05-15', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Sunita Devi', '1985-11-22', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);