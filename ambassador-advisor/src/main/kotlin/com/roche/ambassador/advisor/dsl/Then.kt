package com.roche.ambassador.advisor.dsl

import com.roche.ambassador.advisor.messages.AdviceMessage
import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.model.project.Project

sealed interface Then : Invokable {

    infix fun with(args: Iterable<Any>)

    infix fun with(args: Array<Any>)

    infix fun with(arg: Any)

    infix fun with(argProvider: Project.() -> Any)

    companion object {
        fun nothing(): Then = ThenNothing
        fun adviceMessage(adviceKey: String, rulesBuilder: RulesBuilder): Then = ThenAdviceMessage(adviceKey, rulesBuilder)
    }
}

internal object ThenNothing : Then {
    override fun with(args: Iterable<Any>) {
    }

    override fun with(args: Array<Any>) {
    }

    override fun with(arg: Any) {
    }

    override fun with(argProvider: Project.() -> Any) {
    }

    override fun invoke(): Boolean {
        return true
    }
}

internal class ThenAdviceMessage(
    val adviceKey: String,
    val rulesBuilder: RulesBuilder
) : Then {

    companion object {
        private val log by LoggerDelegate()
    }

    private val args: MutableList<Any> = mutableListOf()

    override infix fun with(args: Iterable<Any>) {
        this.args.addAll(args)
    }

    override infix fun with(args: Array<Any>) {
        this.args.addAll(args)
    }

    override infix fun with(arg: Any) {
        when(arg) {
            is Iterable<*> -> with(arg as Iterable<Any>)
            else -> this.args.add(arg)
        }
    }

    override infix fun with(argProvider: Project.() -> Any) {
        val arg = argProvider(rulesBuilder.context.project)
        with(arg)
    }

    override fun invoke(): Boolean {
        with(rulesBuilder) {
            val key = AdviceKey(adviceKey, args)
            context.getAdviceConfig(key)
                .filter { it.severity != AdviceMessage.AdviceSeverity.INFO } // TODO(x) get rid of filter and handle info advices
                .ifPresent { buildableAdvice.apply(it) }
        }
        return true
    }
}
