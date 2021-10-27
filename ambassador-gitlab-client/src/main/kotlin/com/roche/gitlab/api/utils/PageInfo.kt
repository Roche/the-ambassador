package com.roche.gitlab.api.utils

interface PageInfo {

    fun getNumber(): Int
    fun getTotalElements(): Int
    fun getTotalPages(): Int
    fun getPerPage(): Int
    fun getNextPage(): Int

    companion object {
        fun of(
            number: Int,
            totalElements: Int,
            totalPages: Int,
            perPage: Int,
            nextPage: Int
        ): PageInfo = BasePageInfo(number, totalElements, totalPages, perPage, nextPage)

        fun from(other: PageInfo): PageInfo = of(other.getNumber(), other.getTotalElements(), other.getTotalPages(), other.getPerPage(), other.getNextPage())
    }

    data class BasePageInfo(
        private val number: Int,
        private val totalElements: Int,
        private val totalPages: Int,
        private val perPage: Int,
        private val nextPage: Int
    ) : PageInfo {
        override fun getNumber(): Int = number

        override fun getTotalElements(): Int = totalElements

        override fun getTotalPages(): Int = totalPages

        override fun getPerPage(): Int = perPage
        override fun getNextPage(): Int = nextPage
    }
}
