package com.filipowm.ambassador.security

import com.filipowm.ambassador.UserDetailsProvider
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.userinfo.DefaultReactiveOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import reactor.core.publisher.Mono

internal class AmbassadorUserService(private val oAuth2ProvidersHolder: OAuth2ProvidersHolder) : DefaultReactiveOAuth2UserService() {

    override fun loadUser(userRequest: OAuth2UserRequest): Mono<OAuth2User> {
        val provider = oAuth2ProvidersHolder.get(userRequest.clientRegistration) ?: throw IllegalStateException("Provider must exist for registration")
        return super.loadUser(userRequest)
            .map { it to provider.userDetailsProvider(it.attributes) }
            .map(this::toAmbassadorUser)
    }

    private fun toAmbassadorUser(pair: Pair<OAuth2User, UserDetailsProvider?>): AmbassadorUser {
        if (pair.second == null) {
            throw IllegalStateException("")
        }
        val userDetailsProvider = pair.second!!
        return AmbassadorUser(
            name = userDetailsProvider.getName(),
            username = userDetailsProvider.getUsername(),
            email = userDetailsProvider.getEmail(),
            avatarUrl = userDetailsProvider.getAvatarUrl(),
            webUrl = userDetailsProvider.getWebUrl(),
            attributes = pair.first.attributes,
            authorities = getAuthorities(pair.first, userDetailsProvider)
        )
    }

    private fun getAuthorities(user: OAuth2User, provider: UserDetailsProvider): List<GrantedAuthority> {
        val authorities = user.authorities
            .map { SimpleGrantedAuthority(it.authority) }
            .toMutableList()
        if (provider.isAdmin()) {
            authorities.add(SimpleGrantedAuthority(AmbassadorUser.ADMIN))
        }
        return authorities
    }
}