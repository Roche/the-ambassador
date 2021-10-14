package com.filipowm.ambassador.extensions

import kotlin.math.max
import kotlin.math.min

fun String.capitalize(): String {
    return capitalize(' ')
}

fun String.capitalize(vararg delimiters: Char): String {
    if (this.isBlank() || delimiters.isEmpty()) {
        return this
    }
    val buffer = this.toCharArray()
    var capitalizeNext = true
    val delimSet = toDelimiterSet(delimiters)
    for (i in buffer.indices) {
        val ch = buffer[i]
        if (delimSet.contains(ch.toInt())) {
            capitalizeNext = true
        } else if (capitalizeNext) {
            buffer[i] = Character.toTitleCase(ch)
            capitalizeNext = false
        }
    }
    return String(buffer)
}

fun String.capitalizeFully(): String {
    return capitalizeFully(' ')
}

fun String.capitalizeFully(vararg delimiters: Char): String {
    if (this.isBlank() || delimiters.isEmpty()) {
        return this
    }
    return this.toLowerCase().capitalize(*delimiters)
}

fun String.toCamelCase(capitalizeFirstLetter: Boolean = false, vararg delimiters: Char = " _-".toCharArray()): String {
    var str = this
    if (str.isEmpty()) {
        return str
    }
    str = str.toLowerCase()
    val strLen = str.length
    val newCodePoints = IntArray(strLen)
    var outOffset = 0
    val delimiterSet: Set<Int> = toDelimiterSet(delimiters)
    var capitalizeNext = capitalizeFirstLetter
    var isPreviousCapitalized = false
    var index = 0
    while (index < strLen) {
        val codePointOldStr = this.codePointAt(index)
        val codePoint = str.codePointAt(index)
        if (delimiterSet.contains(codePoint)) {
            capitalizeNext = outOffset != 0
            index += Character.charCount(codePoint)
            isPreviousCapitalized = Character.isUpperCase(codePointOldStr)
        } else if (capitalizeNext || outOffset == 0 && capitalizeFirstLetter) {
            val titleCaseCodePoint = Character.toTitleCase(codePoint)
            newCodePoints[outOffset++] = titleCaseCodePoint
            index += Character.charCount(titleCaseCodePoint)
            isPreviousCapitalized = true
            capitalizeNext = false
        } else if (outOffset > 0 && Character.isUpperCase(codePointOldStr) && !isPreviousCapitalized) {
            newCodePoints[outOffset++] = codePointOldStr
            index += Character.charCount(codePointOldStr)
            isPreviousCapitalized = Character.isUpperCase(codePointOldStr)
        } else {
            newCodePoints[outOffset++] = codePoint
            index += Character.charCount(codePoint)
            isPreviousCapitalized = Character.isUpperCase(codePointOldStr)
        }
    }
    return String(newCodePoints, 0, outOffset)
}

fun String.substringWithFullWords(startIndex: Int = 0, maxEndIndex: Int, vararg separators: Char): String {
    if (maxEndIndex >= this.length - 1) {
        return this
    }
    val maxPlus1 = this.substring(startIndex, maxEndIndex + 1)
    val idx = separators.map { maxPlus1.lastIndexOf(it) }.maxOf { it }
    val separatorIdx = min(max(0, idx), maxPlus1.length)
    return maxPlus1.substring(0, separatorIdx)
}

fun String.substringWithFullWords(startIndex: Int = 0, maxEndIndex: Int): String = this.substringWithFullWords(startIndex, maxEndIndex, ' ')

fun String.substringWithFullWords(startIndex: Int = 0): String = this.substringWithFullWords(startIndex, this.length - 1, ' ')

private fun toDelimiterSet(delimiters: CharArray): Set<Int> {
    val delimiterHashSet: MutableSet<Int> = HashSet()
    delimiterHashSet.add(Character.codePointAt(charArrayOf(' '), 0))
    if (delimiters.isEmpty()) {
        return delimiterHashSet
    }
    for (index in delimiters.indices) {
        delimiterHashSet.add(Character.codePointAt(delimiters, index))
    }
    return delimiterHashSet
}