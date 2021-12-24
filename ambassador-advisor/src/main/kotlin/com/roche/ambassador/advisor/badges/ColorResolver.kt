package com.roche.ambassador.advisor.badges

import com.roche.ambassador.advisor.common.Color

internal class ColorResolver(private val colors: Map<Color, String>) {

    fun resolve(color: Color?): String? = if (color == null) {
        null
    } else {
        colors[color]
    }

}
