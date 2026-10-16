package dev.slne.surf.hud.minestom.listener

import dev.slne.surf.api.minestom.event.EventRegistrar
import dev.slne.surf.api.minestom.extension.addListener
import dev.slne.surf.api.minestom.player.event.PlayerJoinEvent
import dev.slne.surf.hud.minestom.MinestomSurfHud
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerDisconnectEvent

object HudConnectionListener : EventRegistrar {
    override fun register(node: EventNode<Event>) {
        node.addListener<PlayerJoinEvent> { event ->
            MinestomSurfHud.INSTANCE.handleJoin(event.player.uuid)
        }

        node.addListener<PlayerDisconnectEvent> { event ->
            MinestomSurfHud.INSTANCE.handleQuit(event.player.uuid)
        }
    }
}
