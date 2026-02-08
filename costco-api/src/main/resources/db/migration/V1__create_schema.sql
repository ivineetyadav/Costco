-- V1: Core schema for Costco Mining SaaS Platform
-- All primary keys use ULID (CHAR(26))

-- Vendors who rent machines
CREATE TABLE vendors (
    id              CHAR(26) PRIMARY KEY,
    company_name    VARCHAR(200) NOT NULL,
    contact_name    VARCHAR(200),
    email           VARCHAR(200),
    phone           VARCHAR(50),
    address         TEXT,
    created_at      TIMESTAMP DEFAULT NOW()
);

-- Machines registry
CREATE TABLE machines (
    id              CHAR(26) PRIMARY KEY,
    name            VARCHAR(200) NOT NULL,
    type            VARCHAR(100),
    model           VARCHAR(100),
    serial_number   VARCHAR(100) UNIQUE,
    power_rating    VARCHAR(50),
    status          VARCHAR(20) DEFAULT 'AVAILABLE',
    created_at      TIMESTAMP DEFAULT NOW(),
    CONSTRAINT chk_machine_status CHECK (status IN ('AVAILABLE', 'RENTED', 'MAINTENANCE'))
);

-- Users (database-managed auth)
CREATE TABLE users (
    id              CHAR(26) PRIMARY KEY,
    email           VARCHAR(200) UNIQUE NOT NULL,
    full_name       VARCHAR(200),
    password_hash   VARCHAR(200) NOT NULL,
    role            VARCHAR(20) NOT NULL,
    vendor_id       CHAR(26) REFERENCES vendors(id),
    refresh_token   VARCHAR(500),
    is_active       BOOLEAN DEFAULT TRUE,
    last_login_at   TIMESTAMP,
    created_at      TIMESTAMP DEFAULT NOW(),
    CONSTRAINT chk_user_role CHECK (role IN ('ADMIN', 'MANAGER', 'VENDOR'))
);

-- Rental contracts
CREATE TABLE contracts (
    id              CHAR(26) PRIMARY KEY,
    machine_id      CHAR(26) NOT NULL REFERENCES machines(id),
    vendor_id       CHAR(26) NOT NULL REFERENCES vendors(id),
    start_date      DATE NOT NULL,
    end_date        DATE,
    rate_per_hour   DECIMAL(10,2) NOT NULL,
    currency        VARCHAR(3) DEFAULT 'ZAR',
    status          VARCHAR(20) DEFAULT 'ACTIVE',
    created_at      TIMESTAMP DEFAULT NOW(),
    CONSTRAINT chk_contract_status CHECK (status IN ('ACTIVE', 'EXPIRED', 'TERMINATED'))
);

-- Network devices (with validation states)
CREATE TABLE devices (
    id              CHAR(26) PRIMARY KEY,
    machine_id      CHAR(26) REFERENCES machines(id),
    device_serial   VARCHAR(100) UNIQUE NOT NULL,
    iot_thing_name  VARCHAR(100) UNIQUE NOT NULL,
    status          VARCHAR(20) DEFAULT 'PENDING',
    approved_by     CHAR(26) REFERENCES users(id),
    approved_at     TIMESTAMP,
    last_seen_at    TIMESTAMP,
    created_at      TIMESTAMP DEFAULT NOW(),
    CONSTRAINT chk_device_status CHECK (status IN ('PENDING', 'ACTIVE', 'SUSPENDED', 'DECOMMISSIONED'))
);

-- Raw telemetry (partitioned by month)
CREATE TABLE telemetry (
    id              CHAR(26) NOT NULL,
    device_id       CHAR(26) NOT NULL,
    machine_id      CHAR(26) NOT NULL,
    timestamp       TIMESTAMPTZ NOT NULL,
    engine_running  BOOLEAN,
    engine_hours    DECIMAL(10,2),
    fuel_level      DECIMAL(5,2),
    location_lat    DECIMAL(9,6),
    location_lng    DECIMAL(9,6),
    payload_json    JSONB,
    received_at     TIMESTAMPTZ DEFAULT NOW(),
    PRIMARY KEY (id, timestamp)
) PARTITION BY RANGE (timestamp);

-- Create initial partitions (extend as needed)
CREATE TABLE telemetry_2024_q1 PARTITION OF telemetry
    FOR VALUES FROM ('2024-01-01') TO ('2024-04-01');
CREATE TABLE telemetry_2024_q2 PARTITION OF telemetry
    FOR VALUES FROM ('2024-04-01') TO ('2024-07-01');
CREATE TABLE telemetry_2024_q3 PARTITION OF telemetry
    FOR VALUES FROM ('2024-07-01') TO ('2024-10-01');
CREATE TABLE telemetry_2024_q4 PARTITION OF telemetry
    FOR VALUES FROM ('2024-10-01') TO ('2025-01-01');
CREATE TABLE telemetry_2025_q1 PARTITION OF telemetry
    FOR VALUES FROM ('2025-01-01') TO ('2025-04-01');
CREATE TABLE telemetry_2025_q2 PARTITION OF telemetry
    FOR VALUES FROM ('2025-04-01') TO ('2025-07-01');
CREATE TABLE telemetry_2025_q3 PARTITION OF telemetry
    FOR VALUES FROM ('2025-07-01') TO ('2025-10-01');
CREATE TABLE telemetry_2025_q4 PARTITION OF telemetry
    FOR VALUES FROM ('2025-10-01') TO ('2026-01-01');
CREATE TABLE telemetry_2026_q1 PARTITION OF telemetry
    FOR VALUES FROM ('2026-01-01') TO ('2026-04-01');
CREATE TABLE telemetry_2026_q2 PARTITION OF telemetry
    FOR VALUES FROM ('2026-04-01') TO ('2026-07-01');

-- Usage summaries
CREATE TABLE usage_reports (
    id              CHAR(26) PRIMARY KEY,
    contract_id     CHAR(26) REFERENCES contracts(id),
    machine_id      CHAR(26) REFERENCES machines(id),
    vendor_id       CHAR(26) REFERENCES vendors(id),
    period_start    DATE,
    period_end      DATE,
    total_hours     DECIMAL(10,2),
    billable_amount DECIMAL(12,2),
    currency        VARCHAR(3) DEFAULT 'ZAR',
    generated_at    TIMESTAMP DEFAULT NOW()
);

-- Indexes
CREATE INDEX idx_telemetry_device_id ON telemetry (device_id);
CREATE INDEX idx_telemetry_machine_id ON telemetry (machine_id);
CREATE INDEX idx_telemetry_timestamp ON telemetry (timestamp);
CREATE INDEX idx_contracts_machine_id ON contracts (machine_id);
CREATE INDEX idx_contracts_vendor_id ON contracts (vendor_id);
CREATE INDEX idx_contracts_status ON contracts (status);
CREATE INDEX idx_devices_machine_id ON devices (machine_id);
CREATE INDEX idx_devices_status ON devices (status);
CREATE INDEX idx_devices_device_serial ON devices (device_serial);
CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_vendor_id ON users (vendor_id);
CREATE INDEX idx_usage_reports_contract_id ON usage_reports (contract_id);
CREATE INDEX idx_usage_reports_vendor_id ON usage_reports (vendor_id);
CREATE INDEX idx_usage_reports_period ON usage_reports (period_start, period_end);
