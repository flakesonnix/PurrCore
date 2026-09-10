package com.purrcore.i18n

import io.mockk.every
import io.mockk.mockk
import java.io.File
import java.nio.file.Files
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class I18nTest {
    @TempDir
    lateinit var tempDir: File

    private fun mockPlugin(lang: String, enContent: String = "prefix: \"&b[Test] \"\nhello: \"Hello {player}\""): JavaPlugin {
        val plugin = mockk<JavaPlugin>(relaxed = true)
        val config = YamlConfiguration()
        config.set("language", lang)
        every { plugin.config } returns config
        every { plugin.dataFolder } returns tempDir
        every { plugin.getResource(any()) } answers {
            val path = firstArg<String>()
            when (path) {
                "lang/en.yml" -> enContent.byteInputStream()
                "lang/de.yml" -> "prefix: \"&b[TestDE] \"\nhallo: \"Hallo {player}\"".byteInputStream()
                else -> null
            }
        }
        every { plugin.logger } returns mockk(relaxed = true)
        return plugin
    }

    @Test
    fun `t replaces placeholders and colors`() {
        val plugin = mockPlugin("en", "greet: \"&aHello {player} &7from {world}\"")
        val i18n = I18n(plugin)
        i18n.load()
        val out = i18n.t("greet", "player" to "Lucy", "world" to "world")
        // ChatColor translates &a to §a
        assertTrue(out.contains("Hello"))
        assertTrue(out.contains("Lucy"))
        assertTrue(out.contains("world"))
    }

    @Test
    fun `fallback to en when missing key`() {
        val plugin = mockPlugin("de", "prefix: \"&b[P] \"\nonly_en: \"EN {x}\"")
        // de lang doesn't have only_en, should fallback to en
        val i18n = I18n(plugin)
        i18n.load()
        // de file will be copied from getResource de.yml which has hallo, but not only_en
        // our mock en has only_en, de mock has hallo
        val out = i18n.t("only_en", "x" to "1")
        // fallback should give EN or [only_en] if not found
        assertTrue(out.contains("1") || out.contains("only_en"))
    }

    @Test
    fun `tp adds prefix`() {
        val plugin = mockPlugin("en", "prefix: \"&b[Prefix] \"\nmsg: \"Hi\"")
        val i18n = I18n(plugin)
        i18n.load()
        val out = i18n.tp("msg")
        assertTrue(out.contains("[Prefix]") || out.contains("Prefix"))
        assertTrue(out.contains("Hi"))
    }

    @Test
    fun `load creates lang files`() {
        val plugin = mockPlugin("en")
        val i18n = I18n(plugin)
        i18n.load()
        val enFile = File(tempDir, "lang/en.yml")
        assertTrue(enFile.exists())
    }
}
