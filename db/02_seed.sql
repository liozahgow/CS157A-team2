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

SET SQL_SAFE_UPDATES = 0;

-- --------------------------------------------------------------- clear data --
-- Delete child rows before parent rows so foreign-key constraints are satisfied.
DELETE FROM uses;
DELETE FROM laps;
DELETE FROM sessions;
DELETE FROM events;
DELETE FROM car_setups;
DELETE FROM tire_sets;
DELETE FROM drivers;
DELETE FROM race_engineers;
DELETE FROM team_managers;
DELETE FROM cars;
DELETE FROM users;
DELETE FROM tracks;
DELETE FROM teams;

-- Reset AUTO_INCREMENT values so IDs below are predictable after every rerun.
ALTER TABLE sessions AUTO_INCREMENT = 1;
ALTER TABLE events AUTO_INCREMENT = 1;
ALTER TABLE car_setups AUTO_INCREMENT = 1;
ALTER TABLE cars AUTO_INCREMENT = 1;
ALTER TABLE users AUTO_INCREMENT = 1;
ALTER TABLE tracks AUTO_INCREMENT = 1;
ALTER TABLE teams AUTO_INCREMENT = 1;

-- ---------------------------------------------------------------- teams -----
INSERT INTO teams (team_name, series, invite_code, status) VALUES
    ('Apex Racing',          'SCCA Spec Miata',          'APEX2026', 'Approved'),
    ('Redline Motorsport',   'F4 US',                    'RDLN7788', 'Approved'),
    ('Garage 51',            'NASA ST4',                 'GRG51ABC', 'Approved'),
    ('Velocity Works',       'SCCA Touring 3',           'VLCY2026', 'Approved'),
    ('Nova Autosport',       'USF Juniors',              'NOVA4421', 'Approved'),
    ('Summit Racing Lab',    'NASA Spec E30',            'SUMMIT26', 'Approved'),
    ('Iron Peak Racing',     'SCCA GT2',                 'IRON9931', 'Approved'),
    ('Pacific Dynamics',     'IMSA VP Racing Challenge', 'PACF2026', 'Approved'),
    ('Blue Arrow Racing',    'Formula Regional Americas','BLUE7712', 'Approved'),
    ('Vertex Motorsport',    'Porsche Sprint Challenge', 'VRTX5500', 'Approved');

-- ---------------------------------------------------------------- users -----
-- The same role-specific PBKDF2 hashes are reused for seed/demo accounts.
-- user_id layout:
--   1 = admin
--   team 1: manager 2, engineer 3, driver 4
--   team 2: manager 5, engineer 6, driver 7
--   ...
--   team 10: manager 29, engineer 30, driver 31

INSERT INTO users
(team_id, email, password_hash, display_name, phone, driver_number, role)
VALUES
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

    (2, 'manager@redline.example',
     'pbkdf2-sha256$210000$Ed9d31jZxyK6Wvn3IE8OxQ==$B9EDMb9HY9WWgMTnsA7XpVXxqM5J6dfVXlFdB/lHw7k=',
     'Elena Brooks', '415-555-0201', NULL, 'Manager'),
    (2, 'engineer@redline.example',
     'pbkdf2-sha256$210000$gPdINv+Bu691O6uoWO6nXA==$PTHXJMQoDtm9Lil7SqWbgxpROzLU63iyd32wjc/JuDI=',
     'Marcus Lee', '415-555-0202', NULL, 'Engineer'),
    (2, 'rookie@redline.example',
     'pbkdf2-sha256$210000$Wq2cEwpVGqzCgT+c03IZUA==$byc1Eeo8r5WcCFGf45IY2nmkA5xLA1xmQpiP1QsY/JY=',
     'Sam Okafor', '415-555-0203', 7, 'Driver'),

    (3, 'manager@garage51.example',
     'pbkdf2-sha256$210000$Ed9d31jZxyK6Wvn3IE8OxQ==$B9EDMb9HY9WWgMTnsA7XpVXxqM5J6dfVXlFdB/lHw7k=',
     'Jorge Ramirez', '650-555-0301', NULL, 'Manager'),
    (3, 'engineer@garage51.example',
     'pbkdf2-sha256$210000$gPdINv+Bu691O6uoWO6nXA==$PTHXJMQoDtm9Lil7SqWbgxpROzLU63iyd32wjc/JuDI=',
     'Hannah Kim', '650-555-0302', NULL, 'Engineer'),
    (3, 'driver@garage51.example',
     'pbkdf2-sha256$210000$k03cxmhN5acf9qYyOwSoVQ==$IbkyK35PSUlAOUcts+mUrwbpzf6gNTIMhtSPI2EUh9I=',
     'Liam Chen', '650-555-0303', 23, 'Driver'),

    (4, 'manager@velocity.example',
     'pbkdf2-sha256$210000$Ed9d31jZxyK6Wvn3IE8OxQ==$B9EDMb9HY9WWgMTnsA7XpVXxqM5J6dfVXlFdB/lHw7k=',
     'Priya Shah', '510-555-0401', NULL, 'Manager'),
    (4, 'engineer@velocity.example',
     'pbkdf2-sha256$210000$gPdINv+Bu691O6uoWO6nXA==$PTHXJMQoDtm9Lil7SqWbgxpROzLU63iyd32wjc/JuDI=',
     'Ethan Walker', '510-555-0402', NULL, 'Engineer'),
    (4, 'driver@velocity.example',
     'pbkdf2-sha256$210000$k03cxmhN5acf9qYyOwSoVQ==$IbkyK35PSUlAOUcts+mUrwbpzf6gNTIMhtSPI2EUh9I=',
     'Sofia Martinez', '510-555-0403', 18, 'Driver'),

    (5, 'manager@nova.example',
     'pbkdf2-sha256$210000$Ed9d31jZxyK6Wvn3IE8OxQ==$B9EDMb9HY9WWgMTnsA7XpVXxqM5J6dfVXlFdB/lHw7k=',
     'Noah Bennett', '916-555-0501', NULL, 'Manager'),
    (5, 'engineer@nova.example',
     'pbkdf2-sha256$210000$gPdINv+Bu691O6uoWO6nXA==$PTHXJMQoDtm9Lil7SqWbgxpROzLU63iyd32wjc/JuDI=',
     'Grace Liu', '916-555-0502', NULL, 'Engineer'),
    (5, 'driver@nova.example',
     'pbkdf2-sha256$210000$k03cxmhN5acf9qYyOwSoVQ==$IbkyK35PSUlAOUcts+mUrwbpzf6gNTIMhtSPI2EUh9I=',
     'Lucas Nguyen', '916-555-0503', 55, 'Driver'),

    (6, 'manager@summit.example',
     'pbkdf2-sha256$210000$Ed9d31jZxyK6Wvn3IE8OxQ==$B9EDMb9HY9WWgMTnsA7XpVXxqM5J6dfVXlFdB/lHw7k=',
     'Isabella Rossi', '707-555-0601', NULL, 'Manager'),
    (6, 'engineer@summit.example',
     'pbkdf2-sha256$210000$gPdINv+Bu691O6uoWO6nXA==$PTHXJMQoDtm9Lil7SqWbgxpROzLU63iyd32wjc/JuDI=',
     'Daniel Park', '707-555-0602', NULL, 'Engineer'),
    (6, 'driver@summit.example',
     'pbkdf2-sha256$210000$k03cxmhN5acf9qYyOwSoVQ==$IbkyK35PSUlAOUcts+mUrwbpzf6gNTIMhtSPI2EUh9I=',
     'Chloe Morgan', '707-555-0603', 31, 'Driver'),

    (7, 'manager@ironpeak.example',
     'pbkdf2-sha256$210000$Ed9d31jZxyK6Wvn3IE8OxQ==$B9EDMb9HY9WWgMTnsA7XpVXxqM5J6dfVXlFdB/lHw7k=',
     'Andre Williams', '408-555-0701', NULL, 'Manager'),
    (7, 'engineer@ironpeak.example',
     'pbkdf2-sha256$210000$gPdINv+Bu691O6uoWO6nXA==$PTHXJMQoDtm9Lil7SqWbgxpROzLU63iyd32wjc/JuDI=',
     'Maya Singh', '408-555-0702', NULL, 'Engineer'),
    (7, 'driver@ironpeak.example',
     'pbkdf2-sha256$210000$k03cxmhN5acf9qYyOwSoVQ==$IbkyK35PSUlAOUcts+mUrwbpzf6gNTIMhtSPI2EUh9I=',
     'Owen Carter', '408-555-0703', 72, 'Driver'),

    (8, 'manager@pacific.example',
     'pbkdf2-sha256$210000$Ed9d31jZxyK6Wvn3IE8OxQ==$B9EDMb9HY9WWgMTnsA7XpVXxqM5J6dfVXlFdB/lHw7k=',
     'Natalie Foster', '831-555-0801', NULL, 'Manager'),
    (8, 'engineer@pacific.example',
     'pbkdf2-sha256$210000$gPdINv+Bu691O6uoWO6nXA==$PTHXJMQoDtm9Lil7SqWbgxpROzLU63iyd32wjc/JuDI=',
     'Victor Hernandez', '831-555-0802', NULL, 'Engineer'),
    (8, 'driver@pacific.example',
     'pbkdf2-sha256$210000$k03cxmhN5acf9qYyOwSoVQ==$IbkyK35PSUlAOUcts+mUrwbpzf6gNTIMhtSPI2EUh9I=',
     'Ava Thompson', '831-555-0803', 9, 'Driver'),

    (9, 'manager@bluearrow.example',
     'pbkdf2-sha256$210000$Ed9d31jZxyK6Wvn3IE8OxQ==$B9EDMb9HY9WWgMTnsA7XpVXxqM5J6dfVXlFdB/lHw7k=',
     'Carlos Mendes', '925-555-0901', NULL, 'Manager'),
    (9, 'engineer@bluearrow.example',
     'pbkdf2-sha256$210000$gPdINv+Bu691O6uoWO6nXA==$PTHXJMQoDtm9Lil7SqWbgxpROzLU63iyd32wjc/JuDI=',
     'Emily Johnson', '925-555-0902', NULL, 'Engineer'),
    (9, 'driver@bluearrow.example',
     'pbkdf2-sha256$210000$k03cxmhN5acf9qYyOwSoVQ==$IbkyK35PSUlAOUcts+mUrwbpzf6gNTIMhtSPI2EUh9I=',
     'Mateo Silva', '925-555-0903', 27, 'Driver'),

    (10, 'manager@vertex.example',
     'pbkdf2-sha256$210000$Ed9d31jZxyK6Wvn3IE8OxQ==$B9EDMb9HY9WWgMTnsA7XpVXxqM5J6dfVXlFdB/lHw7k=',
     'Rachel Adams', '669-555-1001', NULL, 'Manager'),
    (10, 'engineer@vertex.example',
     'pbkdf2-sha256$210000$gPdINv+Bu691O6uoWO6nXA==$PTHXJMQoDtm9Lil7SqWbgxpROzLU63iyd32wjc/JuDI=',
     'Kenji Sato', '669-555-1002', NULL, 'Engineer'),
    (10, 'driver@vertex.example',
     'pbkdf2-sha256$210000$k03cxmhN5acf9qYyOwSoVQ==$IbkyK35PSUlAOUcts+mUrwbpzf6gNTIMhtSPI2EUh9I=',
     'Zoe Turner', '669-555-1003', 63, 'Driver');

-- ---------------------------------------------------------------- tracks ----
INSERT INTO tracks (track_name, location, length_km, turns) VALUES
    ('WeatherTech Raceway Laguna Seca', 'Monterey, California', 3.602, 11),
    ('Sonoma Raceway',                  'Sonoma, California',   4.056, 12),
    ('Circuit of the Americas',         'Austin, Texas',        5.513, 20),
    ('Road America',                    'Elkhart Lake, Wisconsin', 6.515, 14),
    ('Watkins Glen International',      'Watkins Glen, New York', 5.472, 11),
    ('Sebring International Raceway',   'Sebring, Florida',     6.019, 17),
    ('Michelin Raceway Road Atlanta',   'Braselton, Georgia',   4.088, 12),
    ('Virginia International Raceway',  'Alton, Virginia',      5.263, 17),
    ('Mid-Ohio Sports Car Course',      'Lexington, Ohio',      3.634, 13),
    ('Indianapolis Motor Speedway Road Course', 'Indianapolis, Indiana', 3.925, 14);

-- ---------------------------------------------------------------- cars ------
INSERT INTO cars (team_id, chassis_no, model, car_class, active) VALUES
    (1,  'APX-MX5-001', 'Mazda MX-5 ND',        'Spec Miata',          1),
    (2,  'RDL-F4-007',  'Ligier JS F4',         'Formula 4',           1),
    (3,  'G51-E36-023', 'BMW E36 325i',         'NASA ST4',            1),
    (4,  'VLW-BRZ-018', 'Subaru BRZ',           'SCCA Touring 3',      1),
    (5,  'NVA-JR-055',  'Tatuus JR-23',         'USF Juniors',         1),
    (6,  'SMT-E30-031', 'BMW E30 325is',        'Spec E30',            1),
    (7,  'IPR-GT2-072', 'Chevrolet Corvette',   'SCCA GT2',            1),
    (8,  'PCD-LMP3-009','Ligier JS P320',       'LMP3',                1),
    (9,  'BAR-FR-027',  'Tatuus FR-19',         'Formula Regional',    1),
    (10, 'VTX-992-063', 'Porsche 911 GT3 Cup',  'Porsche Cup',         1);

-- ------------------------------------------------------------- tire_sets ----
INSERT INTO tire_sets
(team_id, set_no, compound, heat_cycles, max_heat_cycles, tire_size, status, received_date)
VALUES
    (1,  1, 'Dry Medium', 2, 8, '205/50R15', 'Available', '2026-03-01'),
    (2,  1, 'Dry Soft',   1, 5, '180/550R13', 'Available', '2026-03-03'),
    (3,  1, 'Dry Medium', 3, 8, '245/40R17', 'Used',      '2026-03-05'),
    (4,  1, 'Dry Hard',   2, 10,'245/40R17', 'Available', '2026-03-07'),
    (5,  1, 'Dry Soft',   1, 5, '180/550R13', 'Available', '2026-03-09'),
    (6,  1, 'Dry Medium', 4, 9, '205/50R15', 'Used',      '2026-03-11'),
    (7,  1, 'Dry Soft',   2, 6, '300/680R18', 'Available', '2026-03-13'),
    (8,  1, 'Dry Medium', 1, 6, '310/710R18', 'Available', '2026-03-15'),
    (9,  1, 'Dry Soft',   2, 5, '180/550R13', 'Used',      '2026-03-17'),
    (10, 1, 'Dry Medium', 1, 7, '325/680R18', 'Available', '2026-03-19');

-- ------------------------------------------------------------- car_setups ---
INSERT INTO car_setups
(car_id, setup_name, front_wing_angle, rear_wing_angle, ride_height_mm,
 tire_pressure_psi, spring_rate, notes)
VALUES
    (1,  'Laguna Baseline',   4.00,  6.00, 82.00, 28.50, 450.00, 'Neutral balance for medium-speed corners'),
    (2,  'Sonoma Qualifying', 8.50, 10.00, 35.00, 19.50, 700.00, 'Higher downforce for qualifying'),
    (3,  'COTA Endurance',    3.00,  5.00, 90.00, 30.00, 500.00, 'Stable setup for long stints'),
    (4,  'Road America',      2.50,  4.00, 85.00, 29.00, 525.00, 'Reduced drag on long straights'),
    (5,  'Watkins Glen',      7.50,  9.00, 34.00, 19.00, 710.00, 'Fast-corner aero balance'),
    (6,  'Sebring Compliance',3.00,  5.50, 95.00, 28.00, 420.00, 'Softer setup for bumps'),
    (7,  'Road Atlanta GT2',  6.00,  8.50, 70.00, 27.50, 800.00, 'Stable through Esses'),
    (8,  'VIR Prototype',     9.00, 11.00, 45.00, 24.00, 900.00, 'High-speed aero stability'),
    (9,  'Mid-Ohio Formula',  8.00, 10.50, 35.00, 18.50, 720.00, 'Rotation for technical section'),
    (10, 'Indy Road Course',  5.50,  7.00, 65.00, 26.00, 850.00, 'Balanced braking and traction');

-- --------------------------------------------------------------- drivers ----
INSERT INTO drivers (user_id, driver_number, license_class, years_racing) VALUES
    (4,  44, 'SCCA Full Competition', 5),
    (7,   7, 'FIA Grade C',           2),
    (10, 23, 'NASA Competition',      4),
    (13, 18, 'SCCA Full Competition', 6),
    (16, 55, 'FIA Grade C',           3),
    (19, 31, 'NASA Competition',      5),
    (22, 72, 'SCCA Full Competition', 8),
    (25,  9, 'FIA Grade B',           7),
    (28, 27, 'FIA Grade C',           4),
    (31, 63, 'FIA Grade B',           6);

-- --------------------------------------------------------- race_engineers ----
INSERT INTO race_engineers (user_id, specialty, years_experience) VALUES
    (3,  'Vehicle Dynamics', 8),
    (6,  'Aerodynamics', 6),
    (9,  'Data Acquisition', 7),
    (12, 'Suspension', 5),
    (15, 'Telemetry', 4),
    (18, 'Chassis Setup', 9),
    (21, 'Powertrain', 10),
    (24, 'Aerodynamics', 8),
    (27, 'Simulation', 6),
    (30, 'Tire Performance', 7);

-- ---------------------------------------------------------- team_managers ----
INSERT INTO team_managers (user_id, office_phone, budget_authority) VALUES
    (2,  '408-555-1101', 150000.00),
    (5,  '415-555-1102', 250000.00),
    (8,  '650-555-1103', 180000.00),
    (11, '510-555-1104', 175000.00),
    (14, '916-555-1105', 300000.00),
    (17, '707-555-1106', 160000.00),
    (20, '408-555-1107', 400000.00),
    (23, '831-555-1108', 650000.00),
    (26, '925-555-1109', 350000.00),
    (29, '669-555-1110', 500000.00);

-- ---------------------------------------------------------------- events ----
INSERT INTO events
(event_name, track_id, team_id, start_date, end_date, event_type)
VALUES
    ('Laguna Seca Spring Test',       1,  1, '2026-03-20', '2026-03-20', 'Test Day'),
    ('Sonoma F4 Weekend',             2,  2, '2026-04-03', '2026-04-05', 'Race Weekend'),
    ('COTA ST4 Challenge',            3,  3, '2026-04-17', '2026-04-19', 'Race Weekend'),
    ('Road America Touring Weekend',  4,  4, '2026-05-01', '2026-05-03', 'Race Weekend'),
    ('Watkins Glen Junior Formula',   5,  5, '2026-05-15', '2026-05-17', 'Race Weekend'),
    ('Sebring Spec E30 Weekend',      6,  6, '2026-06-05', '2026-06-07', 'Race Weekend'),
    ('Road Atlanta GT2 Round',        7,  7, '2026-06-19', '2026-06-21', 'Race Weekend'),
    ('VIR Prototype Challenge',       8,  8, '2026-07-10', '2026-07-12', 'Race Weekend'),
    ('Mid-Ohio Formula Regional',     9,  9, '2026-07-24', '2026-07-26', 'Race Weekend'),
    ('Indy Porsche Sprint Weekend',  10, 10, '2026-08-14', '2026-08-16', 'Race Weekend');

-- --------------------------------------------------------------- sessions ---
-- setup_id is NULL in a couple rows to demonstrate the 0-or-1 setup relationship.
INSERT INTO sessions
(event_id, car_id, setup_id, driver_user_id, session_type, session_date,
 weather, air_temp_c, driver_feedback)
VALUES
    (1,  1,  1,    4,  'Test',       '2026-03-20', 'Clear',        18.50, 'Good front grip; slight oversteer at corner exit'),
    (2,  2,  2,    7,  'Qualifying', '2026-04-04', 'Sunny',       21.00, 'Car feels stable under braking'),
    (3,  3,  NULL, 10, 'Practice',   '2026-04-17', 'Overcast',    24.00, 'Need more rear traction in slow corners'),
    (4,  4,  4,    13, 'Race',       '2026-05-03', 'Clear',       19.50, 'Balance stayed consistent through the stint'),
    (5,  5,  5,    16, 'Qualifying', '2026-05-16', 'Cloudy',      17.00, 'Strong high-speed grip'),
    (6,  6,  6,    19, 'Practice',   '2026-06-05', 'Hot',         31.50, 'Bumps are upsetting the rear axle'),
    (7,  7,  NULL, 22, 'Race',       '2026-06-21', 'Humid',       29.00, 'Good pace but front tires faded late'),
    (8,  8,  8,    25, 'Practice',   '2026-07-10', 'Sunny',       27.50, 'Very stable through high-speed sections'),
    (9,  9,  9,    28, 'Qualifying', '2026-07-25', 'Light Rain',  20.00, 'Need more confidence on corner entry'),
    (10, 10, 10,   31, 'Race',       '2026-08-16', 'Clear',       26.00, 'Rear traction was strong throughout');

-- ---------------------------------------------------------------- laps ------
-- Two laps per session gives 20 lap tuples total.
INSERT INTO laps (session_id, lap_number, lap_time_sec, is_valid) VALUES
    (1, 1, 102.441, 1), (1, 2, 101.982, 1),
    (2, 1,  94.221, 1), (2, 2,  93.875, 1),
    (3, 1, 145.882, 1), (3, 2, 144.913, 0),
    (4, 1, 154.332, 1), (4, 2, 153.908, 1),
    (5, 1, 110.774, 1), (5, 2, 110.115, 1),
    (6, 1, 161.445, 1), (6, 2, 160.992, 1),
    (7, 1,  88.601, 1), (7, 2,  88.247, 0),
    (8, 1, 109.744, 1), (8, 2, 109.201, 1),
    (9, 1,  82.992, 1), (9, 2,  82.510, 1),
    (10,1,  91.663, 1), (10,2,  91.208, 1);

-- ---------------------------------------------------------------- uses ------
INSERT INTO uses (session_id, team_id, set_no, laps_run) VALUES
    (1,  1,  1, 18),
    (2,  2,  1, 16),
    (3,  3,  1, 22),
    (4,  4,  1, 24),
    (5,  5,  1, 15),
    (6,  6,  1, 20),
    (7,  7,  1, 19),
    (8,  8,  1, 21),
    (9,  9,  1, 17),
    (10, 10, 1, 23);

