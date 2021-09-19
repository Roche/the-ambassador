package com.filipowm.ambassador

interface OAuth2ClientProvider {

    fun getOAuth2ClientProperties(): OAuth2ClientProperties?

}