package com.roche.ambassador.model.source

import com.roche.ambassador.model.Specification
import com.roche.ambassador.model.group.Group
import com.roche.ambassador.model.group.GroupFilter
import kotlinx.coroutines.flow.Flow

interface GroupSource : Specification {
    suspend fun flowGroups(filter: GroupFilter): Flow<Group>
}