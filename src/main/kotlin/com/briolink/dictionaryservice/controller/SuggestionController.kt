package com.briolink.dictionaryservice.controller

import com.briolink.dictionaryservice.service.suggestion.SuggestionService
import com.briolink.dictionaryservice.service.suggestion.dto.SuggestionRequest
import com.briolink.dictionaryservice.service.tag.TagService
import com.briolink.lib.common.type.basic.BlSuggestion
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import io.swagger.v3.oas.annotations.parameters.RequestBody
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Validated
@Api(description = "Suggestion controller")
@ApiResponses(
    ApiResponse(code = 200, message = "Suggestion found"),
    ApiResponse(code = 204, message = "Suggestion not found"),
)
@RequestMapping("/api/v1/suggestions")
class SuggestionController(
    private val tagService: TagService,
    private val suggestionService: SuggestionService,
) {
    @GetMapping("/")
    @ApiOperation("Get list suggestion")
    fun getTagSuggestions(
        @RequestBody request: SuggestionRequest
    ): List<BlSuggestion> {
        return suggestionService.getSuggestions(request).map {
            BlSuggestion(it.id, it.name)
        }
    }
}
