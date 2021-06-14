package pl.filipowm.opensource.ambassador.model.score

import io.vavr.control.Either
import pl.filipowm.opensource.ambassador.model.Project
import pl.filipowm.opensource.ambassador.model.score.Score.Fail
import pl.filipowm.opensource.ambassador.model.score.Score.Pass

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