package com.filipowm.gitlab.api.utils

import kotlinx.coroutines.runBlocking
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import java.util.function.Consumer
import java.util.stream.Stream
import java.util.stream.StreamSupport

class Pager<T>(
    pagination: Pagination = Pagination(),
    private val pageProvider: PageProvider<T>
) : Iterator<Page<T>>, PageInfo {

    private val nextPagination: AtomicReference<Pagination> = AtomicReference(pagination)
    private val currentPage: AtomicReference<PageInfo> = AtomicReference(PageInfo.of(pagination.page, -1, -1, pagination.itemsPerPage))

    override fun getNumber(): Int = currentPage.get().getNumber()
    override fun getTotalElements(): Int = currentPage.get().getTotalElements()
    override fun getTotalPages(): Int = currentPage.get().getTotalPages()
    override fun getPerPage(): Int = currentPage.get().getPerPage()

    override fun hasNext(): Boolean {
        return nextPagination.get() != null
    }

    override fun next(): Page<T> {
        return runBlocking { // FIXME: do not block, rather delegate coroutine scope here!
            val currentPagination = nextPagination.getAndSet(null)
            if (currentPagination != null) {
                val page = pageProvider.invoke(currentPagination)
                currentPage.set(PageInfo.from(page))
                if (page.hasNext()) {
                    val nextPage = Pagination(page.getNumber() + 1, page.getPerPage())
                    nextPagination.set(nextPage)
                }
                page
            } else {
                throw IllegalStateException("No more pages available")
            }
        }
    }

    fun stream(): Stream<T> {
        return StreamSupport.stream(spliterator(), false)
    }

    fun spliterator(): Spliterator<T> {
        return PagerSpliterator(this)
    }

    private class PagerSpliterator<T>(private val pager: Pager<T>) : Spliterator<T> {

        private var elements: Iterator<T> = listOf<T>().iterator()

        override fun tryAdvance(action: Consumer<in T>?): Boolean {
            if (action == null) {
                throw NullPointerException("")
            }
            if (elements.hasNext()) {
                action.accept(elements.next())
                return true
            } else if (pager.hasNext()) {
                elements = pager.next().iterator()
                if (elements.hasNext()) {
                    action.accept(elements.next())
                    return true
                }
            }
            return false
        }

        override fun trySplit(): Spliterator<T>? {
            return null
        }

        override fun estimateSize(): Long {
            return pager.currentPage.get().getTotalElements().toLong()
        }

        override fun characteristics(): Int {
            return Spliterator.SIZED or Spliterator.NONNULL or Spliterator.IMMUTABLE
        }

    }

}

typealias PageProvider<T> = suspend (Pagination) -> Page<T>
