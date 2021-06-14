package pl.filipowm.opensource.ambassador.model.score.securityhealth

import io.vavr.control.Either
import pl.filipowm.opensource.ambassador.model.Project
import pl.filipowm.opensource.ambassador.model.score.Score.Fail
import pl.filipowm.opensource.ambassador.model.score.Score.Pass
import pl.filipowm.opensource.ambassador.model.score.ScorePolicy

object SecurityHealthScorePolicy : ScorePolicy<SecurityHealth> {
    override fun calculateScoreOf(project: Project): Either<Pass<SecurityHealth>, Fail> {


        TODO("Not yet implemented")
    }
}