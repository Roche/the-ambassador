package com.roche.ambassador.security

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.security.access.prepost.PreAuthorize

@SecurityRequirement(name = "Admin access", scopes = [AmbassadorUser.ADMIN])
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize(AmbassadorUser.ADMIN_AUTHORITY)
annotation class HasAdminPermission
