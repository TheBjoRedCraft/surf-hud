package dev.slne.surf.hud.api

import net.kyori.adventure.text.Component

@DslMarker
annotation class HudDsl

@HudDsl
class HudBuilder {
    private val elements = LinkedHashMap<String, HudElement>()

    fun line(line: Int, block: HudLineBuilder.() -> Unit) = line(line, DisplaySlot.BOSSBAR, block)

    fun line(line: Int, slot: DisplaySlot, block: HudLineBuilder.() -> Unit) {
        HudLineBuilder(line, slot).apply(block).elements.forEach(::element)
    }

    fun actionBar(line: Int = 0, block: HudLineBuilder.() -> Unit) = line(line, DisplaySlot.ACTIONBAR, block)

    fun hotbarLeft(block: HudLineBuilder.() -> Unit) = hotbarLeft(0, block)

    fun hotbarLeft(line: Int, block: HudLineBuilder.() -> Unit) = line(line, DisplaySlot.HOTBAR_LEFT, block)

    fun hotbarRight(block: HudLineBuilder.() -> Unit) = hotbarRight(0, block)

    fun hotbarRight(line: Int, block: HudLineBuilder.() -> Unit) = line(line, DisplaySlot.HOTBAR_RIGHT, block)

    fun element(element: HudElement) {
        elements[element.id] = element
    }

    fun build(): List<HudElement> = elements.values.toList()
}

@HudDsl
class HudLineBuilder @JvmOverloads constructor(val line: Int, val slot: DisplaySlot = DisplaySlot.BOSSBAR) {
    /** Whether elements get a background unless told otherwise; the hotbar text rows have none. */
    val defaultBackground: Boolean
        get() = line == 0 || (slot != DisplaySlot.HOTBAR_LEFT && slot != DisplaySlot.HOTBAR_RIGHT)

    internal val elements = mutableListOf<HudElement>()

    fun element(id: String, position: Int, content: Component, background: Boolean = defaultBackground) {
        elements += HudElement(id, line, position, content, background, slot)
    }

    fun center(id: String, content: Component, background: Boolean = defaultBackground) =
        element(id, 0, content, background)
}

fun buildHud(block: HudBuilder.() -> Unit): List<HudElement> = HudBuilder().apply(block).build()
