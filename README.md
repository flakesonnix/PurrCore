# PurrCore — Shared Core for Paper & Spigot

[![CI](https://github.com/flakesonnix/PurrCore/actions/workflows/ci.yml/badge.svg)](https://github.com/flakesonnix/PurrCore/actions/workflows/ci.yml)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?logo=kotlin)](https://kotlinlang.org)
[![Paper](https://img.shields.io/badge/Paper-1.26.2-0288D1?logo=minecraft)](https://papermc.io)
[![Spigot](https://img.shields.io/badge/Spigot-compatible-ED8B00)](https://www.spigotmc.org)

**Whisker is now PurrCore** — lightweight shared core you use everywhere. Provides **HikariCP Database** (SQLite/MySQL/PostgreSQL) + **I18n** (en/de, `lang/*.yml`) + utils for all your plugins (Paper & Spigot).

> Built from [`flakesonnix/paper-kotlin-template`](https://github.com/flakesonnix/paper-kotlin-template) — Kotlin 2.0.21, Nix flake, Spotless/ktlint, IDEA, CI. EasyTPA and others `depend: [PurrCore]`.

## Why PurrCore?

- **One pool, one config** — `plugins/PurrCore/config.yml` + `database.db` + `lang/` shared, no per-plugin Hikari duplication
- **I18n everywhere** — `PurrCore.get().i18n.t("key")` + per-plugin lang files
- **Paper & Spigot** — only Bukkit API, no NMS

## Quick Start

```bash
# install core
cp build/libs/purrcore-1.0.0.jar plugins/
# restart — creates plugins/PurrCore/{config.yml,database.db,lang/en.yml}
```

Other plugins:

```yaml
# plugin.yml
depend: [PurrCore]
```

```kotlin
// in your plugin
val core = server.pluginManager.getPlugin("PurrCore") as com.purrcore.PurrCorePlugin
// or
val core = com.purrcore.PurrCorePlugin.get()
val db = core.database // Hikari
val i18n = core.i18n   // shared i18n
// or create own I18n(this) with own lang files
```

## Features

- **Database** — `com.purrcore.db.Database` (Hikari, `sqlite` default file `database.db`, `mysql`/`postgresql`, `migrate()` with `core_players`)
- **I18n** — `com.purrcore.i18n.I18n` (loads `lang/<code>.yml` from `plugins/PurrCore/lang/` or jar, fallback `en`, `prefix`, `t()` with `{placeholders}`, `ChatColor`)
- **Nix flake** — `jdk21` + `gradle` + `jetbrains.idea` + `nixfmt`/`ktlint` native; `nix develop`, `nix build` (impure, `sandbox relaxed`), `nix fmt`, `nix run .#idea`
- **Formatter** — `Spotless + ktlint 1.5.0` + `nixfmt`
- **IDEA** — `gradle idea`, committed `.idea/`

## Config

`src/main/resources/config.yml` → `plugins/PurrCore/config.yml`:

```yaml
language: en
database: { type: sqlite, sqlite: {file: database.db} }
```

See `lang/en.yml`, `lang/de.yml`.

## Development

```bash
nix develop
gradle spotlessApply && gradle shadowJar # → build/libs/purrcore-1.0.0.jar
nix fmt
gradle idea
nix run .#idea
```

## From Template

`gh repo create MyPlugin --template flakesonnix/paper-kotlin-template` then `depend: [PurrCore]`.

## License

MIT
