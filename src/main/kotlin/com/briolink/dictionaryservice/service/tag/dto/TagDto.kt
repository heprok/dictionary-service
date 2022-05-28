package com.briolink.dictionaryservice.service.tag.dto

import com.briolink.dictionaryservice.model.TagType
import com.briolink.dictionaryservice.validation.NullOrValidPath
import com.briolink.dictionaryservice.validation.NullOrValidSlug
import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class TagDto(
    @get:NotNull
    @get:NotBlank
    @get:Size(min = 1, max = 255, message = "validation.tag.name.size")
    @get:ApiModelProperty(required = true, example = "Technology agency")
    val name: String,
    @get:ApiModelProperty(required = false, example = "technology-agency")
    @get:NullOrValidSlug
    val slug: String? = null,
    @get:NotNull
    @get:ApiModelProperty(required = true, example = "Industry")
    val type: TagType,
    @get:NullOrValidPath
    @get:ApiModelProperty(required = false, example = "1.3.1")
    val path: String? = null,
)
