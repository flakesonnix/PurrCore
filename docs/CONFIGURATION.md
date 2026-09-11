# Configuration — PurrCore

`plugins/PurrCore/config.yml` on first run:

```yaml
language: en # en, de
database:
  type: sqlite
  sqlite: { file: database.db }
  mysql: { host: localhost, database: minecraft, user: root, password: "..." }
```

Change `language` or `database.type` → restart. Dependents can share the same file or use own.

See `config.yml` in resources for full defaults.
