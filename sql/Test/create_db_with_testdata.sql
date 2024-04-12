-- Create the database
CREATE DATABASE hmdms;

GO

USE hmdms;

GO

-- AUTH TABLES

-- Table containing all users
CREATE TABLE users (
    user_id nvarchar(255) PRIMARY KEY,
    user_name nvarchar(255) NOT NULL UNIQUE,
    pw nvarchar(255) NOT NULL,
    locked boolean NOT NULL
);

CREATE TABLE tickets (
    ticket nvarchar(255) PRIMARY KEY,
    user_id nvarchar(255) NOT NULL,
    issued_at datetime NOT NULL,
    valid_thru datetime NOT NULL
);

GO

ALTER TABLE tickets
ADD CONSTRAINT FK_tick_user_id FOREIGN KEY (user_id) REFERENCES(users.user_id);

GO

INSERT INTO users (user_id, user_name, pw, locked)
VALUES ('123', 'admin', 'elo', 0)