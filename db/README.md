# Database setup

Pitwall stores everything in a MySQL database named `pitwall`.

## First time setup

1. Open MySQL Workbench and connect to your local server.
2. Run `01_schema.sql` — creates the database and the `teams` / `users` tables.
3. Run `02_seed.sql` — loads the test data every team member shares.
4. Copy `src/main/java/myConfig.example.json` to `src/main/java/myConfig.json`
   and fill in your own MySQL user and password. That file is git-ignored,
   so your password never reaches GitHub.

Re-running `01_schema.sql` drops the tables, so all rows are lost. Re-running
`02_seed.sql` alone is safe and resets the data back to a known state.

## Test accounts

| Email | Password | Role | Team |
|---|---|---|---|
| admin@pitwall.example | admin123 | Admin | (none) |
| manager@apex.example | manager123 | Manager | Apex Racing |
| engineer@apex.example | engineer123 | Engineer | Apex Racing |
| driver@apex.example | driver123 | Driver | Apex Racing (car 44) |
| rookie@redline.example | rookie123 | Driver | Redline Motorsport (car 7) |

These passwords are test data only. Never reuse them for a real account.

To add another account, generate a hash first:

```
java cs157a.util.PasswordHasher myNewPassword
```

Paste the printed string into the `password_hash` column.

## Naming conventions

Agreed by the team — follow these for every new table.

| Rule | Example |
|---|---|
| Table names are plural, lower snake_case | `teams`, `setup_sheets` |
| Primary key is singular table name + `_id` | `team_id` |
| Foreign key keeps the name of the key it points at | `users.team_id` |
| Timestamps end in `_at` | `created_at`, `updated_at` |
| Booleans are a `TINYINT(1)` flag | `active` |
| Fixed value sets are a plain `VARCHAR`, with the legal values written in a comment next to the column | `role` is Driver / Engineer / Manager / Admin |

We keep the schema deliberately simple: `PRIMARY KEY`, `UNIQUE`, `NOT NULL` and
`FOREIGN KEY` only. No `CHECK` constraints and no extra indexes, so the whole
file stays readable. The application is responsible for only writing legal
values into columns such as `status` and `role`.

Lower case matters: MySQL on Windows ignores table-name case but MySQL on
Linux does not, so a query that works locally can fail on another machine.

## Tables so far

### teams
Root of the schema. Every other table will carry a `team_id` so one team can
never read another team's data.

### users
One account, one role, one team. `team_id` is NULL for the system
administrator because the admin belongs to no team.
