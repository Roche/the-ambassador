package pl.filipowm.innersource.ambassador.model.score.securityhealth

import pl.filipowm.innersource.ambassador.model.Project
import pl.filipowm.innersource.ambassador.model.score.Score.*
import pl.filipowm.innersource.ambassador.model.score.ScorePolicy
import io.vavr.control.Either

object SecurityHealthScorePolicy : ScorePolicy<SecurityHealth> {
    override fun calculateScoreOf(project: Project): Either<Pass<SecurityHealth>, Fail> {


        TODO("Not yet implemented")
    }
}