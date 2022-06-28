package com.briolink.dictionaryservice.service.tag.dto

import com.briolink.dictionaryservice.jpa.entity.TagPK
import com.briolink.dictionaryservice.validation.NullOrValidPath
import com.briolink.dictionaryservice.validation.NullOrValidSlug
import com.briolink.lib.common.utils.StringUtils
import com.briolink.lib.dictionary.enumeration.TagType
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.annotations.ApiModelProperty
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class TagDto(
    @get:NotNull
    @get:NotBlank
    @get:Size(min = 1, max = 600, message = "validation.tag.name.size")
    @get:ApiModelProperty(required = true, example = "Technology agency")
    @JsonProperty
    val name: String,
    @get:ApiModelProperty(required = false, example = "technology-agency")
    @get:NullOrValidSlug
    @JsonProperty
    val id: String? = null,
    @get:NotNull
    @get:ApiModelProperty(required = true, example = "Industry")
    @JsonProperty
    val type: TagType,
    @get:NullOrValidPath
    @JsonProperty
    @get:ApiModelProperty(required = false, example = "1.3.1")
    val path: String? = null,
)

data class TagDtoList(
    @JsonProperty
    var tags: ArrayList<@Valid TagDto>
)

fun TagPK.Companion.fromDto(dto: TagDto, idFromNameIfNull: Boolean = true, randomId: Boolean = false): TagPK =
    TagPK().apply {
        id = dto.id ?: if (idFromNameIfNull) StringUtils.slugify(dto.name, randomId, 255) else ""
        type = dto.type
    }
