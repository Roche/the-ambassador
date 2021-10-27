package com.roche.ambassador.model.dataproviders

import com.roche.ambassador.model.extensions.fairy
import com.roche.ambassador.model.project.Contributor

object ContributorGenerator {

    fun generate(): Contributor {
        val person = fairy.person()
        return Contributor(person.fullName, person.email, fairy.baseProducer().randomBetween(0, 300), person.company.url)
    }

    fun generate(count: Int): List<Contributor> = (0..count-1).map { generate() }
}