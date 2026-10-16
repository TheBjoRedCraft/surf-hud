package dev.slne.surf.hud.minestom

import com.google.auto.service.AutoService
import dev.slne.surf.hud.api.SurfHud
import dev.slne.surf.hud.core.AbstractSurfHud
import dev.slne.surf.hud.core.platform.HudPlatform
import net.kyori.adventure.util.Services

@AutoService(SurfHud::class)
class MinestomSurfHud : AbstractSurfHud(), Services.Fallback {
    override val platform: HudPlatform = MinestomHudPlatform

    companion object {
        val INSTANCE get() = SurfHud.instance as MinestomSurfHud
    }
}
