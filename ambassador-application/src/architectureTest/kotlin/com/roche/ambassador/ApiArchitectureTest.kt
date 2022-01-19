package com.roche.ambassador

import com.tngtech.archunit.core.domain.JavaClass.Predicates.assignableTo
import com.tngtech.archunit.core.domain.properties.CanBeAnnotated.Predicates.annotatedWith
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchIgnore
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*
import com.tngtech.archunit.lang.syntax.elements.ClassesThat
import io.github.filipowm.api.annotations.Api
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
    private val `controller classes should be described using OpenAPI` = api()
        .should().beTopLevelClasses()
        .andShould().beAnnotatedWith(Tag::class.java)

    @ArchTest
    @ArchIgnore(reason = "After upgrading to Kotlin 1.5, suspend functions wrapping original functions are not public, even if wrapped function is public")
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
    private val `transactions should no start in controllers` = api()
        .should()
        .notBeAnnotatedWith(Transactional::class.java)

    @ArchTest
    private val `transactions should no start in controller methods` = endpoints()
        .should()
        .notBeAnnotatedWith(Transactional::class.java)

    @ArchTest
    private val `@RestController annotation should not be used` = noClasses()
        .should()
        .beAnnotatedWith(RestController::class.java)
        .because("@Api should be used instead, because it simplifies API management")

    @ArchTest
    private val `controllers should not access repositories directly` = fields()
        .that().areDeclaredInClassesThat().areApiHandlers()
        .should().notHaveRawType(assignableTo(Repository::class.java))
        .andShould().notHaveRawType(annotatedWith(org.springframework.stereotype.Repository::class.java))


    private fun <T> ClassesThat<T>.areApiHandlers() = areAnnotatedWith(Api::class.java)

    private fun api() = classes().that().areApiHandlers()

    private fun endpoints() = methods().that()
        .areDeclaredInClassesThat().areApiHandlers()
        .and().areAnnotatedWith(RequestMapping::class.java)
        .or().areAnnotatedWith(GetMapping::class.java)
        .or().areAnnotatedWith(PatchMapping::class.java)
        .or().areAnnotatedWith(PostMapping::class.java)
        .or().areAnnotatedWith(PutMapping::class.java)
        .or().areAnnotatedWith(DeleteMapping::class.java)

}