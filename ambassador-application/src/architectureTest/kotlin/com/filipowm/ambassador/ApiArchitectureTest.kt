package com.filipowm.ambassador

import com.filipowm.ambassador.TheAmbassadorApplication
import com.tngtech.archunit.core.domain.JavaClass.Predicates.assignableTo
import com.tngtech.archunit.core.domain.properties.CanBeAnnotated.Predicates.annotatedWith
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*
import com.tngtech.archunit.lang.syntax.elements.ClassesThat
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.repository.Repository
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@AnalyzeClasses(packagesOf = [TheAmbassadorApplication::class])
class ApiArchitectureTest {

    @ArchTest
    private val `controller classes should be described using OpenAPI` = controllers()
        .should().beTopLevelClasses()
        .andShould().beAnnotatedWith(Tag::class.java)

    @ArchTest
    private val `controller methods should be public` = endpoints()
        .should().bePublic()
        .because("because AOP proxies are not applied to non-public methods, thus security (or other) annotations may be bypassed")

    @ArchTest
    private val `controller methods should be described using OpenAPI` = endpoints()
        .should().beAnnotatedWith(Operation::class.java)
        .andShould().beAnnotatedWith(ApiResponses::class.java)
        .orShould().beAnnotatedWith(ApiResponse::class.java)

    @ArchTest
    private val `controller methods should not declare any exceptions` = endpoints()
        .should().notDeclareThrowableOfType(assignableTo(Throwable::class.java))

    @ArchTest
    private val `transactions should no start in controllers` = controllers()
        .should()
        .notBeAnnotatedWith(Transactional::class.java)

    @ArchTest
    private val `transactions should no start in controller methods` = endpoints()
        .should()
        .notBeAnnotatedWith(Transactional::class.java)

    @ArchTest
    private val `controllers should not access repositories directly` = fields()
        .that().areDeclaredInClassesThat().areControllers()
        .should().notHaveRawType(assignableTo(Repository::class.java))
        .andShould().notHaveRawType(annotatedWith(org.springframework.stereotype.Repository::class.java))

    private fun <T> ClassesThat<T>.areControllers() = areAnnotatedWith(RestController::class.java)

    private fun controllers() = classes().that().areControllers()

    private fun endpoints() = methods().that()
        .areDeclaredInClassesThat().areControllers()
        .and().areAnnotatedWith(RequestMapping::class.java)
        .or().areAnnotatedWith(GetMapping::class.java)
        .or().areAnnotatedWith(PatchMapping::class.java)
        .or().areAnnotatedWith(PostMapping::class.java)
        .or().areAnnotatedWith(PutMapping::class.java)
        .or().areAnnotatedWith(DeleteMapping::class.java)

}