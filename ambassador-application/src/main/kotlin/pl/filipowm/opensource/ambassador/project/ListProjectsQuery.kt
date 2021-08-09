package pl.filipowm.opensource.ambassador.project

import org.springframework.web.bind.annotation.RequestParam
import pl.filipowm.opensource.ambassador.model.Visibility
import java.util.*

data class ListProjectsQuery(
    @RequestParam("visibility", required = false) var visibility: Optional<Visibility> = Optional.of(Visibility.INTERNAL),
    @RequestParam("query", required = false) var name: Optional<String> = Optional.empty()
)