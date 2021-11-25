package com.roche.ambassador.storage.project

import com.roche.ambassador.model.group.Group
import org.springframework.beans.factory.annotation.Value

interface ProjectGroupProjection {
    fun getGroupId(): Long
    fun getScore(): Double
    fun getCriticality(): Double
    fun getActivity(): Double
    fun getStars(): Int
    fun getForks(): Int
    fun getType(): Group.Type
    @Value("#{target.projects.split(\",\")}")
    fun getProjectIds(): List<String> // FIXME handy workaround, cause it's not that easy to make a list of longs out of it
}
