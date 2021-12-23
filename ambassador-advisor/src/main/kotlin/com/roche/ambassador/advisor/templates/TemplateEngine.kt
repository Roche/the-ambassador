package com.roche.ambassador.advisor.templates

import com.github.jknack.handlebars.Context
import com.github.jknack.handlebars.Handlebars

class TemplateEngine(private val handlebars: Handlebars) {

    fun process(template: String, model: Any): String {
        val context = Context.newContext(model)
        return handlebars.compile(template).apply(context)
    }

}