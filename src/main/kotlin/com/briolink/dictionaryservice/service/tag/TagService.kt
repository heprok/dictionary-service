package com.briolink.dictionaryservice.service.tag

import com.briolink.dictionaryservice.jpa.entity.TagEntity
import com.briolink.dictionaryservice.jpa.repository.TagRepository
import com.briolink.dictionaryservice.model.Tag
import com.briolink.dictionaryservice.service.tag.dto.TagDto
import com.briolink.lib.common.exception.EntityExistException
import com.briolink.lib.common.exception.EntityNotFoundException
import com.briolink.lib.common.exception.ValidationException
import com.briolink.lib.common.utils.StringUtils
import com.briolink.lib.dictionary.enumeration.TagType
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

    fun createTag(@Valid dto: TagDto, withRandom: Boolean): Tag {

        if (tagRepository.existByTypeAndNameAndPath(dto.type, dto.name, dto.path))
            return getTag(dto.type, dto.name, dto.path, true)!!

        if (dto.path != null && tagRepository.existPathByType(dto.type, "{${dto.path}}"))
            throw EntityExistException("exception.tag.path.exist")

        var id: String = dto.id ?: StringUtils.slugify(dto.name, false, 255)

        if (tagRepository.existsById(id)) {
            if (withRandom) {
                id = StringUtils.slugify(dto.name, true, 255)
            } else {
                throw EntityExistException("exception.tag.exist")
            }
        }

        TagEntity(id = id, type = dto.type, name = dto.name).apply {
            getPathDropLastPath(dto.path)?.also {
                if (!tagRepository.existPathByType(dto.type, "{$it}")) {
                    logger.error { "Path $it not found" }
                    throw ValidationException("validation.tag.path.not-exist")
                }
            }

            path = dto.path

            tagRepository.save(this).toDto().apply {
                parent = getParent(dto.type, this.path)
                return this
            }
        }
    }

    fun getTag(id: String, withParents: Boolean = false): Tag? {
        return tagRepository.findById(id).map {
            if (withParents)
                it.toDto().apply {
                    parent = getParent(it.type, it.path)
                }
            else
                it.toDto()
        }.orElse(null)
    }

    fun getTag(type: TagType, name: String, path: String? = null, withParents: Boolean = false): Tag? {
        return tagRepository.getByTypeAndNameAndPath(type, name, path)?.let {
            val tag = it.toDto()
            if (withParents) {
                tag.parent = getParent(type, it.path)
            }
            tag
        }
    }

    private fun getParent(type: TagType, originalPath: String?): Tag? {
        val parentPath = getPathDropLastPath(originalPath) ?: return null

        val tagEntity = tagRepository.getByTypeAndPath(type, parentPath) ?: throw EntityNotFoundException("exception.tag.not-found")
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
