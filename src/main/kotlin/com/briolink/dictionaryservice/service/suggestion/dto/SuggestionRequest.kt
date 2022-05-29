package com.briolink.dictionaryservice.service.suggestion.dto

import com.briolink.dictionaryservice.validation.PathList
import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.Max
import javax.validation.constraints.NotNull

data class SuggestionRequest(
    @get:NotNull
    @get:ApiModelProperty(required = true, example = "Industry")
    val suggestionType: SuggestionTypeEnum,
    @get:ApiModelProperty(required = false, example = "indu")
    val query: String? = null,
    @get:Max(10)
    @get:ApiModelProperty(required = false, example = "10")
    val limit: Int = 10,
    @get:ApiModelProperty(required = false, example = "0")
    @get:Max(400)
    val offset: Int = 0,
    @get:PathList
    @get:ApiModelProperty(required = false)
    val parentIds: Set<String>? = null
)
