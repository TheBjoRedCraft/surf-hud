package dev.slne.surf.hud.core.platform

/**
 * What decides the room beside the hotbar: the offhand slot is shown on the side opposite the main
 * hand, and only while the offhand holds an item.
 */
data class HudHands(
    val leftHanded: Boolean = false,
    val offhandOccupied: Boolean = false,
) {
    companion object {
        val DEFAULT = HudHands()
    }
}
