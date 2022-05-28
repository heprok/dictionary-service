package com.briolink.dictionaryservice.jpa.entity

import com.briolink.dictionaryservice.model.TagType
import org.hibernate.annotations.ColumnTransformer
import org.hibernate.annotations.Type
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.UniqueConstraint

@Table(
    name = "tag",
    uniqueConstraints = [UniqueConstraint(columnNames = ["type", "slug"])]
)
@Entity
class TagEntity(
    @Column(name = "type", nullable = false)
    var type: Int,
    @Column(name = "slug", nullable = false, length = 255)
    var slug: String,
    @Column(name = "name", nullable = false, length = 255)
    var name: String
) : BaseEntity() {

    val tagEnum: TagType
        get() = TagType.ofValue(type)

    @ColumnTransformer(write = "to_tsvector('simple', ?)")
    @Column(name = "name_tsv", nullable = false)
    var nameTsv: String = name

    @Type(type = "ltree")
    @Column(name = "path", columnDefinition = "ltree")
    var path: String? = null
}
