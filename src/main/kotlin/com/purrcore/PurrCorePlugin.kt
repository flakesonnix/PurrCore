package com.purrcore

import com.purrcore.db.Database
import com.purrcore.i18n.I18n
import org.bukkit.plugin.java.JavaPlugin

/**
 * PurrCore — shared core for all plugins (Paper & Spigot).
 * Provides: HikariCP Database (sqlite/mysql/postgresql), I18n (lang files), lightweight utils.
 * Other plugins: `depend: [PurrCore]` + `PurrCore.get()` to share pool & translations.
 */
class PurrCorePlugin : JavaPlugin() {
    lateinit var database: Database
        private set
    lateinit var i18n: I18n
        private set

    override fun onEnable() {
        saveDefaultConfig()
        // i18n — load before DB so DB logs are translated if needed
        i18n = I18n(this)
        i18n.load()

        database = Database(this)
        try {
            database.connect()
            database.migrate()
        } catch (e: Exception) {
            logger.severe("PurrCore DB failed: ${e.message}")
            e.printStackTrace()
        }

        // expose as singleton + Bukkit ServicesManager (optional)
        Instance = this
        try {
            server.servicesManager.register(PurrCorePlugin::class.java, this, this, org.bukkit.plugin.ServicePriority.Normal)
        } catch (_: Exception) {
        }

        logger.info("PurrCore enabled (DB=${if (database.isConnected()) "ok" else "off"}) — use everywhere")
    }

    override fun onDisable() {
        if (::database.isInitialized) database.close()
        Instance = null
        logger.info("PurrCore disabled")
    }

    companion object {
        @Suppress("PropertyName")
        var Instance: PurrCorePlugin? = null
            private set

        fun get(): PurrCorePlugin = Instance ?: throw IllegalStateException("PurrCore not enabled — add 'depend: [PurrCore]' to plugin.yml")
        fun isAvailable(): Boolean = Instance != null
    }
}
