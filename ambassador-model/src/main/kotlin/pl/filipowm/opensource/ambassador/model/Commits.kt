package pl.filipowm.opensource.ambassador.model

import com.fasterxml.jackson.annotation.JsonIgnore
import pl.filipowm.opensource.ambassador.model.stats.Timeline
import java.util.*

class Commits(@JsonIgnore val opened: Timeline) {

    @JsonIgnore
    fun getCommitFrequency(): Float {
        opened.increment(Date())

//        opened.aggregate().last()

        return 0.0f
    }
}