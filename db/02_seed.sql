-- ============================================================================
-- Pitwall - CS157A Team 2
-- 02_seed.sql : test data. Run this AFTER 01_schema.sql.
--
-- Every team member runs the same seed so we all test against identical rows.
--
-- Login accounts (email / password):
--     admin@pitwall.example    / admin123      (Admin,    no team)
--     manager@apex.example     / manager123    (Manager,  Apex Racing)
--     engineer@apex.example    / engineer123   (Engineer, Apex Racing)
--     driver@apex.example      / driver123     (Driver,   Apex Racing, car 44)
--     rookie@redline.example   / rookie123     (Driver,   Redline, car 7)
--
-- The password_hash values are produced by PasswordHasher. To add an account:
--     java cs157a.util.PasswordHasher myNewPassword
-- ============================================================================

USE pitwall;

-- Delete users first, because users points at teams.
DELETE FROM users;
DELETE FROM teams;

-- Restart the id counters so the ids below are always 1, 2, 3.
ALTER TABLE users AUTO_INCREMENT = 1;
ALTER TABLE teams AUTO_INCREMENT = 1;


-- ---------------------------------------------------------------- teams -----
INSERT INTO teams (team_name, series, invite_code, status) VALUES
    ('Apex Racing',        'SCCA Spec Miata', 'APEX2026', 'Approved'),
    ('Redline Motorsport', 'F4 US',           'RDLN7788', 'Approved'),
    ('Garage 51',          'Karting Senior',  'GRG51ABC', 'Pending');


-- ---------------------------------------------------------------- users -----
-- team_id 1 = Apex Racing, 2 = Redline Motorsport.
-- The administrator has no team, so its team_id is NULL.
INSERT INTO users (team_id, email, password_hash, display_name, phone, driver_number, role) VALUES
    (NULL, 'admin@pitwall.example',
     'pbkdf2-sha256$210000$oqzXD0u1pTREDzItdZk0eQ==$hZ9d+3DX5QlB9sFUovcLcL5xaTfX8LzXU3pjTKZGta0=',
     'System Administrator', NULL, NULL, 'Admin'),

    (1, 'manager@apex.example',
     'pbkdf2-sha256$210000$Ed9d31jZxyK6Wvn3IE8OxQ==$B9EDMb9HY9WWgMTnsA7XpVXxqM5J6dfVXlFdB/lHw7k=',
     'Dana Cruz', '408-555-0101', NULL, 'Manager'),

    (1, 'engineer@apex.example',
     'pbkdf2-sha256$210000$gPdINv+Bu691O6uoWO6nXA==$PTHXJMQoDtm9Lil7SqWbgxpROzLU63iyd32wjc/JuDI=',
     'Ravi Patel', '408-555-0102', NULL, 'Engineer'),

    (1, 'driver@apex.example',
     'pbkdf2-sha256$210000$k03cxmhN5acf9qYyOwSoVQ==$IbkyK35PSUlAOUcts+mUrwbpzf6gNTIMhtSPI2EUh9I=',
     'Mia Torres', '408-555-0103', 44, 'Driver'),

    (2, 'rookie@redline.example',
     'pbkdf2-sha256$210000$Wq2cEwpVGqzCgT+c03IZUA==$byc1Eeo8r5WcCFGf45IY2nmkA5xLA1xmQpiP1QsY/JY=',
     'Sam Okafor', NULL, 7, 'Driver');


-- ---------------------------------------------------------------- check -----
-- Run these to confirm the data loaded: 3 teams and 5 users.
SELECT * FROM teams;

SELECT user_id, team_id, email, display_name, driver_number, role FROM users;
