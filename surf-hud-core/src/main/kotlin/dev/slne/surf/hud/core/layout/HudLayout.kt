package dev.slne.surf.hud.core.layout

import dev.slne.surf.hud.api.DisplaySlot
import dev.slne.surf.hud.api.HudElement
import dev.slne.surf.hud.api.theme.HudTheme
import dev.slne.surf.hud.core.layout.component.ComponentWidths
import dev.slne.surf.hud.core.platform.HudHands
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component

object HudLayout {
    private const val HOTBAR_HALF_WIDTH = 91

    fun compose(
        theme: HudTheme,
        slot: DisplaySlot,
        elements: Collection<HudElement>,
        render: (Component) -> Component,
    ): Component {
        val lines = elements
            .groupBy { it.line }
            .toSortedMap()

        return Component.text()
            .append(lines.mapNotNull { (line, lineElements) ->
                val font = theme.lineFontOrNull(slot, line) ?: return@mapNotNull null
                composeLine(theme, font, lineElements, render)
            })
            .build()
    }

    /**
     * Lines [elements] up from an anchor of [slot]: outwards from the hotbar edges, or inwards from
     * the screen edges, where the text shader moves them. Like every line the result has no net
     * width, so the centered action bar keeps it in place.
     */
    fun composeSide(
        theme: HudTheme,
        slot: DisplaySlot,
        hands: HudHands,
        elements: Collection<HudElement>,
        render: (Component) -> Component,
    ): Component? {
        val towardsLeft = slot == DisplaySlot.HOTBAR_LEFT || slot == DisplaySlot.SCREEN_RIGHT
        val distance = when (slot) {
            DisplaySlot.HOTBAR_LEFT, DisplaySlot.HOTBAR_RIGHT -> hotbarDistance(theme, slot, hands)
            DisplaySlot.SCREEN_LEFT, DisplaySlot.SCREEN_RIGHT -> theme.screenEdgeMargin
            else -> throw IllegalArgumentException("$slot is not a side slot")
        }

        val lines = elements
            .groupBy { it.line }
            .toSortedMap()
            .mapNotNull { (line, lineElements) ->
                val font = theme.lineFontOrNull(slot, line) ?: return@mapNotNull null
                val sorted = lineElements.sortedBy { it.position }
                val parts = parts(theme, font, sorted, render).let { if (towardsLeft) it.asReversed() else it }
                val total = parts.sumOf { it.width } + theme.gap * (parts.size - 1)
                val start = if (towardsLeft) -distance - total else distance

                HudComposer(theme, font).apply {
                    space(start)
                    parts.forEachIndexed { index, part ->
                        if (index > 0) {
                            space(theme.gap)
                        }
                        append(part)
                    }
                    space(-start - total)
                }.build()
            }

        return lines.takeIf { it.isNotEmpty() }?.let { Component.text().append(it).build() }
    }

    private fun hotbarDistance(theme: HudTheme, slot: DisplaySlot, hands: HudHands): Int {
        val offhandSide = (slot == DisplaySlot.HOTBAR_LEFT) != hands.leftHanded
        return HOTBAR_HALF_WIDTH + theme.hotbarGap +
                (if (offhandSide && hands.offhandOccupied) theme.offhandWidth else 0) +
                (if (!offhandSide) theme.attackIndicatorWidth else 0)
    }

    private fun parts(
        theme: HudTheme,
        font: Key,
        sorted: List<HudElement>,
        render: (Component) -> Component,
    ): List<HudComposer> = sorted.map { element ->
        val rendered = render(element.content)
        val contentWidth = ComponentWidths.width(rendered)

        HudComposer(theme, font).apply {
            if (element.background) {
                pill(rendered, contentWidth)
            } else {
                content(rendered, contentWidth)
            }
        }
    }

    private fun composeLine(
        theme: HudTheme,
        font: Key,
        elements: List<HudElement>,
        render: (Component) -> Component,
    ): Component {
        val sorted = elements.sortedBy { it.position }
        val parts = parts(theme, font, sorted, render)

        val total = parts.sumOf { it.width } + theme.gap * (parts.size - 1)
        val firstAnchor = sorted.indexOfFirst { it.position == 0 }
        val offset = if (firstAnchor < 0) {
            total / 2
        } else {
            val lastAnchor = sorted.indexOfLast { it.position == 0 }
            val before = parts.take(firstAnchor).sumOf { it.width + theme.gap }
            val anchor = parts.subList(firstAnchor, lastAnchor + 1).sumOf { it.width } +
                    theme.gap * (lastAnchor - firstAnchor)
            before + anchor / 2
        }

        return HudComposer(theme, font).apply {
            space(-offset)
            parts.forEachIndexed { index, part ->
                if (index > 0) {
                    space(theme.gap)
                }
                append(part)
            }
            space(offset - total)
        }.build()
    }
}
