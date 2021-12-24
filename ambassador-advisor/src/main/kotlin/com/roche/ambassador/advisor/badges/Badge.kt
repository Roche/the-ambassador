package com.roche.ambassador.advisor.badges

import com.roche.ambassador.advisor.common.Color

data class Badge(
    val message: String,
    val label: String? = null,
    val url: String? = null,
    val color: Color? = null,
    val labelColor: Color? = null,
    val logo: Logo? = null,
) {

    data class Logo(
        val value: String,
        val width: Int? = null,
        val color: String? = null
    )
}