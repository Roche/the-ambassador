package com.filipowm.ambassador.extensions

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
    var index = 0
    while (index < strLen) {
        val codePoint = str.codePointAt(index)
        if (delimiterSet.contains(codePoint)) {
            capitalizeNext = outOffset != 0
            index += Character.charCount(codePoint)
        } else if (capitalizeNext || outOffset == 0 && capitalizeFirstLetter) {
            val titleCaseCodePoint = Character.toTitleCase(codePoint)
            newCodePoints[outOffset++] = titleCaseCodePoint
            index += Character.charCount(titleCaseCodePoint)
            capitalizeNext = false
        } else {
            newCodePoints[outOffset++] = codePoint
            index += Character.charCount(codePoint)
        }
    }
    return String(newCodePoints, 0, outOffset)
}

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