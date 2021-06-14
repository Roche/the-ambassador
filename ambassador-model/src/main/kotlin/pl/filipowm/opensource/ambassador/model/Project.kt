package pl.filipowm.opensource.ambassador.model

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class Project(
    val id: Long,
    val url: String,
    val avatarUrl: String?,
    val name: String,
    val description: String?,
    val visibility: Visibility,
    val forksCount: Int?,
    val starsCount: Int?,
    val tags: List<String>?,
    val createdDate: LocalDate?,
    val lastUpdatedDate: LocalDate?,
    val commits: Commits?,
    val issues: Issues?,
    val files: Files,
    val languages: Map<String, Float>?,
    val members: Members?
) {

    @JsonIgnore
    fun getDaysSinceLastUpdate(): Long {
        return ChronoUnit.DAYS.between(lastUpdatedDate, LocalDate.now())
    }

    @JsonIgnore
    fun getDaysSinceCreation(): Long {
        return ChronoUnit.DAYS.between(createdDate, LocalDate.now())
    }

    @JsonIgnore
    fun calculateCriticalityScore(): Float {
        return 0.0f
    }

    @JsonIgnore
    fun getScorecard(): Float {
        return 0.0f
    }

    @JsonIgnore
    fun getMainLanguage(): String? {
        return languages!!.maxByOrNull { it.value }
            ?.key
    }
}
