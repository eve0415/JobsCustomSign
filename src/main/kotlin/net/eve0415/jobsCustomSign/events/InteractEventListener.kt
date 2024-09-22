package net.eve0415.jobsCustomSign.events

import com.gamingmesh.jobs.Jobs
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.WorldGuard
import net.eve0415.jobsCustomSign.JobsCustomSign
import org.bukkit.block.Block
import org.bukkit.block.Sign
import org.bukkit.block.sign.Side
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class InteractEventListener(private val plugin: JobsCustomSign) : Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onEvent(event: PlayerInteractEvent) {
        if (event.player.hasPermission("jobs.admin")) return

        if (event.action != Action.RIGHT_CLICK_BLOCK) return

        val sign = event.clickedBlock?.state
        if (sign !is Sign) return

        if (!isGuarded(event.clickedBlock!!)) return

        val config = plugin.config.getStringList("signs")
        if (config.isEmpty()) return

        val signs = sign.getSide(Side.FRONT).lines

        var job: String? = null

        config.forEachIndexed { index, line ->
            if (line.contains("<JOB_NAME>")) {
                Regex(line.replace("<JOB_NAME>", ".+")).find(signs[index]) ?: return

                job = Regex(line.replace("<JOB_NAME>", "(.+)")).find(signs[index])?.groupValues?.get(1)
                return@forEachIndexed
            }

            if (signs[index] != line) return
        }

        if (job == null) return
        event.isCancelled = true

        Jobs.getPlayerManager().getJobsPlayer(event.player).joinJob(Jobs.getJob(job))
    }

    private fun isGuarded(block: Block): Boolean {
        val manager =
            WorldGuard.getInstance().platform.regionContainer.get(BukkitAdapter.adapt(block.world)) ?: return false
        for (region in manager.regions.entries) {
            if (region.value.contains(block.location.blockX, block.location.blockY, block.location.blockZ)) {
                return true
            }
        }

        return false
    }
}