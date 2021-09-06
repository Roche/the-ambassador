package com.filipowm.ambassador.document

import java.security.DigestException
import java.security.GeneralSecurityException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

class Hash private constructor() {

    companion object {
        fun sha256(text: String): Optional<String> {
            if (text.isBlank()) {
                return Optional.empty()
            }
            val bytes = text.toByteArray()
            return try {
                val md = MessageDigest.getInstance("SHA-256")
                val digest = md.digest(bytes)
                val result = digest.fold("", { str, it -> str + "%02x".format(it) })
                Optional.of(result)
            } catch (e: GeneralSecurityException) {
                when (e) {
                    is DigestException, is NoSuchAlgorithmException -> Optional.empty()
                    else -> throw e
                }
            }
        }

        fun sha256OrNull(text: String): String? {
            return sha256(text).orElse(null)
        }
    }
}
