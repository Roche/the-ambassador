package com.filipowm.ambassador.storage.indexing

import com.filipowm.ambassador.storage.Identifiable
import java.util.*
import javax.persistence.Embeddable

@Embeddable
class Lock(private var id: UUID? = null) : Identifiable {
    override fun getId(): UUID? {
        return id
    }

    fun setId(id: UUID?) {
        this.id = id
    }

}