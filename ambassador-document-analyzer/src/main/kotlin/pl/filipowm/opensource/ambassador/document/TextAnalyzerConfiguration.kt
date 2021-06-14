package pl.filipowm.opensource.ambassador.document

data class TextAnalyzerConfiguration(
    val supportedLanguages: List<Language>,
    val timeout: Long
) {

}