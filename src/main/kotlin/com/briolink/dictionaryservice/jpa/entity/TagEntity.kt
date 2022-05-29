package com.briolink.dictionaryservice.jpa.entity

import com.briolink.dictionaryservice.model.TagType
import com.briolink.lib.common.utils.StringUtils
import org.hibernate.annotations.ColumnTransformer
import org.hibernate.annotations.Type
import java.util.UUID
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

    @PrePersist
    fun prePersist() {
        if (id == null) {
            id = if (type.idIsUUID) UUID.randomUUID().toString()
            else StringUtils.slugify(name, false, 255)
        }
    }

    @ColumnTransformer(write = "to_tsvector('simple', ?)")
    @Column(name = "name_tsv", nullable = false)
    var nameTsv: String = name

    @Type(type = "ltree")
    @Column(name = "path", columnDefinition = "ltree")
    var path: String? = null
}
