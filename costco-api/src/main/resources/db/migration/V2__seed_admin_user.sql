-- V2: Seed default admin user
-- Password: Admin@123 (BCrypt hash) - must be changed on first login
INSERT INTO users (id, email, full_name, password_hash, role, vendor_id, is_active, created_at)
VALUES (
    '01HYX3K0000000000000000000',
    'admin@costcomining.co.za',
    'System Admin',
    '$2b$10$A0aBq65pdg9sW8/ULZAPs.1utoB0BxpQm.oJgl60flzGVZPiqwv9O',
    'ADMIN',
    NULL,
    TRUE,
    NOW()
);
