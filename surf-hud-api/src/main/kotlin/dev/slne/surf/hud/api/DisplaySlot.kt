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
}
