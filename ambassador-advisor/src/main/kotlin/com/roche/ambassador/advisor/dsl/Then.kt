package com.roche.ambassador.advisor.dsl

import com.roche.ambassador.advisor.model.BuildableAdvice
import com.roche.ambassador.model.project.Project

class Then<A : BuildableAdvice>(
    val adviceKey: String,
    val rulesBuilder: RulesBuilder<A>
) : Invokable {

    private val args: MutableList<Any> = mutableListOf()

    infix fun with(args: Iterable<Any>) {
        this.args.addAll(args)
    }

    infix fun with(args: Array<Any>) {
        this.args.addAll(args)
    }

    infix fun with(arg: Any) {
        this.args.add(arg)
    }

    infix fun with(argProvider: Project.() -> Any) {
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