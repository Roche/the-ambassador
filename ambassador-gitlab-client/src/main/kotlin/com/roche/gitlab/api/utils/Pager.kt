package com.roche.gitlab.api.utils

import kotlinx.coroutines.channels.ChannelIterator
import java.util.*
import java.util.concurrent.atomic.AtomicReference

class Pager<T>(
    pagination: Pagination = Pagination(),
    private val pageProvider: PageProvider<T>
) : ChannelIterator<Page<T>>, PageInfo {

    private val nextPagination: AtomicReference<Pagination> = AtomicReference(pagination)
    private val currentPage: AtomicReference<PageInfo> = AtomicReference(PageInfo.of(pagination.page, -1, -1, pagination.itemsPerPage, pagination.page + 1))

    private val currentPageData = AtomicReference<Page<T>>()

    override fun getNumber(): Int = currentPage.get().getNumber()
    override fun getTotalElements(): Int = currentPage.get().getTotalElements()
    override fun getTotalPages(): Int = currentPage.get().getTotalPages()
    override fun getPerPage(): Int = currentPage.get().getPerPage()
    override fun getNextPage(): Int = currentPage.get().getNextPage()

    operator fun iterator(): ChannelIterator<Page<T>> {
        return this
    }

    suspend fun get(): List<T> {
        val page = if (hasNext()) {
            Optional.ofNullable(next())
        } else {
            Optional.empty()
        }
        return page.map { it.content }.orElseGet { listOf() }
    }

    override suspend fun hasNext(): Boolean {
        val currentPagination = nextPagination.getAndSet(null)
        if (currentPagination != null) {
            val page = pageProvider.invoke(currentPagination)
            currentPageData.set(page)
            currentPage.set(PageInfo.from(page))
            if (page.hasNext()) {
                val nextPage = Pagination(page.getNumber() + 1, page.getPerPage())
                nextPagination.set(nextPage)
            } else {
                nextPagination.set(null)
            }
        } else {
            currentPageData.set(null)
        }

        return currentPageData.get() != null
    }

    suspend fun forEach(handler: suspend (T) -> Unit) {
        for (page in this) {
            for (entry in page) {
                handler(entry)
            }
        }
    }

    override fun next(): Page<T> {
        return currentPageData.get()
    }

}

typealias PageProvider<T> = suspend (Pagination) -> Page<T>