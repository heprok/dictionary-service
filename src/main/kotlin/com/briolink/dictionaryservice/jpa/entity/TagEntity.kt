package com.briolink.dictionaryservice.jpa.entity

import com.briolink.lib.common.utils.StringUtils
import com.briolink.lib.dictionary.enumeration.TagType
import org.hibernate.Hibernate
import org.hibernate.annotations.ColumnTransformer
import org.hibernate.annotations.Type
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.PrePersist
import javax.persistence.Table

@Table(name = "tag")
@Entity
class TagEntity(
    @EmbeddedId
    var id: TagPK,
    @Column(name = "name", nullable = false, length = 500)
    var name: String
) : BaseEntity() {
    @PrePersist
    fun prePersist() {
        if (id.id.isBlank()) {
            id.id = StringUtils.slugify(name, false, 255)
        }
    }

    @ColumnTransformer(write = "to_tsvector('simple', ?)")
    @Column(name = "name_tsv", nullable = false)
    var nameTsv: String = name

    @Type(type = "ltree")
    @Column(name = "path", columnDefinition = "ltree")
    var path: String? = null
}

@Embeddable
class TagPK() : Serializable {
    @Column(name = "id", nullable = false, length = 255)
    lateinit var id: String

    @Column(name = "type", nullable = false, columnDefinition = "int2")
    private var _type: Int = -1

    var type: TagType
        get() = TagType.ofValue(_type.toString())
        set(value) {
            _type = value.value.toInt()
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as TagPK

        return id == other.id && _type == other._type
    }

    override fun hashCode(): Int = 1756406093

    fun toModel() = com.briolink.dictionaryservice.model.TagId(
        id = id,
        type = type
    )

    companion object
}
