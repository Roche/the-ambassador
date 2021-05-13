package pl.filipowm.innersource.ambassador.model.scorecard

sealed class Scorecard {


    var correctiveActions = mutableListOf<CorrectiveAction>()
}