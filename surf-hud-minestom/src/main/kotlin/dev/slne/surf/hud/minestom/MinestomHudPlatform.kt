package dev.slne.surf.hud.minestom

import dev.slne.surf.api.minestom.extension.ConnectionManager
import dev.slne.surf.hud.core.platform.HudPlatform
import java.util.*

object MinestomHudPlatform : HudPlatform {
    override fun viewer(uuid: UUID) = ConnectionManager.getOnlinePlayerByUuid(uuid)?.takeIf { it.isOnline }
}
