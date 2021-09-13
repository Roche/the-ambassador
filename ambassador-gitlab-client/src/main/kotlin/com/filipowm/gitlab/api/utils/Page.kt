package com.filipowm.gitlab.api.utils

data class Page<T>(val content: List<T>, val pageInfo: PageInfo) : Collection<T> by content, PageInfo by pageInfo {
    fun hasContent(): Boolean = content.isNotEmpty()

    fun hasNext(): Boolean = (getNumber() + 1 < getTotalPages()) || (getNextPage() > getNumber())

    fun hasPrevious(): Boolean = getNumber() > 0

    fun isFirst(): Boolean = !hasPrevious()

    fun isLast(): Boolean = !hasNext()
}
