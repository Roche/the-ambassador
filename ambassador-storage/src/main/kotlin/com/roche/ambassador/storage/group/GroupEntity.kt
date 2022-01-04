package com.roche.ambassador.storage.group

import com.roche.ambassador.model.group.Group
import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import javax.persistence.*

@Entity
@Table(name = "\"group\"")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType::class)
// @NamedEntityGraph(
//    name = "Group.history",
//    attributeNodes = [NamedAttributeNode("history")]
// )
class GroupEntity(
    @Id var id: Long? = null,
    var name: String? = null,
    @Column(name = "full_name")
    var fullName: String? = null,
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb", name = "\"group\"")
    @Basic(fetch = FetchType.LAZY)
    var group: Group,
    @Column(name = "score")
    var score: Double? = 0.0,
    @Column(name = "activity_score")
    var activityScore: Double = 0.0,
    @Column(name = "criticality_score")
    var criticalityScore: Double = 0.0,
    @Column(name = "stars")
    var stars: Int = 0
)
