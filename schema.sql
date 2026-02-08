-- Enable UUID extension (optional but good for Postgres)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. Table: users
CREATE TABLE IF NOT EXISTS "users" (
    id VARCHAR(100) PRIMARY KEY,
    version INTEGER,
    created_by VARCHAR(100),
    created_time TIMESTAMP,
    modified_by VARCHAR(100),
    modified_time TIMESTAMP,
    user_name VARCHAR(15),
    full_name VARCHAR(100),
    email VARCHAR(100),
    password VARCHAR(100)
);

-- 2. Table: account
CREATE TABLE IF NOT EXISTS "account" (
    id VARCHAR(100) PRIMARY KEY,
    version INTEGER,
    created_by VARCHAR(100),
    created_time TIMESTAMP,
    modified_by VARCHAR(100),
    modified_time TIMESTAMP,
    account_code VARCHAR(255) NOT NULL,
    account_name VARCHAR(255) NOT NULL,
    active_flag VARCHAR(8) NOT NULL,
    user_id VARCHAR(100) REFERENCES "users"(id),
    CONSTRAINT uk_account_code_user UNIQUE (account_code, user_id)
);

-- 3. Table: category
CREATE TABLE IF NOT EXISTS "category" (
    id VARCHAR(100) PRIMARY KEY,
    version INTEGER,
    created_by VARCHAR(100),
    created_time TIMESTAMP,
    modified_by VARCHAR(100),
    modified_time TIMESTAMP,
    category_code VARCHAR(255) NOT NULL,
    category_name VARCHAR(255) NOT NULL,
    category_type VARCHAR(50), -- EXPENSE / INCOME
    user_id VARCHAR(100) REFERENCES "users"(id)
);

-- 4. Table: transaction_code
CREATE TABLE IF NOT EXISTS "transaction_code" (
    id VARCHAR(100) PRIMARY KEY,
    version INTEGER,
    created_by VARCHAR(100),
    created_time TIMESTAMP,
    modified_by VARCHAR(100),
    modified_time TIMESTAMP,
    transaction_code VARCHAR(50) NOT NULL,
    transaction_name VARCHAR(100),
    description VARCHAR(255),
    user_id VARCHAR(100) REFERENCES "users"(id)
);

-- 5. Table: daily_cash (Transaksi Harian)
CREATE TABLE IF NOT EXISTS "daily_cash" (
    id VARCHAR(100) PRIMARY KEY,
    version INTEGER,
    created_by VARCHAR(100),
    created_time TIMESTAMP,
    modified_by VARCHAR(100),
    modified_time TIMESTAMP,
    transaction_number SERIAL, -- Auto increment integer for transaction number
    transaction_date DATE,
    cashflow_flag VARCHAR(100), -- IN / OUT
    value NUMERIC(30, 10), -- Menggunakan NUMERIC sesuai permintaan untuk uang
    description VARCHAR(1024),
    transaction_code_id VARCHAR(100) REFERENCES "transaction_code"(id),
    category_id VARCHAR(100) REFERENCES "category"(id),
    account_id VARCHAR(100) REFERENCES "account"(id),
    user_id VARCHAR(100) REFERENCES "users"(id)
);

-- 6. Table: account_balance (Saldo Akun)
CREATE TABLE IF NOT EXISTS "account_balance" (
    id VARCHAR(100) PRIMARY KEY,
    version INTEGER,
    created_by VARCHAR(100),
    created_time TIMESTAMP,
    modified_by VARCHAR(100),
    modified_time TIMESTAMP,
    balance NUMERIC(30, 10), -- Menggunakan NUMERIC sesuai permintaan untuk uang
    account_id VARCHAR(100) REFERENCES "account"(id),
    user_id VARCHAR(100) REFERENCES "users"(id)
);

-- 7. Table: account_balance_transfer (Transfer Antar Akun)
CREATE TABLE IF NOT EXISTS "account_balance_transfer" (
    id VARCHAR(100) PRIMARY KEY,
    version INTEGER,
    created_by VARCHAR(100),
    created_time TIMESTAMP,
    modified_by VARCHAR(100),
    modified_time TIMESTAMP,
    transfer_date DATE,
    transfer_amount NUMERIC(30, 10), -- Menggunakan NUMERIC sesuai permintaan untuk uang
    description VARCHAR(1024),
    source_account_id VARCHAR(100) REFERENCES "account"(id),
    destination_account_id VARCHAR(100) REFERENCES "account"(id),
    user_id VARCHAR(100) REFERENCES "users"(id)
);
