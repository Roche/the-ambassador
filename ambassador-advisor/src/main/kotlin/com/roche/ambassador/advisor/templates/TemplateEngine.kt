package com.roche.ambassador.advisor.templates

import com.github.jknack.handlebars.Context
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Template

class TemplateEngine(private val handlebars: Handlebars) {

    fun process(template: String, model: Any): String {
        return compile(handlebars::compile, template, model)
    }

    fun processInline(template: String, model: Any): String {
        return compile(handlebars::compileInline, template, model)
    }

    fun preprocessInline(template: String, model: Any): String {
        return compile(handlebars::compileInline, template, model)
    }

    private fun compile(compiler: (String) -> Template, template: String, model: Any): String {
        val context = Context.newContext(model)
        return compiler(template).apply(context)
    }

}