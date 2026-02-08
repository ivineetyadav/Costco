-- V2: Seed default admin user
-- Password: Admin@123 (BCrypt hash) - must be changed on first login
INSERT INTO users (id, email, full_name, password_hash, role, vendor_id, is_active, created_at)
VALUES (
    '01HYX3K0000000000000000000',
    'admin@costcomining.co.za',
    'System Admin',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'ADMIN',
    NULL,
    TRUE,
    NOW()
);
