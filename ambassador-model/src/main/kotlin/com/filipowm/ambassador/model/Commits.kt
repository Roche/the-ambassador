package com.filipowm.ambassador.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.filipowm.ambassador.model.stats.Timeline

class Commits(@JsonIgnore val timeline: Timeline) {

    @JsonIgnore
    fun getCommitFrequency(): Float {
//        opened.aggregate().last()

        return 0.0f
    }
}
