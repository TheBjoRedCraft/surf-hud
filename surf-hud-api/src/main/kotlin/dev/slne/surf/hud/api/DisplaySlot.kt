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
     * Left of the hotbar, on line 0 only. Elements line up outwards: the lowest position sits next
     * to the hotbar, higher positions further left. Room is left for the offhand slot when the
     * offhand slot is shown on this side.
     */
    HOTBAR_LEFT,

    /**
     * Right of the hotbar, on line 0 only; the mirror image of [HOTBAR_LEFT].
     */
    HOTBAR_RIGHT,

    /**
     * At the left edge of the screen, line 0 centered vertically, lines -2 to 2. Elements line up
     * from the edge towards the middle: the lowest position sits at the edge.
     *
     * The client cannot be told the screen width, so the resource pack's text shader moves these
     * glyphs to the edge; without it they are not visible.
     */
    SCREEN_LEFT,

    /**
     * At the right edge of the screen; the mirror image of [SCREEN_LEFT].
     */
    SCREEN_RIGHT,
}
