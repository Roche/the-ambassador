package pl.filipowm.innersource.ambassador.model

open class Contributor(val type: ContributorType,
                       val location: Location,
                       val organization: Organization) {
    var commitsCounts = 0
    var issuesCount = 0
    var prCount = 0
}
