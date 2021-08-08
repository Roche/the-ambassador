package pl.filipowm.opensource.ambassador.model

import com.fasterxml.jackson.annotation.JsonIgnore
import pl.filipowm.opensource.ambassador.model.stats.Timeline

class Commits(@JsonIgnore val timeline: Timeline) {

    @JsonIgnore
    fun getCommitFrequency(): Float {
//        opened.aggregate().last()

        return 0.0f
    }
}