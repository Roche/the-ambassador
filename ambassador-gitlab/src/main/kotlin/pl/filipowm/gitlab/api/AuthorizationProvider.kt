package pl.filipowm.gitlab.api

import java.io.IOException
import java.util.*

interface AuthorizationProvider {

    companion object {
        val ANONYMOUS = AnonymousAuthorizationProvider
    }

    @Throws(IOException::class)
    fun getAuthorization(): Optional<String>

    object AnonymousAuthorizationProvider : AuthorizationProvider {
        override fun getAuthorization(): Optional<String> = Optional.empty()
    }
}
