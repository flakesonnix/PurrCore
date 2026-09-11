# PurrCore

Paper & Spigot — Kotlin — shared core for our plugins.

Provides HikariCP database (sqlite/mysql/pg) and I18n (en/de) so other plugins stay small. Other plugins declare `depend: [PurrCore]` and call `PurrCore.get()`.

```bash
gradle shadowJar
# → build/libs/purrcore-1.0.0.jar
```

Drop in `plugins/` first, then add dependents like EasyTPA or EasyScoreboard.

## What it does

- **DB** — one `database.db` (or MySQL/Postgres) shared via `Database` wrapper, pooled, auto-migrates
- **I18n** — `lang/en.yml`, `lang/de.yml`, easy `i18n.t("key")`
- Loaded before dependents, exposes `PurrCorePlugin.Instance`

## Config

`plugins/PurrCore/config.yml`:

```yaml
language: en # en, de
database:
  type: sqlite
  sqlite: { file: database.db }
  mysql: { host: localhost, database: minecraft }
```

## Dev

```bash
nix develop
gradle spotlessApply && nix fmt
gradle shadowJar
gradle publishToMavenLocal # for local depend testing
```

See `docs/` for DB and i18n details.
