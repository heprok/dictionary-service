package com.briolink.dictionaryservice.model

import com.briolink.dictionaryservice.jpa.entity.TagEntity
import com.fasterxml.jackson.annotation.JsonProperty
import javax.persistence.EnumType
import javax.persistence.Enumerated

data class Tag(
    @JsonProperty
    var id: String,
    @JsonProperty
    @Enumerated(EnumType.STRING)
    var type: TagType,
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
    type = type,
    name = name
).also {
    it.path = path
}
