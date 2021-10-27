package com.roche.ambassador.gradle.utils

import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestListener
import org.gradle.api.tasks.testing.TestResult
import org.gradle.internal.logging.text.StyledTextOutput
import org.gradle.internal.logging.text.StyledTextOutput.Style.*
import org.gradle.internal.logging.text.StyledTextOutputFactory

class TestResultLogger(outFactory: StyledTextOutputFactory) : TestListener {

    private val out: StyledTextOutput = outFactory.create(javaClass.name, LogLevel.LIFECYCLE).style(Normal)

    private val failedTests = mutableListOf<TestDescriptor>()
    private val skippedTests = mutableListOf<TestDescriptor>()

    override fun beforeSuite(suite: TestDescriptor) {
        // do nothing
    }

    private fun StyledTextOutput.error(text: String): StyledTextOutput = withStyle(Failure).text(text)

    private fun StyledTextOutput.info(text: String): StyledTextOutput = withStyle(Info).text(text)

    private fun StyledTextOutput.header(text: String): StyledTextOutput = withStyle(Header).text(text)

    private fun StyledTextOutput.success(text: String): StyledTextOutput = withStyle(Success).text(text)

    private fun TestResult.ResultType.log(): StyledTextOutput {
        return when (this) {
            TestResult.ResultType.SUCCESS -> out.withStyle(Success)
            TestResult.ResultType.FAILURE -> out.withStyle(Failure)
            else -> out.withStyle(Info)
        }.text(this)
    }

    override fun afterSuite(suite: TestDescriptor, result: TestResult) {
        if (suite.parent == null) { // root suite
            out.println()
            out.header("---- TEST REPORT ----")
            out.println()
            out.header("Test result: ")
            result.resultType.log()
            out.println()
            out.header("Test summary: ${result.testCount} tests, ")
            out.success("${result.successfulTestCount} succeeded, ")
            out.error("${result.failedTestCount} failed, ")
            out.info("${result.skippedTestCount} skipped, ")
            out.println()
            failedTests.takeIf { it.isNotEmpty() }?.prefixedSummary { out.error("\tFailed Tests") }
            skippedTests.takeIf { it.isNotEmpty() }?.prefixedSummary { out.info("\tSkipped Tests:") }
        }
    }

    override fun beforeTest(testDescriptor: TestDescriptor) {
        // do nothing
    }

    override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {
        when (result.resultType) {
            TestResult.ResultType.FAILURE -> failedTests.add(testDescriptor)
            TestResult.ResultType.SKIPPED -> skippedTests.add(testDescriptor)
            else -> Unit
        }
    }

    private fun List<TestDescriptor>.prefixedSummary(action: () -> Unit) {
        action()
        out.println()
        forEach { test -> out.println("\t\t${test.name}") }
    }
}