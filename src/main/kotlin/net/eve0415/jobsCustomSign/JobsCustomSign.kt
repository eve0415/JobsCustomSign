package net.eve0415.jobsCustomSign

import net.eve0415.jobsCustomSign.events.JobsEventListener
import org.bukkit.plugin.java.JavaPlugin

@Suppress("unused")
class JobsCustomSign : JavaPlugin() {
    override fun onEnable() {
        this.logger.info("Starting up JobsCustomSign")

        this.server.pluginManager.registerEvents(JobsEventListener(this), this)
    }

    override fun onDisable() {
        this.logger.info("Shutting down JobsCustomSign")
    }
}
