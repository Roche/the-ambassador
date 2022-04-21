package com.roche.ambassador.storage.languages

import com.roche.ambassador.Identifiable
import com.roche.ambassador.storage.Lookup
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "languages_lookup")
class Language constructor(
    @Id @GeneratedValue
    private var id: UUID? = null,
    @Column(name = "name")
    private var name: String,
    @Column(name = "count")
    private var count: Long
) : Identifiable<UUID>, Lookup {

    override fun getId(): UUID? = id

    override fun setId(id: UUID?) {
        this.id = id
    }

    override fun getName(): String = name

    override fun getCount(): Long = count
}
