package com.briolink.dictionaryservice.controller

import com.briolink.dictionaryservice.model.Tag
import com.briolink.dictionaryservice.model.TagType
import com.briolink.dictionaryservice.service.DictionaryService
import com.briolink.dictionaryservice.validation.ValidSlug
import com.briolink.lib.common.type.basic.BlSuggestion
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.constraints.NotNull

@RestController
@Api(description = "Dictionary API")
@ApiResponses(
    ApiResponse(code = 200, message = "Dictionary found"),
    ApiResponse(code = 204, message = "Dictionary not found"),
)
@RequestMapping("/api/v1/dictionaries")
class DictionaryController(
    private val dictionaryService: DictionaryService,
) {
    @GetMapping("/suggestions")
    @ApiOperation("Get list suggestion by query")
    fun getTagSuggestions(
        @ApiParam(value = "query", required = false) query: String? = null,
        @ApiParam(value = "limit", required = false) limit: Int? = null,
        @ApiParam(value = "offset", required = false) offset: Int? = null,
        @ApiParam(value = "type", required = false) type: TagType? = null,
    ): List<BlSuggestion> {
        TODO("not implemented")
    }

    @GetMapping("/")
    @ApiOperation("Get full info about tag id and type")
    fun getTagInfo(
        @NotNull @ApiParam(value = "type", required = true) type: TagType,
        @NotNull @ValidSlug @ApiParam(value = "id", required = true) slug: String,
        @NotNull @ApiParam(defaultValue = "false", value = "withParent", required = false) withParent: Boolean = false,
    ): ResponseEntity<Tag> {
        TODO("not implemented")
    }
}
