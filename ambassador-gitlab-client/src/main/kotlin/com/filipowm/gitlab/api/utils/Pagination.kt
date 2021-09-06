package com.filipowm.gitlab.api.utils

class Pagination(
    @QueryParam("page") val page: Int = 1,
    @QueryParam("per_page") val itemsPerPage: Int = 25
)
