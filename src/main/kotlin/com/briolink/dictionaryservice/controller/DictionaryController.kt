package com.briolink.dictionaryservice.controller

import com.briolink.dictionaryservice.service.DictionaryService
import com.briolink.lib.location.enumeration.TypeLocationEnum
import com.briolink.lib.location.model.LocationFullInfo
import com.briolink.lib.location.model.LocationSuggestion
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.jetbrains.annotations.NotNull
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
    fun getLocationSuggestions(
        @ApiParam(value = "query", required = false) query: String? = null,
        @ApiParam(value = "limit", required = false) limit: Int? = null,
        @ApiParam(value = "offset", required = false) offset: Int? = null,
        @ApiParam(value = "type", required = false) type: TypeLocationEnum? = null,
    ): List<LocationSuggestion> {
        TODO("not implemented")
    }

    @GetMapping("/info/{type}/{id}")
    @ApiOperation("Get full info about tag id and type")
    fun getLocationInfo(
        @NotNull @PathVariable(value = "type", required = true) type: TypeLocationEnum,
        @NotNull @PathVariable(value = "id", required = true) id: Int
    ): LocationFullInfo? {
        TODO("not implemented")
    }
}
