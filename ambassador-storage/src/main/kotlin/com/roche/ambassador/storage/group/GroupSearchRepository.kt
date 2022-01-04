package com.roche.ambassador.storage.group

import com.fasterxml.jackson.databind.ObjectMapper
import com.roche.ambassador.model.group.Group
import com.roche.ambassador.storage.jooq.tables.Group.GROUP
import com.roche.ambassador.storage.search.AbstractSearchRepository
import org.jooq.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional(readOnly = true)
class GroupSearchRepository(
    dsl: DSLContext,
    private val objectMapper: ObjectMapper,
    @Value("\${ambassador.language}")
    language: String
) : AbstractSearchRepository<GroupEntity, GroupSearchQuery, Long>(dsl, language) {
    override fun table(): Table<*> = GROUP

    override fun idColumn(): TableField<*, Long> = GROUP.ID

    override fun nameColumn(): TableField<*, String> = GROUP.NAME

    override fun dataColumn(): TableField<*, *> = GROUP.GROUP_

    override fun textsearchColumn(): TableField<*, *> = GROUP.TEXTSEARCH

    override fun defaultScoreColumn(): TableField<*, *> = GROUP.SCORE

    override fun mapper(): RecordMapper<Record4<Long, String, *, *>, GroupEntity> {
        return RecordMapper<Record4<Long, String, *, *>, GroupEntity> {
            GroupEntity(
                it.get(GROUP.ID),
                it.get(nameColumn()),
                group = objectMapper.readValue(it.get(GROUP.GROUP_).data(), Group::class.java),
            )
        }
    }
}
