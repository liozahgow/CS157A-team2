# Pitwall — CS157A Team 2

A web platform that helps grassroots racing teams record, organize and learn
from everything that happens at the track. See `references/` for the project
proposal and the full list of functional requirements.

**Stack:** Java 25 · Jakarta Servlet 6 / JSP · Apache Tomcat 11 · MySQL · Maven

---

## 1. Get it running (from a fresh clone)

Follow these in order. Steps 3 and 4 are the ones people usually miss.

### Prerequisites

| Tool | Version we use |
|---|---|
| JDK | 25 |
| Eclipse IDE | 2026-09, **Enterprise Java and Web Developers** edition |
| Apache Tomcat | 11.0 |
| MySQL Community Server | 26.7 (plus MySQL Workbench) |
| Git | any recent version |

The plain "Eclipse IDE for Java Developers" edition will not work — it has no
server tooling. Download the Enterprise Java and Web Developers package.

### Step 1 — Clone

```
git clone https://github.com/liozahgow/CS157A-team2.git
```

### Step 2 — Import into Eclipse

`File` → `Import...` → `Maven` → **Existing Maven Projects** → pick the cloned
folder → `Finish`.

Do **not** use `Import` → `General` → `Existing Projects into Workspace`. The
Eclipse metadata (`.classpath`, `.project`, `.settings/`) is deliberately kept
out of Git because it stores absolute paths from one person's machine.
Importing as a Maven project makes Eclipse generate your own copy.

### Step 3 — Point the project at Tomcat 11

Eclipse needs to know which server the project targets, otherwise the JSP
editor cannot find the Servlet API.

1. `Window` → `Preferences` → `Server` → `Runtime Environments` → `Add...`
2. Choose **Apache Tomcat v11.0**, browse to your Tomcat install folder, `Finish`
3. Right-click the project → `Properties` → `Targeted Runtimes` → tick
   **Apache Tomcat v11.0** → `Apply and Close`
4. Right-click the project → `Maven` → `Update Project...` → tick
   **Force Update of Snapshots/Releases** → `OK`

Check `Properties` → `Project Facets`: **Dynamic Web Module** must read **6.0**.
If it says 2.4, see the troubleshooting table below.

### Step 4 — Create your local config file

The app reads the database credentials from a file that is **not** in Git.

1. Copy `src/main/java/myConfig.example.json`
2. Rename the copy to `src/main/java/myConfig.json` (same folder)
3. Fill in your own MySQL user and password

`myConfig.json` is listed in `.gitignore`. Never remove that line and never
commit the file — it holds your real password.

### Step 5 — Build the database

Open MySQL Workbench and run, in this order:

1. `db/01_schema.sql` — creates the `pitwall` database and its tables
2. `db/02_seed.sql` — loads the shared test data

Everyone runs the same seed so we all test against identical rows. See
[db/README.md](db/README.md) for details and the naming conventions.

### Step 6 — Run it

Right-click `src/main/webapp/index.jsp` → `Run As` → `Run on Server` → pick your
Tomcat 11 → `Finish`.

The browser opens at
`http://localhost:8080/my_cs_157a_group_project/index.jsp`.

### Step 7 — Log in

| Email | Password | Role |
|---|---|---|
| manager@apex.example | manager123 | Manager |
| engineer@apex.example | engineer123 | Engineer |
| driver@apex.example | driver123 | Driver |
| admin@pitwall.example | admin123 | Admin (belongs to no team) |

Press **List all racing teams** — three rows from the `teams` table appear.
If you got that far, your environment is correct.

---

## 2. Troubleshooting

Every one of these has actually happened to us.

| Symptom | Cause and fix |
|---|---|
| `The default superclass "javax.servlet.http.HttpServlet" ... was not found` | The Dynamic Web Module facet is 2.4, which expects the old `javax` API, but Tomcat 11 uses `jakarta`. Redo Step 3; the facet must be 6.0. |
| `No suitable driver found for jdbc:mysql://...` | The JDBC driver is not registered. Tomcat initialises DriverManager before any webapp loads, so a driver in `WEB-INF/lib` is never auto-discovered. `Db.java` fixes this with `Class.forName`. If you still see it, the driver never reached `WEB-INF/lib` — run `Maven` → `Update Project`, then clean the server. |
| `Cannot find /myConfig.json on the classpath` | Step 4 not done, or the file is in the wrong folder. It belongs in `src/main/java/`, next to `myConfig.example.json`. |
| `Table 'pitwall.teams' doesn't exist` | Step 5 not done. Run `db/01_schema.sql`, then `db/02_seed.sql`. |
| `Access denied for user ... ` | Wrong MySQL user or password in `myConfig.json`. |
| The background image or the CSS does not load | Tomcat is serving a stale copy. Servers view → right-click the server → `Clean...`, then `Clean Tomcat Work Directory...`, then `Start`. Hard-refresh the browser with Ctrl+F5. |
| A source change has no effect | Three different refreshes exist and they do different things. **F5** makes Eclipse re-read the disk, `Project` → `Clean...` recompiles the classes, and the server's `Clean...` re-deploys to Tomcat. After editing files outside Eclipse you need all three. |
| `cannot find symbol: MyConfig`, but only inside a JSP | A JSP cannot import a class from the default package. Every class we write lives in a `cs157a.*` package for this reason. |
| `import` inside a `<% %>` block does not compile | Scriptlet code ends up inside a method, and Java has no method-level imports. Use `<%@ page import="..." %>` at the top of the file. |

---

## 3. Project structure

```
your-project-root-dir/
├── db/                  ★ SQL scripts (DDL and DML) that WE must write ourselves.
│   ├── 01_schema.sql         ★ DDL. Feed straight into Workbench to create the database and tables.
│   ├── 02_seed.sql           ★ INSERT statements: fake rows for testing. Everyone uses the same data.
│   └── README.md             ★ How to use the files in db/. Our ER diagram can go here later.
│
├── references/          ★ Project proposal and functional requirements PDFs.
│
├── src/main/java/cs157a/
│   ├── util/            ★ Shared helpers. Nothing here runs a business query, though opening a
│   │                      connection is fine. MyConfig reads the JSON config, Db hands out
│   │                      connections, PasswordHasher hashes and verifies passwords.
│   ├── model/           ★ Plain Java classes that mirror one table row. "CREATE TABLE teams"
│   │                      maps to "public class Team". No SQL here, ever.
│   ├── dao/             ★ Data Access Object. THE ONLY PLACE SQL IS ALLOWED. Runs the query,
│   │                      turns each ResultSet row into a model object, returns a List.
│   ├── service/         ★ (not created yet) For an action that needs several statements in one
│   │                      transaction. A single statement goes straight in the DAO.
│   └── web/
│       ├── filter/      ★ AuthFilter blocks any /app/ URL when nobody is logged in.
│       └── *Servlet     ★ Flow control: read the request, pick the query, call the DAO, put the
│                          result in the request, forward to a JSP. No SQL, no HTML.
│
└── src/main/webapp/     ★ Everything the browser deals with.
    ├── WEB-INF/views/   ★ Our JSPs. The browser CANNOT open these directly, so every page has
    │                      to come through a servlet, and therefore through AuthFilter.
    ├── css/ images/     ★ Static resources, public on purpose.
    └── index.jsp        ★ The only public page: the login form.
```

### How one request flows

```
  browser  GET /                 -> index.jsp                    login form
  browser  POST /login           -> LoginServlet
                                    -> UserDAO.findByEmailAndPassword()
                                       -> SELECT ... WHERE email = ?
                                       -> PasswordHasher.verify()
                                    -> session.setAttribute("user", user)
                                    -> redirect /app/home
  browser  GET /app/home         -> AuthFilter  (not logged in -> back to index.jsp)
                                    -> HomeServlet -> forward -> home.jsp
  user presses the button
           GET /app/teams        -> AuthFilter -> TeamServlet
                                    -> TeamDAO.findAll() -> SQL -> List<Team>
                                    -> setAttribute("teams", ...) -> home.jsp
  home.jsp loops over the list and prints the table
```

`home.jsp` is rendered by two different servlets. `HomeServlet` sets no
`teams` attribute, so the page shows a hint; `TeamServlet` sets it, so the page
shows the table. Same view, different data — that is what the layering buys us.

---

## 4. Team conventions

Agreed rules. Please follow them so our code stays reviewable.

1. **English only inside files.** Code, comments, SQL, commit messages. We can
   chat in any language, but all three of us read the repo.
2. **SQL lives only in `cs157a.dao`.** A `SELECT` in a servlet or a JSP is a
   bug. This also keeps our own query work easy to point at.
3. **Always use `?` placeholders**, never string concatenation, for anything
   that came from a user.
4. **Every query that reads team data takes a `teamId` and puts it in the
   `WHERE` clause.** One team must never see another team's rows.
5. **Never commit `myConfig.json`.** If a password ever reaches GitHub, change
   the MySQL password first, then clean the history.
6. **Soft delete, never hard delete.** Set `active = 0` so history stays linked.
7. Table names are plural and lower snake_case; see `db/README.md` for the full
   naming table.

### Adding a new account

Generate a hash, then paste it into the `password_hash` column:

```
java cs157a.util.PasswordHasher myNewPassword
```

### Reading the config from your own code

```java
import cs157a.util.MyConfig;

String databaseName = MyConfig.get("database_name");
```

`MyConfig` reads `myConfig.json` once and caches it. For a database connection
use `cs157a.util.Db.get()` instead of building the URL yourself.

---

## 5. Status

**Done**

- `teams` and `users` tables, linked by a foreign key
- Login and logout, PBKDF2 password hashing, session handling
- `AuthFilter` guarding every `/app/` URL
- One full vertical slice to copy for the other tables:
  schema → seed → model → DAO → servlet → JSP

**Next**

- The remaining 8 tables: `cars`, `tracks`, `events`, `sessions`,
  `setup_sheets`, `tire_sets`, `session_tire_usages`, `laps`
- Registration (FR-1) and roster management (FR-3)
- `RoleFilter`, so a Driver cannot open Engineer-only pages
- FR-10 best-setup search — the multi-table JOIN, and the most important query
  in the whole project

### Legacy files, ignore them

`src/main/java/HW1_*.java`, `MysqlCon.java` and
`src/main/webapp/my_3_tier_demo.jsp` are left over from homework 1 and the
original JDBC demo. They are kept as a reference only and are not part of the
application.
