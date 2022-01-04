package com.roche.ambassador.advisor.badges

import com.roche.ambassador.advisor.common.AdvisorConfigurationException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

internal class ShieldsBadgeProvider(config: Map<String, String>, private val colorResolver: ColorResolver) : BadgeProvider {

    private val defaultConfig: Map<String, String>
    private val url: String

    companion object {
        const val URL_PROP = "url"
        const val STYLE_PROP = "style"
        const val LOGO_PROP = "logo"
        const val LOGO_COLOR_PROP = "logoColor"
        const val LOGO_WIDTH_PROP = "logoWidth"
        const val LABEL_COLOR_PROP = "labelColor"
        const val COLOR_PROP = "color"
        const val CACHE_SECONDS_PROP = "cacheSeconds"
        const val LABEL_PROP = "label"
        const val MESSAGE_PROP = "message"
        private const val INVALID_PROP = "_"
    }

    private fun Map<String, String>.readProp(name: String): Pair<String, String> {
        val value = this[name] ?: INVALID_PROP
        val encoded = value.encodeToUrl()
        return name to encoded
    }

    private fun String.encodeToUrl(): String = URLEncoder.encode(this, StandardCharsets.UTF_8)

    init {
        val rootUrl = config[URL_PROP] ?: throw AdvisorConfigurationException("URL was not provided in ambassador.badges.config.url")
        url = rootUrl
        defaultConfig = mapOf(
            config.readProp(STYLE_PROP),
            config.readProp(LOGO_PROP),
            config.readProp(LOGO_COLOR_PROP),
            config.readProp(LOGO_WIDTH_PROP),
            config.readProp(LABEL_COLOR_PROP),
            config.readProp(COLOR_PROP),
            config.readProp(CACHE_SECONDS_PROP),
        )
    }

    private fun MutableMap<String, String>.putIfNotNull(key: String, value: String?) {
        if (value != null) {
            this[key] = value.encodeToUrl()
        }
    }

    private fun buildBadgeUrl(badge: Badge): String {
        val conf = defaultConfig.toMutableMap()
        conf[MESSAGE_PROP] = badge.message
        conf.putIfNotNull(LABEL_PROP, badge.label)
        conf.putIfNotNull(LOGO_PROP, badge.logo?.value)
        conf.putIfNotNull(LOGO_COLOR_PROP, badge.logo?.color)
        conf.putIfNotNull(LOGO_WIDTH_PROP, badge.logo?.width.toString())
        conf.putIfNotNull(LABEL_COLOR_PROP, colorResolver.resolve(badge.labelColor))
        conf.putIfNotNull(COLOR_PROP, colorResolver.resolve(badge.color))
        val query = conf
            .filterValues { it != INVALID_PROP }
            .map { "${it.key}=${it.value}" }
            .joinToString("&") { it }
        return "$url?$query"
    }

    override fun getBadgeAsMarkdown(badge: Badge): String {
        val badgeUrl = buildBadgeUrl(badge)
        val label = badge.label ?: ""
        val url = if (badge.url.isNullOrBlank()) {
            "#"
        } else {
            badge.url
        }
        return "[![$label]($badgeUrl)]($url)"
    }
}
