package com.filipowm.gitlab.api.utils

interface PageInfo {

    fun getNumber(): Int
    fun getTotalElements(): Int
    fun getTotalPages(): Int
    fun getPerPage(): Int

    companion object {
        fun of(
            number: Int, totalElements: Int,
            totalPages: Int, perPage: Int
        ): PageInfo = BasePageInfo(number, totalElements, totalPages, perPage)

        fun from(other: PageInfo): PageInfo = of(other.getNumber(), other.getTotalElements(), other.getTotalPages(), other.getPerPage())
    }

    data class BasePageInfo(
        private val number: Int,
        private val totalElements: Int,
        private val totalPages: Int,
        private val perPage: Int
    ) : PageInfo {
        override fun getNumber(): Int = number

        override fun getTotalElements(): Int = totalElements

        override fun getTotalPages(): Int = totalPages

        override fun getPerPage(): Int = perPage

    }
}