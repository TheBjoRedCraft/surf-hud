package dev.slne.surf.hud.api

import net.kyori.adventure.text.Component

data class HudElement(
    val id: String,
    val line: Int,
    val position: Int,
    val content: Component,
    val background: Boolean = true,
    val slot: DisplaySlot = DisplaySlot.BOSSBAR,
) {
    init {
        require(id.isNotBlank()) { "HUD element id must not be blank" }
    }

    @Deprecated("Kept for binary compatibility", level = DeprecationLevel.HIDDEN)
    constructor(
        id: String,
        line: Int,
        position: Int,
        content: Component,
        background: Boolean = true,
    ) : this(id, line, position, content, background, DisplaySlot.BOSSBAR)

    @Deprecated("Kept for binary compatibility", level = DeprecationLevel.HIDDEN)
    fun copy(
        id: String = this.id,
        line: Int = this.line,
        position: Int = this.position,
        content: Component = this.content,
        background: Boolean = this.background,
    ) = copy(id, line, position, content, background, slot)
}
