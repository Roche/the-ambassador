package pl.filipowm.opensource.ambassador.gitlab.api

import org.gitlab4j.api.GitLabApi
import org.gitlab4j.api.GitLabApiClient

class GitLabApiBuilder(private val url: String, private val token: String) {

    companion object {
        private const val HIGHEST_PRECEDENCE = Int.MIN_VALUE
        private const val LOWEST_PRECEDENCE = Int.MAX_VALUE
    }

    private val decorators = mutableMapOf<DecoratorEnabler, Int>()

    private fun addDecorator(order: Int, decoratorEnabler: DecoratorEnabler): GitLabApiBuilder {
        decorators.put(decoratorEnabler, order)
        return this
    }

    private fun addDecorator(decoratorEnabler: DecoratorEnabler): GitLabApiBuilder {
        return addDecorator(LOWEST_PRECEDENCE, decoratorEnabler)
    }

    fun enableExceptionHandling(): GitLabApiBuilder {
        return addDecorator(HIGHEST_PRECEDENCE) { ExceptionHandlingDecorator(it) }
    }

    private fun decorate(iterator: Iterator<DecoratorEnabler>, api: GitLabApiClient): GitLabApiClient {
        if (iterator.hasNext()) {
            val decorated = iterator.next().invoke(api)
            return decorate(iterator, decorated)
        }
        return api
    }

    fun build(): GitLabApi {
        return GitlabApiDecorator(url, token) {
            val decoratorsIterator = decorators.toList()
                .sortedBy { (_, value) -> value }
                .map { it.first }
                .iterator()
            decorate(decoratorsIterator, it)
        }
    }

}

private typealias DecoratorEnabler = (GitLabApiClient) -> GitLabApiClient