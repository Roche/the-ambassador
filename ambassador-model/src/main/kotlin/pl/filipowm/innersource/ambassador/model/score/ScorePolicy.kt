package pl.filipowm.innersource.ambassador.model.score

import pl.filipowm.innersource.ambassador.model.Project
import pl.filipowm.innersource.ambassador.model.score.Score.*
import io.vavr.control.Either

interface ScorePolicy<T> {

    fun calculateScoreOf(project: Project) : Either<Pass<T>, Fail>

    companion object {
        fun activity() : ScorePolicy<Double> {
            return ActivityScorePolicy
        }

        fun criticality() : ScorePolicy<Double> {
            return CriticalityScorePolicy
        }

        fun securityHealth() : ScorePolicy<Double> {
            TODO("")
        }

        fun quality() : ScorePolicy<Double> {
            TODO("")
        }
    }
}