# Architecture — PurrCore

PurrCore is a tiny library plugin. Other plugins depend on it to share one DB pool and translations.

```
Paper
  └─ PurrCorePlugin (JavaPlugin)
       ├─ Database (HikariCP) — sqlite/mysql/pg, migrate(), getConnection()
       └─ I18n — loads lang/en.yml, de.yml, t(key)
```

## Modules

- `PurrCorePlugin` — onEnable: load i18n, connect DB, register service
- `db/Database` — Hikari config from config.yml, sqlite pool 1, others 10
- `i18n/I18n` — simple yaml loader with fallback

Other plugins get DB via `PurrCore.get().database` or own pool if not present.

## Threading

DB calls off main thread via `asyncScheduler`. Never block main.
