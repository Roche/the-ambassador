package com.filipowm.ambassador.storage.utils

import net.ttddyy.dsproxy.QueryCountHolder
import org.springframework.test.context.TestContext
import org.springframework.test.context.TestExecutionListener

class QueryCountCleaningListener : TestExecutionListener {

    override fun beforeTestExecution(testContext: TestContext) {
        QueryCountHolder.clear()
    }
}