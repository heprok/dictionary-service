package com.briolink.dictionaryservice.service.tag

import com.briolink.dictionaryservice.jpa.entity.TagEntity
import com.briolink.dictionaryservice.jpa.repository.TagRepository
import com.briolink.dictionaryservice.model.Tag
import com.briolink.dictionaryservice.model.TagType
import com.briolink.dictionaryservice.model.toDto
import com.briolink.dictionaryservice.service.tag.dto.TagDto
import com.briolink.lib.common.exception.EntityExistException
import com.briolink.lib.common.exception.EntityNotFoundException
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

    fun createTag(@Valid @RequestBody dto: TagDto): Tag {
        if (dto.path != null && tagRepository.existPathByType(dto.type.value, "{${dto.path}}"))
            throw EntityExistException("exception.tag.path.exist")

        val slug = (dto.slug ?: StringUtils.slugify(dto.name, false, 255)).let {
            if (tagRepository.countByTypeAndSlug(dto.type.value, it) > 0) StringUtils.slugify(dto.name, true, 255) else it
        }

        TagEntity(type = dto.type.value, slug = slug, name = dto.name).apply {
            getPathDropLastPath(dto.path)?.also {
                if (!tagRepository.existPathByType(dto.type.value, "{$it}")) {
                    logger.error { "Path $it not found" }
                    throw ValidationException("validation.tag.path.not-exist")
                }
            }

            path = dto.path

            tagRepository.save(this).toDto().apply {
                parent = getParent(tagEnum, this.path)
                return this
            }
        }
    }

    fun getTag(type: TagType, slug: String, withParents: Boolean = false): Tag? {
        return tagRepository.findByTypeAndSlug(type.value, slug)?.let {
            val tag = it.toDto()
            if (withParents) {
                tag.parent = getParent(type, it.path)
            }

            tag
        }
    }

    private fun getParent(type: TagType, originalPath: String?): Tag? {
        val parentPath = getPathDropLastPath(originalPath) ?: return null

        val tagEntity = tagRepository.findByTypeAndPath(type.value, parentPath) ?: throw EntityNotFoundException("exception.tag.not-found")
        val tag = tagEntity.toDto().apply {
            this.parent = getParent(type, this.path)
        }

        return tag
    }

    private fun getPathDropLastPath(path: String?): String? {
        if (path == null) return null
        if (path.split(".").size == 1) return null

        return path.replaceAfterLast('.', "").dropLast(1)
    }
}
