package com.filipowm.ambassador.architecture

import com.filipowm.ambassador.TheAmbassadorApplication
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@AnalyzeClasses(packagesOf = [TheAmbassadorApplication::class], importOptions = [ImportOption.OnlyIncludeTests::class])
class UnitTestConventionsTest {

    @ArchTest
    private val `unit tests should be declared in test classes` = methods()
        .that().areAnnotatedWith(Test::class.java)
        .should().beDeclaredInClassesThat().haveSimpleNameEndingWith("Test")
        .because("this is how tests are discovered")

    @ArchTest
    private val `unit tests should not run full spring context` = methods()
        .that().areAnnotatedWith(Test::class.java)
        .should().beDeclaredInClassesThat().areNotAnnotatedWith(SpringBootTest::class.java)
        .because("such tests are integration tests, cause they test how multiple components interact, thus they should be declared in 'integrationTest' dir")
}