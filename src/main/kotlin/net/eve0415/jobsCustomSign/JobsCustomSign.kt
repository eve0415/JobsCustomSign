package net.eve0415.jobsCustomSign

import net.eve0415.jobsCustomSign.events.InteractEventListener
import net.eve0415.jobsCustomSign.events.JobsEventListener
import org.bukkit.plugin.java.JavaPlugin

@Suppress("unused")
class JobsCustomSign : JavaPlugin() {
    override fun onEnable() {
        logger.info("Starting up JobsCustomSign")

        saveDefaultConfig()

        server.pluginManager.registerEvents(JobsEventListener(this), this)
        server.pluginManager.registerEvents(InteractEventListener(this), this)
    }

    override fun onDisable() {
        logger.info("Shutting down JobsCustomSign")
    }
}
