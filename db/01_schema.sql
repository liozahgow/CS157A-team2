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

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS uses;
DROP TABLE IF EXISTS laps;
DROP TABLE IF EXISTS sessions;
DROP TABLE IF EXISTS events;
DROP TABLE IF EXISTS car_setups;
DROP TABLE IF EXISTS tire_sets;
DROP TABLE IF EXISTS drivers;
DROP TABLE IF EXISTS race_engineers;
DROP TABLE IF EXISTS team_managers;
DROP TABLE IF EXISTS cars;
DROP TABLE IF EXISTS tracks;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS teams;

SET FOREIGN_KEY_CHECKS = 1;

-- ---------------------------------------------------------------- teams -----
CREATE TABLE teams (
    team_id     INT AUTO_INCREMENT PRIMARY KEY,
    team_name   VARCHAR(100) NOT NULL UNIQUE,
    series      VARCHAR(50),
    invite_code CHAR(8)      NOT NULL UNIQUE,
    status      VARCHAR(10)  NOT NULL DEFAULT 'Pending',
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------------- users -----
CREATE TABLE users (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    team_id       INT NULL,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    display_name  VARCHAR(100) NOT NULL,
    phone         VARCHAR(20),
    driver_number INT NULL,
    role          VARCHAR(20)  NOT NULL,
    active        TINYINT(1)   NOT NULL DEFAULT 1,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (team_id) REFERENCES teams(team_id)
);

-- ---------------------------------------------------------------- tracks ----
CREATE TABLE tracks (
    track_id   INT AUTO_INCREMENT PRIMARY KEY,
    track_name VARCHAR(100) NOT NULL,
    location   VARCHAR(100) NOT NULL,
    length_km  DECIMAL(5,3) NOT NULL,
    turns      INT NOT NULL
);

-- ---------------------------------------------------------------- cars ------
CREATE TABLE cars (
    car_id     INT AUTO_INCREMENT PRIMARY KEY,
    team_id    INT NOT NULL,
    chassis_no VARCHAR(50) NOT NULL UNIQUE,
    model      VARCHAR(100) NOT NULL,
    car_class  VARCHAR(50) NOT NULL,
    active     BOOLEAN NOT NULL,

    FOREIGN KEY (team_id) REFERENCES teams(team_id)
);

-- ------------------------------------------------------------- tire_sets ----
-- Weak-entity style key: set_no is unique only within a team.
CREATE TABLE tire_sets (
    team_id         INT NOT NULL,
    set_no          INT NOT NULL,
    compound        VARCHAR(50),
    heat_cycles     INT,
    max_heat_cycles INT,
    tire_size       VARCHAR(50),
    status          VARCHAR(50),
    received_date   DATE,

    PRIMARY KEY (team_id, set_no),
    FOREIGN KEY (team_id) REFERENCES teams(team_id)
);

-- ------------------------------------------------------------- car_setups ---
CREATE TABLE car_setups (
    setup_id          INT AUTO_INCREMENT PRIMARY KEY,
    car_id            INT NOT NULL,
    setup_name        VARCHAR(100) NOT NULL,
    front_wing_angle  DECIMAL(5,2),
    rear_wing_angle   DECIMAL(5,2),
    ride_height_mm    DECIMAL(6,2),
    tire_pressure_psi DECIMAL(5,2),
    spring_rate       DECIMAL(8,2),
    notes             TEXT,

    FOREIGN KEY (car_id) REFERENCES cars(car_id)
);

-- --------------------------------------------------------------- drivers ----
-- ISA subtype of users: user_id is both the PK and an FK to users.
CREATE TABLE drivers (
    user_id        INT PRIMARY KEY,
    driver_number  INT NOT NULL,
    license_class  VARCHAR(50),
    years_racing   INT,

    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- --------------------------------------------------------- race_engineers ----
CREATE TABLE race_engineers (
    user_id          INT PRIMARY KEY,
    specialty        VARCHAR(100),
    years_experience INT,

    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- ---------------------------------------------------------- team_managers ----
CREATE TABLE team_managers (
    user_id          INT PRIMARY KEY,
    office_phone     VARCHAR(20),
    budget_authority DECIMAL(12,2),

    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- ---------------------------------------------------------------- events ----
CREATE TABLE events (
    event_id   INT AUTO_INCREMENT PRIMARY KEY,
    event_name VARCHAR(100) NOT NULL,
    track_id   INT NOT NULL,
    team_id    INT NOT NULL,
    start_date DATE NOT NULL,
    end_date   DATE NOT NULL,
    event_type VARCHAR(50),

    FOREIGN KEY (track_id) REFERENCES tracks(track_id),
    FOREIGN KEY (team_id) REFERENCES teams(team_id)
);

-- --------------------------------------------------------------- sessions ---
CREATE TABLE sessions (
    session_id       INT AUTO_INCREMENT PRIMARY KEY,
    event_id         INT NOT NULL,
    car_id           INT NOT NULL,
    setup_id         INT NULL,
    driver_user_id   INT NOT NULL,
    session_type     VARCHAR(50) NOT NULL,
    session_date     DATE NOT NULL,
    weather          VARCHAR(50),
    air_temp_c       DECIMAL(5,2),
    driver_feedback  TEXT,

    FOREIGN KEY (event_id) REFERENCES events(event_id),
    FOREIGN KEY (car_id) REFERENCES cars(car_id),
    FOREIGN KEY (setup_id) REFERENCES car_setups(setup_id),
    FOREIGN KEY (driver_user_id) REFERENCES drivers(user_id)
);

-- ---------------------------------------------------------------- laps ------
-- Weak-entity style key: lap_number is unique only within a session.
CREATE TABLE laps (
    session_id  INT NOT NULL,
    lap_number  INT NOT NULL,
    lap_time_sec DECIMAL(8,3) NOT NULL,
    is_valid    BOOLEAN NOT NULL,

    PRIMARY KEY (session_id, lap_number),
    FOREIGN KEY (session_id) REFERENCES sessions(session_id)
);

-- ---------------------------------------------------------------- uses ------
-- Relationship table between sessions and tire_sets.
CREATE TABLE uses (
    session_id INT NOT NULL,
    team_id    INT NOT NULL,
    set_no     INT NOT NULL,
    laps_run   INT NOT NULL,

    PRIMARY KEY (session_id, team_id, set_no),
    FOREIGN KEY (session_id) REFERENCES sessions(session_id),
    FOREIGN KEY (team_id, set_no) REFERENCES tire_sets(team_id, set_no)
);

