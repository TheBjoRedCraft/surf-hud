package dev.slne.surf.hud.api.theme

import dev.slne.surf.hud.api.DisplaySlot
import net.kyori.adventure.key.Key

data class HudTheme(
    /** The fonts of the [DisplaySlot.BOSSBAR] lines from line 0 downwards. */
    val lineFonts: List<Key> = DEFAULT_LINE_FONTS,
    val spaceFont: Key = Key.key("surf", "space"),
    val pillCap: HudGlyph = HudGlyph('', 1),
    val pillSegments: List<HudGlyph> = binaryGlyphs(0xE002, 128),
    val negativeSpaces: List<HudGlyph> = binaryGlyphs(0xF801, 256),
    val positiveSpaces: List<HudGlyph> = binaryGlyphs(0xF821, 256),
    val paddingLeft: Int = 5,
    val paddingRight: Int = 4,
    val gap: Int = 3,
    /** The fonts of the [DisplaySlot.ACTIONBAR] lines from line 0 downwards. */
    val actionBarFonts: List<Key> = DEFAULT_ACTION_BAR_FONTS,
    /** The fonts of the [DisplaySlot.BOSSBAR] lines from line -1 upwards. */
    val lineFontsAbove: List<Key> = DEFAULT_LINE_FONTS_ABOVE,
    /** The fonts of the [DisplaySlot.ACTIONBAR] lines from line -1 upwards. */
    val actionBarFontsAbove: List<Key> = DEFAULT_ACTION_BAR_FONTS_ABOVE,
    /** The font of the [DisplaySlot.HOTBAR_LEFT] and [DisplaySlot.HOTBAR_RIGHT] line. */
    val hotbarFont: Key = Key.key("surf", "hotbar"),
    /** The distance between the hotbar, or whatever is next to it, and the first element beside it. */
    val hotbarGap: Int = 4,
    /** The room kept for the offhand slot on its side of the hotbar while the offhand holds an item. */
    val offhandWidth: Int = 29,
    /**
     * The room kept for the hotbar attack indicator on the main hand side. Whether a player shows
     * the indicator there is not sent to the server, and the default client setting shows it at
     * the crosshair, so none is kept by default; 24 clears it.
     */
    val attackIndicatorWidth: Int = 0,
    /** The fonts of the [DisplaySlot.SCREEN_LEFT] lines from line 0 downwards. */
    val screenLeftFonts: List<Key> = edgeFonts("screen_left", "below"),
    /** The fonts of the [DisplaySlot.SCREEN_LEFT] lines from line -1 upwards. */
    val screenLeftFontsAbove: List<Key> = edgeFonts("screen_left", "above").drop(1),
    /** The fonts of the [DisplaySlot.SCREEN_RIGHT] lines from line 0 downwards. */
    val screenRightFonts: List<Key> = edgeFonts("screen_right", "below"),
    /** The fonts of the [DisplaySlot.SCREEN_RIGHT] lines from line -1 upwards. */
    val screenRightFontsAbove: List<Key> = edgeFonts("screen_right", "above").drop(1),
    /** The distance between a screen edge and the first element beside it. */
    val screenEdgeMargin: Int = 4,
) {
    init {
        require(lineFonts.isNotEmpty()) { "At least one line font is required" }
        require(actionBarFonts.isNotEmpty()) { "At least one action bar font is required" }
        require(pillSegments.any { it.width == 1 }) { "Pill segments require a glyph of width 1" }
        require(negativeSpaces.any { it.width == 1 }) { "Negative spaces require a glyph of width 1" }
        require(positiveSpaces.any { it.width == 1 }) { "Positive spaces require a glyph of width 1" }
        require(paddingLeft >= pillCap.width) { "Left padding must cover the pill cap" }
        require(paddingRight >= 0) { "Right padding must not be negative" }
        require(gap >= 0) { "Gap must not be negative" }
        require(hotbarGap >= 0) { "Hotbar gap must not be negative" }
        require(offhandWidth >= 0) { "Offhand width must not be negative" }
        require(attackIndicatorWidth >= 0) { "Attack indicator width must not be negative" }
        require(screenEdgeMargin >= 0) { "Screen edge margin must not be negative" }
    }

    @Deprecated("Kept for binary compatibility", level = DeprecationLevel.HIDDEN)
    constructor(
        lineFonts: List<Key> = DEFAULT_LINE_FONTS,
        spaceFont: Key = Key.key("surf", "space"),
        pillCap: HudGlyph = HudGlyph('', 1),
        pillSegments: List<HudGlyph> = binaryGlyphs(0xE002, 128),
        negativeSpaces: List<HudGlyph> = binaryGlyphs(0xF801, 256),
        positiveSpaces: List<HudGlyph> = binaryGlyphs(0xF821, 256),
        paddingLeft: Int = 5,
        paddingRight: Int = 4,
        gap: Int = 3,
    ) : this(
        lineFonts, spaceFont, pillCap, pillSegments, negativeSpaces, positiveSpaces,
        paddingLeft, paddingRight, gap,
    )

    @Deprecated("Kept for binary compatibility", level = DeprecationLevel.HIDDEN)
    fun copy(
        lineFonts: List<Key> = this.lineFonts,
        spaceFont: Key = this.spaceFont,
        pillCap: HudGlyph = this.pillCap,
        pillSegments: List<HudGlyph> = this.pillSegments,
        negativeSpaces: List<HudGlyph> = this.negativeSpaces,
        positiveSpaces: List<HudGlyph> = this.positiveSpaces,
        paddingLeft: Int = this.paddingLeft,
        paddingRight: Int = this.paddingRight,
        gap: Int = this.gap,
    ) = copy(
        lineFonts, spaceFont, pillCap, pillSegments, negativeSpaces, positiveSpaces,
        paddingLeft, paddingRight, gap, actionBarFonts, lineFontsAbove, actionBarFontsAbove,
        hotbarFont, hotbarGap, offhandWidth, attackIndicatorWidth,
        screenLeftFonts, screenLeftFontsAbove, screenRightFonts, screenRightFontsAbove, screenEdgeMargin,
    )

    /** The number of [DisplaySlot.BOSSBAR] lines. */
    val lines: Int get() = lineFonts.size

    /** The font of a [DisplaySlot.BOSSBAR] line. */
    fun lineFont(line: Int): Key = lineFont(DisplaySlot.BOSSBAR, line)

    fun lineFontOrNull(slot: DisplaySlot, line: Int): Key? {
        val (below, above) = when (slot) {
            DisplaySlot.BOSSBAR -> lineFonts to lineFontsAbove
            DisplaySlot.ACTIONBAR -> actionBarFonts to actionBarFontsAbove
            DisplaySlot.HOTBAR_LEFT, DisplaySlot.HOTBAR_RIGHT -> return hotbarFont.takeIf { line == 0 }
            DisplaySlot.SCREEN_LEFT -> screenLeftFonts to screenLeftFontsAbove
            DisplaySlot.SCREEN_RIGHT -> screenRightFonts to screenRightFontsAbove
        }
        return if (line >= 0) below.getOrNull(line) else above.getOrNull(-line - 1)
    }

    fun lineFont(slot: DisplaySlot, line: Int): Key = lineFontOrNull(slot, line)
        ?: throw IllegalArgumentException("HUD line $line is not supported in $slot by this theme")

    companion object {
        private val DEFAULT_LINE_FONTS = listOf(Key.key("surf", "hud"), Key.key("surf", "hud2"), Key.key("surf", "hud3"))
        private val DEFAULT_LINE_FONTS_ABOVE = listOf(Key.key("surf", "hud_above1"))
        private val DEFAULT_ACTION_BAR_FONTS = listOf(
            Key.key("surf", "actionbar"),
            Key.key("surf", "actionbar_below1"),
            Key.key("surf", "actionbar_below2"),
            Key.key("surf", "actionbar_below3"),
        )
        private val DEFAULT_ACTION_BAR_FONTS_ABOVE = listOf(Key.key("surf", "actionbar2"))

        /** `surf:<edge>`, then `surf:<edge>_<direction>1` and `surf:<edge>_<direction>2`. */
        private fun edgeFonts(edge: String, direction: String) =
            listOf(Key.key("surf", edge)) + (1..2).map { Key.key("surf", "${edge}_$direction$it") }

        private fun binaryGlyphs(firstCodePoint: Int, largest: Int): List<HudGlyph> =
            generateSequence(1) { it * 2 }
                .takeWhile { it <= largest }
                .mapIndexed { index, width -> HudGlyph((firstCodePoint + index).toChar(), width) }
                .toList()
                .asReversed()
    }
}
