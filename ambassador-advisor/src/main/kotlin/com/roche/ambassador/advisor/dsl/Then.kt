package com.roche.ambassador.advisor.dsl

import com.roche.ambassador.advisor.model.BuildableAdvice
import com.roche.ambassador.model.project.Project

sealed interface Then : Invokable {

    infix fun with(args: Iterable<Any>)

    infix fun with(args: Array<Any>)

    infix fun with(arg: Any)

    infix fun with(argProvider: Project.() -> Any)

    companion object {
        fun nothing(): Then = ThenNothing
        fun <A : BuildableAdvice> adviceMessage(adviceKey: String, rulesBuilder: RulesBuilder<A>): Then = ThenAdviceMessage(adviceKey, rulesBuilder)
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

internal class ThenAdviceMessage<A : BuildableAdvice>(
    val adviceKey: String,
    val rulesBuilder: RulesBuilder<A>
) : Then {

    private val args: MutableList<Any> = mutableListOf()

    override infix fun with(args: Iterable<Any>) {
        this.args.addAll(args)
    }

    override infix fun with(args: Array<Any>) {
        this.args.addAll(args)
    }

    override infix fun with(arg: Any) {
        this.args.add(arg)
    }

    override infix fun with(argProvider: Project.() -> Any) {
        val arg = argProvider(rulesBuilder.context.project)
        with(arg)
    }

    override fun invoke(): Boolean {
        with(rulesBuilder) {
            val key = AdviceKey(adviceKey, args)
            val config = context.getAdviceConfig(key)
            buildableAdvice.apply(config)
        }
        return true
    }
}
