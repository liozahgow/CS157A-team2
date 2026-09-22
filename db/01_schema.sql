-- ============================================================================
-- Pitwall - CS157A Team 2
-- 01_schema.sql : creates the database and the teams / users tables.
--
-- How to run:  open this file in MySQL Workbench and execute it,
--              then run 02_seed.sql.
-- WARNING:     this script DROPS the tables, so every row is lost.
-- ============================================================================

CREATE DATABASE IF NOT EXISTS pitwall;

USE pitwall;

-- Drop users first. It points at teams, so teams cannot be dropped while
-- users still exists.
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS teams;


-- ----------------------------------------------------------------------------
-- teams : one racing team. This is the root of the schema - almost every other
--         table will carry a team_id so one team never sees another team's data.
-- ----------------------------------------------------------------------------
CREATE TABLE teams (
    team_id     INT AUTO_INCREMENT PRIMARY KEY,
    team_name   VARCHAR(100) NOT NULL UNIQUE,
    series      VARCHAR(50),                             -- racing category, e.g. "F4 US"
    invite_code CHAR(8)      NOT NULL UNIQUE,            -- drivers/engineers use it to join
    status      VARCHAR(10)  NOT NULL DEFAULT 'Pending', -- Pending / Approved / Suspended
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);


-- ----------------------------------------------------------------------------
-- users : one account. Everyone belongs to one team and holds one role inside
--         it, except the system administrator, whose team_id is NULL because
--         the admin belongs to no team.
-- ----------------------------------------------------------------------------
CREATE TABLE users (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    team_id       INT NULL,                        -- NULL only for the administrator
    email         VARCHAR(255) NOT NULL UNIQUE,    -- the login name
    password_hash VARCHAR(255) NOT NULL,           -- never store a plain password here
    display_name  VARCHAR(100) NOT NULL,
    phone         VARCHAR(20),
    driver_number INT NULL,                        -- drivers only, NULL for everyone else
    role          VARCHAR(20)  NOT NULL,           -- Driver / Engineer / Manager / Admin
    active        TINYINT(1)   NOT NULL DEFAULT 1, -- 0 = left the team, history kept
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (team_id) REFERENCES teams(team_id)
);
