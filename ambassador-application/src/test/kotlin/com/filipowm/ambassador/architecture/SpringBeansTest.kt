package com.filipowm.ambassador.architecture

import com.filipowm.ambassador.TheAmbassadorApplication
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields
import com.tngtech.archunit.library.GeneralCodingRules
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RestController

@AnalyzeClasses(packagesOf = [TheAmbassadorApplication::class])
class SpringBeansTest {

    @ArchTest
    private val `no beans should use field injection` = GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION

    @ArchTest
    private val `beans should be immutable` = fields()
        .that().areDeclaredInClassesThat()
        .areAnnotatedWith(Repository::class.java).or()
        .areAnnotatedWith(Service::class.java).or()
        .areAnnotatedWith(Component::class.java).or()
        .areAnnotatedWith(RestController::class.java).or()
        .areAnnotatedWith(Controller::class.java).or()
        .areAnnotatedWith(Configuration::class.java)
        .should().beFinal()
        .andShould().bePrivate()
        .orShould().beProtected()
        .orShould().beStatic()

}