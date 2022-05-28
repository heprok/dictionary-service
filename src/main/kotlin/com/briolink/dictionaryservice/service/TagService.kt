package com.briolink.dictionaryservice.service

import com.briolink.dictionaryservice.dto.TagDto
import com.briolink.dictionaryservice.jpa.entity.TagEntity
import com.briolink.dictionaryservice.jpa.repository.TagRepository
import com.briolink.dictionaryservice.model.Tag
import com.briolink.dictionaryservice.model.TagType
import com.briolink.dictionaryservice.model.toDto
import com.briolink.lib.common.exception.ValidationException
import com.briolink.lib.common.utils.StringUtils
import io.swagger.v3.oas.annotations.parameters.RequestBody
import mu.KLogging
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated
import javax.transaction.Transactional
import javax.validation.Valid

@Service
@Transactional
@Validated
class TagService(
    private val tagRepository: TagRepository
) {
    companion object : KLogging()

    fun createTag(@Valid @RequestBody dto: TagDto): TagEntity {
        val slug = (dto.slug ?: StringUtils.slugify(dto.name, false, 255)).let {
            if (tagRepository.countByTypeAndSlug(dto.type.value, it) > 0) StringUtils.slugify(dto.name, true, 255) else it
        }

        TagEntity(type = dto.type.value, slug = slug, name = dto.name).apply {
            if (dto.path != null) {
                dto.path.split(".").forEach {
                    if (!tagRepository.existPathByType(dto.type.value, it)) {
                        logger.error { "Path $it not found" }
                        throw ValidationException("validation.tag.path.not-exist")
                    }
                }
            }
            path = dto.path

            return tagRepository.save(this)
        }
    }

    fun getTag(type: TagType, slug: String, withParents: Boolean = false): Tag? {
        return tagRepository.findByTypeAndSlug(type.value, slug)?.let {
            val tag = it.toDto()
            if (withParents && it.path != null) {
                it.path!!.fo
            }
        }

        private fun getParent(type: TagType, path: String): Tag {
            val tagEntity = tagRepository.findByTypeAndPath(type.value, path)
        }
    }
}
