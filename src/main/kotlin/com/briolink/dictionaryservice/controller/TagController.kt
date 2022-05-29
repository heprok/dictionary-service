package com.briolink.dictionaryservice.controller

import com.briolink.dictionaryservice.model.Tag
import com.briolink.dictionaryservice.service.tag.TagService
import com.briolink.dictionaryservice.service.tag.dto.TagDto
import com.briolink.dictionaryservice.validation.ValidSlug
import com.briolink.lib.dictionary.enumeration.TagType
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import io.swagger.v3.oas.annotations.parameters.RequestBody
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid
import javax.validation.constraints.NotNull

@RestController
@Validated
@Api(description = "Tag controller")
@ApiResponses(
    ApiResponse(code = 200, message = "Tag found"),
    ApiResponse(code = 201, message = "Tag found"),
    ApiResponse(code = 204, message = "Tag not found"),
    ApiResponse(code = 404, message = "Parent tag not found"),
    ApiResponse(code = 406, message = "Tag type not found"),
    ApiResponse(code = 409, message = "Tag and path already exists")
)
@RequestMapping("/api/v1/tags")
class TagController(
    private val tagService: TagService,
) {
    @GetMapping("/")
    @ApiOperation("Get full info about tag by id and type")
    fun getTagInfo(
        @NotNull @ApiParam(value = "type", required = true) type: TagType,
        @NotNull @ValidSlug @ApiParam(value = "id", required = true) id: String,
        @NotNull @ApiParam(defaultValue = "false", value = "withParent", required = false) withParent: Boolean = false,
    ): ResponseEntity<Tag> {
        return tagService.getTag(type, id, withParent)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.noContent().build()
    }

    @PostMapping("/")
    @ApiOperation("Create new tag")
    fun createTag(
        @Valid @RequestBody tag: TagDto,
    ): ResponseEntity<Tag> {
        return ResponseEntity(tagService.createTag(tag), HttpStatus.CREATED)
    }
}
