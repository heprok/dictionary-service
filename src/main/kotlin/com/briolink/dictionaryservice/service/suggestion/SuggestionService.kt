package com.briolink.dictionaryservice.service.suggestion

import com.briolink.dictionaryservice.jpa.repository.TagRepository
import com.briolink.dictionaryservice.model.TagType
import com.briolink.dictionaryservice.service.suggestion.dto.SuggestionRequest
import com.briolink.dictionaryservice.service.suggestion.dto.SuggestionTypeEnum
import com.briolink.lib.common.type.interfaces.IBaseSuggestion
import com.briolink.lib.common.type.jpa.PageRequest
import mu.KLogging
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated
import javax.validation.Valid

@Service
@Validated
class SuggestionService(
    private val tagRepository: TagRepository
) {
    companion object : KLogging()

    fun getSuggestions(@Valid request: SuggestionRequest): List<IBaseSuggestion> {
        val (type, query, limit, offset, parentIds) = request
        val pageReq = PageRequest(offset, limit)

        val q = if (query.isNullOrBlank()) null else query

        return if (
            type == SuggestionTypeEnum.Industry
        ) {
            tagRepository.getSuggestionWithPath(TagType.Industry, null, q, "", pageReq)
        } else if (
            type == SuggestionTypeEnum.IndustrySector
        ) {
            tagRepository.getSuggestionWithPath(TagType.Industry, 1, q, "", pageReq)
        } else if (
            type == SuggestionTypeEnum.IndustryGroup &&
            !parentIds.isNullOrEmpty()
        ) {
            val pIds = parentIds.filter { it.isBlank().not() && it.split(".").count { c -> c.toIntOrNull() != null } == 1 }.map { "$it.*" }
            if (pIds.isEmpty()) return listOf()
            tagRepository.getSuggestionWithPath(TagType.Industry, 2, q, pIds.joinToString(",", "{", "}"), pageReq)
        } else if (
            type == SuggestionTypeEnum.IndustryCode &&
            !parentIds.isNullOrEmpty()
        ) {
            val pIds = parentIds.filter { it.isBlank().not() && it.split(".").count { c -> c.toIntOrNull() != null } == 2 }.map { "$it.*" }
            if (pIds.isEmpty()) return listOf()
            tagRepository.getSuggestionWithPath(TagType.Industry, 3, q, pIds.joinToString(",", "{", "}"), pageReq)
        } else if (
            type == SuggestionTypeEnum.ProductCode
        ) {
            tagRepository.getSuggestion(TagType.ProductCode, q, pageReq)
        } else if (
            type == SuggestionTypeEnum.IndustryGroup ||
            type == SuggestionTypeEnum.IndustryCode
        ) {
            listOf()
        } else {
            tagRepository.getSuggestion(TagType.valueOf(type.name), q, pageReq)
        }
    }
}
