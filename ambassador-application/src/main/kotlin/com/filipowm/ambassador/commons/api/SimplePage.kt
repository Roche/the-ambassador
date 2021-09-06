package com.filipowm.ambassador.commons.api

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.data.domain.Page

data class Paged<T>(val data: List<T>, val pageInfo: PageInfo) {

    companion object {
        fun <T> from(page: Page<T>): Paged<T> {
            val pageInfo = PageInfo(page.size, page.totalPages, page.totalElements, page.number)
            return Paged(page.content, pageInfo)
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    data class PageInfo(
        val pageSize: Int,
        val totalPages: Int,
        val total: Long,
        val page: Int,
    ) {

        @JsonInclude
        fun isLast(): Boolean {
            return (this.page + 1) == this.totalPages
        }

        @JsonInclude
        fun isFirst(): Boolean {
            return this.page == 0
        }
    }
}
