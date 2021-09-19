package com.filipowm.ambassador.security

import org.springframework.security.access.prepost.PreAuthorize

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize(AmbassadorUser.ADMIN_AUTHORITY)
annotation class HasAdminPermission
