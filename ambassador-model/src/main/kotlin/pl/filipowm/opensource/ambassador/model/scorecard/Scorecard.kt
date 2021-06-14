package pl.filipowm.opensource.ambassador.model.scorecard

sealed class Scorecard {


    var correctiveActions = mutableListOf<CorrectiveAction>()
}