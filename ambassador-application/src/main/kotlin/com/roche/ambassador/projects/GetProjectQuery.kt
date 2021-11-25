package com.roche.ambassador.projects

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import javax.validation.constraints.Min

class GetProjectQuery(
    @PathVariable @field:Min(1) var id: Long,
    @RequestParam("reindex") var reindex: Boolean = false
)
