package com.roche.ambassador

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*
import com.tngtech.archunit.lang.syntax.elements.ClassesThat
import org.junit.*
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@AnalyzeClasses(packagesOf = [TheAmbassadorApplication::class], importOptions = [ImportOption.OnlyIncludeTests::class])
class UnitTestConventionsTest {

    @ArchTest
    private val `unit tests should be declared in test classes` = methods()
        .that().areAnnotatedWith(Test::class.java)
        .should().beDeclaredInClassesThat().areTests()
        .because("this is how tests are discovered")

    @ArchTest
    private val `unit tests should not run full spring context` = methods()
        .that().areAnnotatedWith(Test::class.java)
        .should().beDeclaredInClassesThat().areNotAnnotatedWith(SpringBootTest::class.java)
        .because("such tests are integration tests, cause they test how multiple components interact, thus they should be declared in 'integrationTest' dir")

    @ArchTest
    val `test methods should not use JUnit 4` = methods()
        .that().areDeclaredInClassesThat().areTests()
        .should().notBeAnnotatedWith(org.junit.Test::class.java)
        .andShould().notBeAnnotatedWith(Before::class.java)
        .andShould().notBeAnnotatedWith(BeforeClass::class.java)
        .andShould().notBeAnnotatedWith(After::class.java)
        .andShould().notBeAnnotatedWith(AfterClass::class.java)
        .andShould().notBeAnnotatedWith(Ignore::class.java)
        .andShould().notBeAnnotatedWith(Rule::class.java)
        .because("Ambassador is using JUnit 5")

    @ArchTest
    val `fields should not use JUnit 4`: ArchRule = fields()
        .that().areDeclaredInClassesThat().areTests()
        .should().notBeAnnotatedWith(Rule::class.java)
        .because("Ambassador is using JUnit 5")

    @ArchTest
    val `test classes should not use JUnit 4`: ArchRule = classes()
        .that().areTests()
        .should().notBeAnnotatedWith(Ignore::class.java)
        .because("Ambassador is using JUnit 5")

    private fun <T> ClassesThat<T>.areTests() = haveSimpleNameEndingWith("Test")
}