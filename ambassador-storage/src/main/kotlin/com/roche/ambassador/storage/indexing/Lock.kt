package com.roche.ambassador.storage.indexing

import com.roche.ambassador.Identifiable
import java.util.*
import javax.persistence.Embeddable

@Embeddable
class Lock(private var id: UUID? = null) : Identifiable<UUID> {
    override fun getId(): UUID? {
        return id
    }

    override fun setId(id: UUID?) {
        this.id = id
    }
}
