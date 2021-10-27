package com.roche.gitlab.api.utils

class Sort private constructor(
    @QueryParam("order_by") val field: String? = null,
    @QueryParam("sort") val direction: String? = null
) {

    companion object {
        fun asc(field: String): Sort {
            return Sort(field, "asc")
        }

        fun desc(field: String): Sort {
            return Sort(field, "desc")
        }

        fun by(field: String): Sort {
            return Sort(field, null)
        }

        fun none(): Sort {
            return Sort()
        }
    }
}
