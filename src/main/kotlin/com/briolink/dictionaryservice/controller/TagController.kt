package com.briolink.dictionaryservice.controller

import com.briolink.dictionaryservice.model.Tag
import com.briolink.dictionaryservice.service.tag.TagService
import com.briolink.dictionaryservice.service.tag.dto.TagDto
import com.briolink.dictionaryservice.service.tag.dto.TagDtoList
import com.briolink.lib.common.exception.BadRequestException
import com.briolink.lib.common.type.jpa.PageRequest
import com.briolink.lib.dictionary.enumeration.TagType
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
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
    ApiResponse(code = 400, message = "Bad request"),
    ApiResponse(code = 404, message = "Parent tag not found"),
    ApiResponse(code = 406, message = "Tag type not found"),
    ApiResponse(code = 409, message = "Tag and path already exists")
)
@RequestMapping("/api/v1/tags")
class TagController(
    private val tagService: TagService,
) {
    @GetMapping("/{id}/")
    @ApiOperation(
        "Get full info about tag by id",
        notes = "The query must contain a name or id. First you search by id, then by name"
    )
    fun getTagInfo(
        @NotNull @ApiParam(defaultValue = "false", value = "withParent", required = false) withParent: Boolean = false,
        @PathVariable id: String,
    ): ResponseEntity<Tag> {
        val tag = tagService.getTag(id, withParent)

        return if (tag == null) ResponseEntity.noContent().build() else ResponseEntity.ok(tag)
    }

    @GetMapping("/", produces = ["application/json"])
    @ApiOperation("Get all tags")
    fun getTagsInfo(
        @ApiParam(value = "ids", required = false) ids: List<String>?,
        @ApiParam(value = "names", required = false) names: List<String>?,
        @ApiParam(value = "paths", required = false) paths: List<String>?,
        @ApiParam(value = "types", required = false) types: List<TagType>?,
        @ApiParam(value = "limit", required = true, defaultValue = "30") limit: Int = 30,
        @ApiParam(value = "offset", required = true, defaultValue = "0") offset: Int = 0,
        @NotNull @ApiParam(defaultValue = "false", value = "withParent", required = false) withParent: Boolean = false,
    ): ResponseEntity<List<Tag>> {

        if (ids.isNullOrEmpty() && names.isNullOrEmpty() && paths.isNullOrEmpty())
            throw BadRequestException("ids, names, paths must be not null or empty")

        if (!types.isNullOrEmpty() && names.isNullOrEmpty() && paths.isNullOrEmpty())
            throw BadRequestException("Query must be names or paths")

        if (limit < 0 || offset < 0)
            throw BadRequestException("limit and offset must be greater than 0")

        if (limit > 100)
            throw BadRequestException("limit must be less than 100")

        if (!ids.isNullOrEmpty() && ids.size > limit)
            throw BadRequestException("ids must be less than limit")

        val tags: List<Tag> =
            tagService.getTags(ids, names, types, paths, withParent, PageRequest(offset, limit))

        return if (tags.isEmpty()) ResponseEntity.noContent().build() else ResponseEntity.ok(tags)
    }

    @PostMapping("/", produces = ["application/json"], consumes = ["application/json"])
    @ApiOperation("Create new tag")
    fun createTag(
        @Valid @RequestBody tag: TagDto,
    ): ResponseEntity<Tag> {
        return ResponseEntity(tagService.createTag(tag, false), HttpStatus.CREATED)
    }

    @PostMapping("/bulk", produces = ["application/json"], consumes = ["application/json"])
    @ApiOperation("Create new tags")
    @ResponseBody
    fun createTags(
        @NotNull @Valid @RequestBody tagList: TagDtoList,
    ): ResponseEntity<List<Tag>> {
        if (tagList.tags.isEmpty())
            throw BadRequestException("tagList must be not empty")

        return ResponseEntity(tagService.createTags(tagList.tags), HttpStatus.CREATED)
    }
}
