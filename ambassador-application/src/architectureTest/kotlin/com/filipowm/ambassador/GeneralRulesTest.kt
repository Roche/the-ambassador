package com.filipowm.ambassador

import com.filipowm.ambassador.TheAmbassadorApplication
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields
import com.tngtech.archunit.library.GeneralCodingRules.*

@AnalyzeClasses(packagesOf = [TheAmbassadorApplication::class])
class GeneralRulesTest {

    @ArchTest
    private val `no classes should throw generic exceptions` = NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS
        .because("Ambassador has own exceptions hierarchy")

    @ArchTest
    private val `no classes should access standard stream` = NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS

    @ArchTest
    private val `no classes should use java util logging` = NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING
        .because("Ambassador uses Slf4j")

    @ArchTest
    private val `loggers should be private static final`: ArchRule = fields().that().haveRawType(java.util.logging.Logger::class.java)
        .should().bePrivate()
        .andShould().beStatic()
        .andShould().beFinal()
        .because("we agreed on this convention")

    @ArchTest
    private val `no classes should use jodatime` = NO_CLASSES_SHOULD_USE_JODATIME
        .because("Java8 Date/Time API should be used instead")

}