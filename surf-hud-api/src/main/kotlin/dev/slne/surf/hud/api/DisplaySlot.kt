package dev.slne.surf.hud.api

/**
 * Where a [HudElement] is shown. Lines count downwards from line 0 in both slots, so negative lines
 * are above it; lines the theme has no font for are not rendered.
 */
enum class DisplaySlot {
    /**
     * The rows at the top of the screen, rendered as boss bar names: line -1 above the boss bar,
     * lines 1 and 2 below it.
     */
    BOSSBAR,

    /**
     * The action bar above the hotbar: line -1 above the action bar, lines 1 to 3 below it.
     */
    ACTIONBAR,

    /**
     * Left of the hotbar. Line 0 is centered on the hotbar; lines -1 and 1 are two smaller text rows
     * in its upper and lower half, which the [HudBuilder] gives no background by default. Elements
     * line up outwards: the lowest position sits next to the hotbar, higher positions further left.
     * Room is left for the offhand slot when the offhand slot is shown on this side.
     */
    HOTBAR_LEFT,

    /**
     * Right of the hotbar; the mirror image of [HOTBAR_LEFT].
     */
    HOTBAR_RIGHT,
}
