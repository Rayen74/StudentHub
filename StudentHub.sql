-- =====================================================================
-- StudentHub SQLite Initialization Script
-- =====================================================================

-- Enable foreign key support
PRAGMA foreign_keys = ON;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nom TEXT NOT NULL,
    prenom TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Create index on email for fast search and authentication
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
