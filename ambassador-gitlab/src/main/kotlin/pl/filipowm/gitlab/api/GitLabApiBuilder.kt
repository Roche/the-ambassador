package pl.filipowm.gitlab.api

import org.gitlab4j.api.GitLabApi
// import io.ktor.client.HttpClient
// import io.ktor.client.engine.HttpClientEngine
// import io.ktor.client.engine.cio.CIO

class GitLabApiBuilder internal constructor() {

    private var authorizationProvider: AuthorizationProvider = AuthorizationProvider.ANONYMOUS
//    private var httpClient : HttpClient = HttpClient()

    fun url(url: String): GitLabApiBuilder {
        return this
    }

    fun apiVersion(apiVersion: GitLabApi.ApiVersion): GitLabApiBuilder {
        return this
    }

    fun authenticated(): AuthenticationBuilder {
        return AuthenticationBuilder(this)
    }

//    fun httpClient(client: HttpClient) {

//    }

    fun httpClient(): HttpClientBuilder {
        return HttpClientBuilder(this)
    }

    fun build() {
    }

    class HttpClientBuilder internal constructor(private val gitLabApiBuilder: GitLabApiBuilder) {
//        private var engine: HttpClientEngine =

//        fun engine(engine: HttpClientEngine) {
//             HttpClient(CIO)
//        }
    }

    class AuthenticationBuilder internal constructor(private val gitLabApiBuilder: GitLabApiBuilder) {

        fun withPersonalAccessToken(personalAccessToken: String): GitLabApiBuilder {
            return gitLabApiBuilder
        }

        fun withProjectAccessToken(projectAccessToken: String): GitLabApiBuilder {
            return gitLabApiBuilder
        }

        fun withOAuth2(accessToken: String): GitLabApiBuilder {
            return gitLabApiBuilder
        }

        fun withOAuth2(username: String, password: CharSequence): GitLabApiBuilder {
            return gitLabApiBuilder
        }
    }
}
