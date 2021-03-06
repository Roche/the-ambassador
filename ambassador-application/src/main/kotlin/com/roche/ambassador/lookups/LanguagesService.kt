package com.roche.ambassador.lookups

import com.roche.ambassador.storage.languages.Language
import com.roche.ambassador.storage.languages.LanguageRepository
import com.roche.ambassador.storage.project.ProjectEntityRepository
import org.springframework.stereotype.Service

@Service
internal class LanguagesService(
    private val projectEntityRepository: ProjectEntityRepository,
    languageRepository: LanguageRepository
) : LookupService<Language, LanguageRepository>(languageRepository) {

    override fun retrieveLookups(): List<Language> {
        return projectEntityRepository.findAllLanguages()
            .map { Language(name = it.getName(), count = it.getCount()) }
    }
}
