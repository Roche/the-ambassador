package com.filipowm.ambassador.storage.utils

import net.ttddyy.dsproxy.QueryCount
import net.ttddyy.dsproxy.QueryCountHolder
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.ObjectAssert

class QueryAssertions private constructor() {

    companion object {
        fun assertQueryCount(): ObjectAssert<QueryCount> = assertThat(QueryCountHolder.getGrandTotal())
    }
}

fun ObjectAssert<QueryCount>.hasDeleted(expected: Long): ObjectAssert<QueryCount> {
    extracting { it.delete }.isEqualTo(expected)
    return this
}

fun ObjectAssert<QueryCount>.hasInserted(expected: Long): ObjectAssert<QueryCount> {
    extracting { it.insert }.isEqualTo(expected)
    return this
}

fun ObjectAssert<QueryCount>.hasSelected(expected: Long): ObjectAssert<QueryCount> {
    extracting { it.select }.isEqualTo(expected)
    return this
}

fun ObjectAssert<QueryCount>.hasUpdated(expected: Long): ObjectAssert<QueryCount> {
    extracting { it.update }.isEqualTo(expected)
    return this
}

fun ObjectAssert<QueryCount>.hasSuccessful(expected: Long): ObjectAssert<QueryCount> {
    extracting { it.success }.isEqualTo(expected)
    return this
}

fun ObjectAssert<QueryCount>.hasFailed(expected: Long): ObjectAssert<QueryCount> {
    extracting { it.failure }.isEqualTo(expected)
    return this
}