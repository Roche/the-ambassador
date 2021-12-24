package com.roche.ambassador.advisor.badges

import com.roche.ambassador.advisor.common.AdvisorConfigurationException
import com.roche.ambassador.advisor.templates.TemplateEngine

internal class TextBadgeProvider(
    config: Map<String, String>,
    private val templateEngine: TemplateEngine
) : BadgeProvider {

    private val template = config.getOrElse(TEMPLATE_PROP) { throw AdvisorConfigurationException("Text badge template was not defined under") }

    companion object {
        private const val TEMPLATE_PROP = "template"
    }

    override fun getBadgeAsMarkdown(badge: Badge): String {
        return templateEngine.processInline(template, badge)
    }
}