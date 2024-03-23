-- Create the database
CREATE DATABASE hmdms;

GO

USE hmdms;

GO

-- AUTH TABLES

-- Table containing all users
CREATE TABLE user (
    user_id nvarchar(255) PRIMARY KEY,
    user_name nvarchar(255) NOT NULL,
    pw nvarchar(255) NOT NULL,
    locked boolean NOT NULL
);

CREATE TABLE ticket (
    ticket nvarchar(255) PRIMARY KEY,
    user_id nvarchar(255) NOT NULL,
    issued_at datetime NOT NULL,
    valid_thru datetime NOT NULL
);

GO

ALTER TABLE ticket
ADD CONSTRAINT FK_tick_user_id FOREIGN KEY (user_id) REFERENCES(user.user_id);