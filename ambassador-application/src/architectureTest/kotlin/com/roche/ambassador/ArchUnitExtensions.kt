package com.roche.ambassador

import com.tngtech.archunit.base.DescribedPredicate
import com.tngtech.archunit.lang.syntax.elements.ClassesThat
import com.tngtech.archunit.lang.syntax.elements.GivenClassesConjunction
import kotlin.reflect.KClass

fun ClassesThat<GivenClassesConjunction>.areAnnotatedWithAny(vararg annotationTypes: KClass<out Annotation>): GivenClassesConjunction {
    var finalThat = this
    annotationTypes.forEach {
        finalThat = finalThat.areAnnotatedWith(it.java).or()
    }
    return finalThat.areAnnotatedWith(DescribedPredicate.alwaysFalse())
}