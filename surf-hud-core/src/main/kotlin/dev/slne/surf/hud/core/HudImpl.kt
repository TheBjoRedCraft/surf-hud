package dev.slne.surf.hud.core

import dev.slne.surf.hud.api.DisplaySlot
import dev.slne.surf.hud.api.Hud
import dev.slne.surf.hud.api.HudElement
import dev.slne.surf.hud.api.theme.HudTheme
import dev.slne.surf.hud.core.layout.HudLayout
import dev.slne.surf.hud.core.platform.HudHands
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.identity.Identity
import net.kyori.adventure.text.Component
import net.kyori.adventure.translation.GlobalTranslator
import java.util.*
import java.util.concurrent.TimeUnit

class HudImpl(
    override val viewer: UUID,
    private val service: AbstractSurfHud,
) : Hud {
    private val lock = Any()
    private val content = LinkedHashMap<String, HudElement>()
    private val spacer = BossBar.bossBar(Component.empty(), 0f, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS)
    private val bar = BossBar.bossBar(Component.empty(), 0f, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS)

    @Volatile
    private var dirty = true
    private var shownTo: Audience? = null
    private var bossBarShown = false
    private var bossBarContent: Component? = null

    @Volatile
    private var actionBarContent: Component? = null

    @Volatile
    private var actionBarSentAt = 0L

    @Volatile
    private var hands = HudHands.DEFAULT

    override var visible: Boolean = true
        set(value) {
            field = value
            dirty = true
        }

    override val elements: List<HudElement>
        get() = synchronized(lock) { content.values.toList() }

    override fun get(id: String): HudElement? = synchronized(lock) { content[id] }

    override fun add(element: HudElement) = modify {
        content[element.id] = element
    }

    override fun add(elements: Collection<HudElement>) = modify {
        elements.forEach { content[it.id] = it }
    }

    override fun content(id: String, content: Component): Boolean = synchronized(lock) {
        val element = this.content[id] ?: return false
        if (element.content != content) {
            this.content[id] = element.copy(content = content)
            dirty = true
        }
        true
    }

    override fun remove(id: String): HudElement? = synchronized(lock) {
        content.remove(id)?.also { dirty = true }
    }

    override fun remove(ids: Collection<String>) = synchronized(lock) {
        if (content.keys.removeAll(ids.toSet())) {
            dirty = true
        }
    }

    override fun clear() = modify {
        content.clear()
    }

    override fun refresh() {
        dirty = true
        service.flush(this)
    }

    fun invalidate() {
        synchronized(lock) {
            shownTo = null
            bossBarShown = false
            bossBarContent = null
            actionBarContent = null
            dirty = true
        }
    }

    fun flush(audience: Audience, theme: HudTheme, hands: HudHands) {
        if (hands != this.hands) {
            this.hands = hands
            dirty = true
        }

        if (!dirty && !actionBarExpired()) {
            return
        }

        synchronized(lock) {
            if (shownTo !== audience) {
                shownTo?.let(::hideFrom)
                shownTo = audience
                dirty = true
            }

            if (dirty) {
                dirty = false
                compose(audience, theme)
            }

            val actionBar = actionBarContent
            if (actionBar != null && actionBarExpired()) {
                audience.sendActionBar(actionBar)
                actionBarSentAt = System.nanoTime()
            }
        }
    }

    fun hide() = synchronized(lock) {
        shownTo?.let(::hideFrom)
        shownTo = null
    }

    private fun compose(audience: Audience, theme: HudTheme) {
        val slots = if (visible) content.values.groupBy { it.slot } else emptyMap()
        val locale = audience.get(Identity.LOCALE).orElse(Locale.US)

        fun layout(slot: DisplaySlot) = slots[slot]?.let { elements ->
            HudLayout.compose(theme, slot, elements) { GlobalTranslator.render(it, locale) }
        }

        fun side(slot: DisplaySlot) = slots[slot]?.let { elements ->
            HudLayout.composeSide(theme, slot, hands, elements) { GlobalTranslator.render(it, locale) }
        }

        // The action bar is a single message, so the hotbar and screen edge sides travel along with it
        val actionBar = listOfNotNull(
            layout(DisplaySlot.ACTIONBAR),
            side(DisplaySlot.HOTBAR_LEFT),
            side(DisplaySlot.HOTBAR_RIGHT),
            side(DisplaySlot.SCREEN_LEFT),
            side(DisplaySlot.SCREEN_RIGHT),
        ).takeIf { it.isNotEmpty() }?.let { Component.text().append(it).build() }

        showBossBar(audience, layout(DisplaySlot.BOSSBAR))
        showActionBar(audience, actionBar)
    }

    private fun showBossBar(audience: Audience, composed: Component?) {
        if (composed == null) {
            hideBossBar(audience)
            return
        }

        if (bossBarContent != composed) {
            bossBarContent = composed
            bar.name(composed)
        }

        if (!bossBarShown) {
            audience.showBossBar(spacer)
            audience.showBossBar(bar)
            bossBarShown = true
        }
    }

    private fun showActionBar(audience: Audience, composed: Component?) {
        if (composed == actionBarContent) {
            return
        }

        if (composed == null) {
            hideActionBar(audience)
            return
        }

        actionBarContent = composed
        actionBarSentAt = System.nanoTime() - ACTION_BAR_RESEND_NANOS
    }

    private fun actionBarExpired() =
        actionBarContent != null && System.nanoTime() - actionBarSentAt >= ACTION_BAR_RESEND_NANOS

    private fun hideFrom(audience: Audience) {
        hideBossBar(audience)
        hideActionBar(audience)
    }

    private fun hideBossBar(audience: Audience) {
        if (bossBarShown) {
            audience.hideBossBar(spacer)
            audience.hideBossBar(bar)
            bossBarShown = false
        }
    }

    private fun hideActionBar(audience: Audience) {
        if (actionBarContent != null) {
            audience.sendActionBar(Component.empty())
            actionBarContent = null
        }
    }

    private inline fun modify(block: () -> Unit) {
        synchronized(lock) {
            block()
            dirty = true
        }
    }

    private companion object {
        // The client fades the action bar out after three seconds, so it is resent well before that
        val ACTION_BAR_RESEND_NANOS = TimeUnit.SECONDS.toNanos(1)
    }
}
