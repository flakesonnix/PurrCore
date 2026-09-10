package com.purrcore.db

import io.mockk.every
import io.mockk.mockk
import java.io.File
import java.nio.file.Files
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class DatabaseTest {
    @TempDir
    lateinit var tempDir: File

    private fun mockPlugin(): JavaPlugin {
        val plugin = mockk<JavaPlugin>(relaxed = true)
        val config = YamlConfiguration()
        config.set("database.type", "sqlite")
        config.set("database.sqlite.file", "test.db")
        config.set("database.pool.maximum-pool-size", 1)
        config.set("database.pool.minimum-idle", 1)
        config.set("database.pool.connection-timeout", 10000)
        config.set("database.pool.idle-timeout", 60000)
        config.set("database.pool.max-lifetime", 600000)
        every { plugin.config } returns config
        every { plugin.dataFolder } returns tempDir
        every { plugin.logger } returns mockk(relaxed = true)
        return plugin
    }

    @Test
    fun `connect and migrate sqlite`() {
        val plugin = mockPlugin()
        val db = Database(plugin)
        db.connect()
        assertTrue(db.isConnected())
        assertTrue(db.isSqlite())
        db.migrate()
        // check table exists
        db.getConnection().use { c ->
            c.createStatement().use { s ->
                s.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='core_players'").use { rs ->
                    assertTrue(rs.next(), "core_players table should exist")
                }
            }
        }
        db.close()
        assertFalse(db.isConnected())
    }

    @Test
    fun `isConnected false before connect`() {
        val plugin = mockPlugin()
        val db = Database(plugin)
        assertFalse(db.isConnected())
    }

    @Test
    fun `getConnection throws before connect`() {
        val plugin = mockPlugin()
        val db = Database(plugin)
        try {
            db.getConnection()
            assert(false) { "should throw" }
        } catch (e: Exception) {
            assertTrue(e.message!!.contains("not initialized"))
        }
    }
}
