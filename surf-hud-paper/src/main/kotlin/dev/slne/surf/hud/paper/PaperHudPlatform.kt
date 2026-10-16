package dev.slne.surf.hud.paper

import dev.slne.surf.hud.core.platform.HudHands
import dev.slne.surf.hud.core.platform.HudPlatform
import org.bukkit.Bukkit
import org.bukkit.inventory.MainHand
import java.util.UUID

object PaperHudPlatform : HudPlatform {
    override fun viewer(uuid: UUID) = Bukkit.getPlayer(uuid)?.takeIf { it.isOnline }

    override fun hands(uuid: UUID): HudHands {
        val player = Bukkit.getPlayer(uuid) ?: return HudHands.DEFAULT
        return HudHands(
            leftHanded = player.mainHand == MainHand.LEFT,
            offhandOccupied = !player.inventory.itemInOffHand.isEmpty,
        )
    }
}
