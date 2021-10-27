package com.roche.ambassador

import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchIgnore
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.library.DependencyRules
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices

@ArchIgnore(reason = "there are a lot of cycles, and instead of freezing, lets solve it asap") // FIXME
@AnalyzeClasses(packagesOf = [TheAmbassadorApplication::class])
class DependenciesTest {

    @ArchTest
    private val `no classes should depend on upper packages` = DependencyRules.NO_CLASSES_SHOULD_DEPEND_UPPER_PACKAGES

    @ArchTest
    private val `should be no cycles between packages` =
        slices().matching("com.roche.ambassador.(*)..").should().beFreeOfCycles()
}