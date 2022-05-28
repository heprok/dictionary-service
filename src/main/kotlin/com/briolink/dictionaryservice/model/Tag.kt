package com.briolink.dictionaryservice.model

import com.briolink.dictionaryservice.jpa.entity.TagEntity
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID
import javax.persistence.EnumType
import javax.persistence.Enumerated

data class Tag(
    @JsonProperty
    var id: UUID,
    @JsonProperty
    @Enumerated(EnumType.STRING)
    var type: TagType,
    @JsonProperty
    var slug: String,
    @JsonProperty
    var name: String
) {
    @JsonProperty
    var path: String? = null

    @JsonProperty
    var parent: Tag? = null
}

fun TagEntity.toDto(): Tag = Tag(
    id = id!!,
    type = tagEnum,
    slug = slug,
    name = name
).also {
    it.path = path
}
