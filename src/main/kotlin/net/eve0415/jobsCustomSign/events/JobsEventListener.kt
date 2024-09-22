package net.eve0415.jobsCustomSign.events

import com.gamingmesh.jobs.Jobs
import com.gamingmesh.jobs.api.JobsJoinEvent
import net.eve0415.jobsCustomSign.JobsCustomSign
import net.luckperms.api.LuckPerms
import net.luckperms.api.node.Node
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class JobsEventListener(private val plugin: JobsCustomSign) : Listener {
    val lp: LuckPerms by lazy { plugin.server.servicesManager.getRegistration(LuckPerms::class.java)!!.provider }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onEvent(event: JobsJoinEvent) {
        Jobs.getJobs()
            .filterNot { it.equals(event.job) }
            .forEach { event.player.leaveJob(it) }

        this.lp.userManager.modifyUser(event.player.uniqueId) {
            it.data().apply {
                Jobs.getJobs()
                    .filterNot { it.equals(event.job) }
                    .map { remove(Node.builder(it.name.lowercase()).build()) }

                add(Node.builder(event.job.name.lowercase()).build())
            }
        }

        plugin.logger.info("${event.player.name} is now a ${event.job.name}!")
    }
}