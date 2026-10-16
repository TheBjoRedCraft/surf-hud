package dev.slne.surf.hud.minestom

import dev.slne.surf.api.minestom.extension.ConnectionManager
import dev.slne.surf.hud.core.platform.HudHands
import dev.slne.surf.hud.core.platform.HudPlatform
import net.minestom.server.entity.MainHand
import java.util.*

object MinestomHudPlatform : HudPlatform {
    override fun viewer(uuid: UUID) = ConnectionManager.getOnlinePlayerByUuid(uuid)?.takeIf { it.isOnline }

    override fun hands(uuid: UUID): HudHands {
        val player = ConnectionManager.getOnlinePlayerByUuid(uuid) ?: return HudHands.DEFAULT
        return HudHands(
            leftHanded = player.settings.mainHand() == MainHand.LEFT,
            offhandOccupied = !player.itemInOffHand.isAir,
        )
    }
}
