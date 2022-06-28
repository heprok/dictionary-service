package com.briolink.dictionaryservice.model

import com.briolink.dictionaryservice.jpa.entity.TagEntity
import com.briolink.dictionaryservice.jpa.entity.TagPK
import com.briolink.lib.dictionary.enumeration.TagType
import javax.persistence.EnumType
import javax.persistence.Enumerated

data class Tag(
    val id: TagId,
    var name: String
) {
    var path: String? = null
    var parent: Tag? = null

    companion object {
        fun fromEntity(entity: TagEntity): Tag {
            return Tag(
                id = TagId(entity.id.id, entity.id.type),
                name = entity.name
            ).apply {
                path = entity.path
            }
        }
    }
}

data class TagId(
    var id: String,
    @Enumerated(EnumType.STRING)
    var type: TagType,
) {
    fun toEntityId() = TagPK().also {
        it.id = this.id
        it.type = this.type
    }
}
