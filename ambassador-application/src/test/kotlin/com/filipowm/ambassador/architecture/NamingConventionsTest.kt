package com.filipowm.ambassador.architecture

import com.filipowm.ambassador.TheAmbassadorApplication
import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RestController

@AnalyzeClasses(packagesOf = [TheAmbassadorApplication::class])
class NamingConventionsTest {

    @ArchTest
    private val `services should be suffixed with 'Service'`: ArchRule = classes()
        .that().resideOutsideOfPackage("..configuration..")
        .and().resideOutsideOfPackage("..commons..")
        .and().areAnnotatedWith(Service::class.java)
        .should().haveSimpleNameEndingWith("Service")

    @ArchTest
    private val `API endpoints should be suffixed with 'Api'` = classes().that()
        .areAnnotatedWith(RestController::class.java)
        .should().haveSimpleNameEndingWith("Api")

    @ArchTest
    private val `configuration classes should be suffixed with 'Configuration'`: ArchRule = classes()
        .that().areAnnotatedWith(Configuration::class.java)
        .should().haveSimpleNameEndingWith("Configuration")

    @ArchTest
    private val `DTOs should be suffixed with 'Dto'`: ArchRule = classes()
        .that().haveSimpleNameEndingWith("DTO")
        .should().haveSimpleNameEndingWith("Dto")
        .because("keep same naming of DTOs")

    @ArchTest
    private val `exceptions should be suffixed with 'Exception'`: ArchRule = classes()
        .that().areAssignableTo(Exception::class.java)
        .should().haveSimpleNameEndingWith("Exception")

    @ArchTest
    private val `interfaces should not have names containing word 'interface'`: ArchRule = noClasses()
        .that().areInterfaces()
        .should().haveSimpleNameContaining("Interface")

    @ArchTest
    private val `interface implementation should not have dummy word 'Impl'`: ArchRule = noClasses()
        .that().implement(JavaClass.Predicates.INTERFACES)
        .should().haveSimpleNameEndingWith("Impl")
        .andShould().haveSimpleNameEndingWith("Implementation")
}