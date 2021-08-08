package pl.filipowm.opensource.ambassador.model

import pl.filipowm.opensource.ambassador.model.files.Documentation
import pl.filipowm.opensource.ambassador.model.files.File
import pl.filipowm.opensource.ambassador.model.files.License

data class Files(
    val readme: Documentation,
    val contributingGuide: Documentation,
    val license: License,
    val ciDefinition: File,
    val changelog: File,
    val gitignore: File
) {
    companion object {
        val CHANGELOG_DEFAULT = "CHANGELOG.md"
        val GITIGNORE_DEFAULT = ".gitignore"
        val CONTRIBUTING_DEFAULT = "CONTRIBUTING.md"
        val LICENSE_DEFAULT = "LICENSE"
        val README_DEFAULT = "README.md"
    }
}