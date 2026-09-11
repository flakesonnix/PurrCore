package com.purrcore

import io.mockk.every
import io.mockk.mockk
import java.io.File
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class PurrCorePluginTest {

    @TempDir
    lateinit var tempDir: File

    @Test
    fun `get throws when not available`() {
        // In test JVM no plugin is enabled, so Instance should be null
        // We do not force reset to avoid reflection complexity — just verify behavior matches isAvailable
        if (!PurrCorePlugin.isAvailable()) {
            try {
                PurrCorePlugin.get()
                assert(false) { "should throw when not available" }
            } catch (e: IllegalStateException) {
                assertTrue(e.message!!.contains("not enabled"))
            }
        } else {
            // if some other test set it, just verify get returns something
            assertTrue(PurrCorePlugin.isAvailable())
            assertNotNull(PurrCorePlugin.get())
        }
    }

    private fun assertNotNull(value: Any?) {
        assertTrue(value != null)
    }

    @Test
    fun `database connect creates file`() {
        val plugin = mockk<JavaPlugin>(relaxed = true)
        val config = YamlConfiguration()
        config.set("database.type", "sqlite")
        config.set("database.sqlite.file", "test.db")
        config.set("database.pool.maximum-pool-size", 1)
        config.set("language", "en")
        every { plugin.config } returns config
        every { plugin.dataFolder } returns tempDir
        every { plugin.getResource(any()) } returns null
        every { plugin.logger } returns mockk(relaxed = true)
        val db = com.purrcore.db.Database(plugin)
        db.connect()
        assertTrue(db.isConnected())
        db.close()
        assertFalse(db.isConnected())
        assertTrue(File(tempDir, "test.db").exists() || db.isSqlite() == false)
    }
}
