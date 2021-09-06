package pl.filipowm.opensource.ambassador.model

data class Problem(
    val source: Source,
    val severity: Severity,
    val description: String,
    val suggestion: String
) {

    enum class Severity {
        HIGH,
        MEDIUM,
        LOW
    }
}
