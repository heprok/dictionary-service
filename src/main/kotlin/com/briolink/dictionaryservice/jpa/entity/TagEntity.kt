package com.briolink.dictionaryservice.jpa.entity

import com.briolink.dictionaryservice.model.Tag
import com.briolink.lib.common.utils.StringUtils
import com.briolink.lib.dictionary.enumeration.TagType
import org.hibernate.annotations.ColumnTransformer
import org.hibernate.annotations.Type
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.PrePersist
import javax.persistence.Table

@Table(name = "tag")
@Entity
class TagEntity(
    @Id
    @Column(name = "id", length = 255)
    var id: String? = null,
    @Type(type = "persist-enum")
    @Column(name = "type", nullable = false, columnDefinition = "int2")
    var type: TagType,
    @Column(name = "name", nullable = false, length = 255)
    var name: String
) : BaseEntity() {
    fun toDto(): Tag = Tag(
        id = id!!,
        type = type,
        name = name
    ).also {
        it.path = path
    }

    @PrePersist
    fun prePersist() {
        if (id == null) {
            id = StringUtils.slugify(name, false, 255)
        }
    }

    @ColumnTransformer(write = "to_tsvector('simple', ?)")
    @Column(name = "name_tsv", nullable = false)
    var nameTsv: String = name

    @Type(type = "ltree")
    @Column(name = "path", columnDefinition = "ltree")
    var path: String? = null
}
