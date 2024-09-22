package net.eve0415.jobsCustomSign.events

import com.gamingmesh.jobs.Jobs
import com.gamingmesh.jobs.api.JobsJoinEvent
import net.eve0415.jobsCustomSign.JobsCustomSign
import net.luckperms.api.LuckPerms
import net.luckperms.api.node.Node
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import java.util.concurrent.TimeUnit

class JobsEventListener(private val plugin: JobsCustomSign) : Listener {
    val lp: LuckPerms by lazy { plugin.server.servicesManager.getRegistration(LuckPerms::class.java)!!.provider }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onEvent(event: JobsJoinEvent) {
        val cooldownNode = lp.userManager.getUser(event.player.uniqueId)?.nodes
            ?.filter { it.key == "job.cooldown" }
            ?.firstOrNull()

        if (cooldownNode != null && cooldownNode.expiryDuration != null) {
            val message = plugin.config.getString("cooldown.error")
                ?: "You must wait <TIME> seconds before changing a job"

            event.player.player.spigot().sendMessage(TextComponent(
                message.replace("<TIME>", cooldownNode.expiryDuration!!.seconds.toString())
            ).apply {
                color = ChatColor.RED
            })
            event.isCancelled = true

            return
        }

        Jobs.getJobs()
            .filterNot { it.equals(event.job) }
            .forEach { event.player.leaveJob(it) }

        val cooldown = plugin.config.getLong("cooldown.time")

        lp.userManager.modifyUser(event.player.uniqueId) {
            it.data().apply {
                Jobs.getJobs()
                    .filterNot { it.equals(event.job) }
                    .map { remove(Node.builder(it.name.lowercase()).build()) }

                add(Node.builder(event.job.name.lowercase()).build())

                if (cooldown > 0) {
                    add(Node.builder("job.cooldown").value(true).expiry(cooldown, TimeUnit.SECONDS).build())
                }
            }
        }

        plugin.logger.info("${event.player.name} is now a ${event.job.name}!")
    }
}