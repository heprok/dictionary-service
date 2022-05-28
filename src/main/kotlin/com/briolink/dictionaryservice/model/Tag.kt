package com.briolink.dictionaryservice.model

import com.briolink.dictionaryservice.jpa.entity.TagEntity
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class Tag(
    @JsonProperty
    var id: UUID,
    @JsonProperty
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

fun TagEntity.toDto() = Tag(
    id = id!!,
    type = tagEnum,
    slug = slug,
    name = name
).let {
    it.path = path
}
