# Database — PurrCore

One HikariCP pool shared by all plugins.

- **sqlite** default — file `plugins/PurrCore/database.db`, pool 1
- **mysql / postgresql** — set host/db/user/pass, pool 10

```kotlin
val db = PurrCore.get().database
db.getConnection().use { c -> c.prepareStatement("SELECT 1").executeQuery() }
```

Migrations run in `Database.migrate()` — `CREATE TABLE IF NOT EXISTS`. Add tables there if you extend PurrCore.

Docker for testing: `docker compose up -d` (if you switch to mysql).
