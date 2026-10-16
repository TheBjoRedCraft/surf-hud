package dev.slne.surf.hud.minestom

import dev.slne.surf.api.minestom.extension.ConnectionManager
import dev.slne.surf.api.minestom.extension.SchedulerManager
import dev.slne.surf.api.minestom.plugin.SurfMinestomPlugin
import dev.slne.surf.hud.minestom.listener.HudConnectionListener
import net.minestom.server.timer.Task
import net.minestom.server.timer.TaskSchedule

object MinestomMain : SurfMinestomPlugin() {
    private var tickTask: Task? = null

    override suspend fun onEnable() {
        HudConnectionListener.register(eventNode)
        ConnectionManager.onlinePlayers.forEach { MinestomSurfHud.INSTANCE.handleJoin(it.uuid) }

        tickTask = SchedulerManager.buildTask { MinestomSurfHud.INSTANCE.tick() }
            .repeat(TaskSchedule.nextTick())
            .schedule()
    }

    override suspend fun onDisable() {
        tickTask?.cancel()
        tickTask = null

        ConnectionManager.onlinePlayers.forEach { MinestomSurfHud.INSTANCE.remove(it.uuid) }
    }
}
