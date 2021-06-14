package pl.filipowm.opensource.ambassador.model.score

import io.vavr.control.Either
import pl.filipowm.opensource.ambassador.model.Project
import pl.filipowm.opensource.ambassador.model.score.Score.Fail
import pl.filipowm.opensource.ambassador.model.score.Score.Pass

object CriticalityScorePolicy : ScorePolicy<Double> {
    override fun calculateScoreOf(project: Project): Either<Pass<Double>, Fail> {
//        project.commits
//                .opened
//                .last(1).years()
//                .by().months()
//                .sum()
//        project.releases.count()
//        project.releases.aggregate().last(1).year().sum()
//        project.issues
//                .comments
//                .aggregate()
//                .last(90)
//                .days()
//                .average()
        TODO("Not yet implemented")
    }

}