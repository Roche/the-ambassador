package com.roche.ambassador.advisor.rules

import com.roche.ambassador.advisor.configuration.RulesProperties
import com.roche.ambassador.advisor.dsl.RulesBuilder
import com.roche.ambassador.model.project.Permissions
import org.springframework.util.unit.DataSize
import java.time.Duration

interface Rule {

    fun RulesBuilder.apply()

}

interface RepositoryRule : Rule
interface CiRule : Rule

internal fun RulesBuilder.createPermissionBasedRuleset(
    name: String,
    rule: RulesProperties.Rule,
    otherRules: List<Rule>,
    permissionExtractor: Permissions.() -> Permissions.Permission,
) {
    whenEnabled(rule) {
        matchFirst({ permissionExtractor(permissions) }) {
            that { isDisabled() } then "$name.disabled"
            or {
                thatNot { canEveryoneAccess() } then "$name.private"
                otherRules.apply(this)
            }
        }
    }
}


internal fun RulesBuilder.createPermissionBasedRuleset(
    name: String,
    rule: RulesProperties.Rule,
    permissionExtractor: Permissions.() -> Permissions.Permission,
) = createPermissionBasedRuleset(name, rule, listOf(), permissionExtractor)

internal fun <T : Rule> List<T>.apply(rulesBuilder: RulesBuilder) {
    forEach {
        with(it) {
            rulesBuilder.apply()
        }
    }
}

operator fun Long.compareTo(dataSize: DataSize): Int = compareTo(dataSize.toBytes())
operator fun Long.compareTo(duration: Duration): Int = compareTo(duration.seconds)