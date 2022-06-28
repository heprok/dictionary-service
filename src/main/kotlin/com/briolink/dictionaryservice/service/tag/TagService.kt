package com.briolink.dictionaryservice.service.tag

import com.briolink.dictionaryservice.jpa.entity.TagEntity
import com.briolink.dictionaryservice.jpa.entity.TagPK
import com.briolink.dictionaryservice.jpa.repository.TagRepository
import com.briolink.dictionaryservice.model.Tag
import com.briolink.dictionaryservice.model.TagId
import com.briolink.dictionaryservice.service.tag.dto.TagDto
import com.briolink.dictionaryservice.service.tag.dto.fromDto
import com.briolink.lib.common.exception.EntityExistException
import com.briolink.lib.common.exception.EntityNotFoundException
import com.briolink.lib.common.exception.ValidationException
import com.briolink.lib.common.type.jpa.PageRequest
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

    fun createTags(@Valid tags: List<@Valid TagDto>): List<Tag> {
        val existTags = mutableListOf<Tag>()

        val tagWithNotNullPath = tags.filter { it.path != null && it.path.split(".").isNotEmpty() }
        val pathsForChecked = tagWithNotNullPath.map { it.path }.toSet()
        val checkPaths = mutableMapOf<TagType, MutableSet<String>>()

        tagWithNotNullPath.forEach { tag ->
            val checkPath = getPathDropLastPath(tag.path) ?: return@forEach

            if (!pathsForChecked.contains(checkPath)) {
                checkPaths[tag.type].also {
                    val type = tag.type

                    if (it != null) it.add(checkPath)
                    else checkPaths[type] = mutableSetOf(checkPath)
                }
            }
        }

        checkPaths.forEach { (type, paths) ->
            if (!tagRepository.existPathByType(paths.count().toLong(), type.idType, paths.joinToString(",", "{", "}"))) {
                throw EntityNotFoundException("Tag with type $type, paths $paths not found")
            }

            val parentPaths = paths.mapNotNull { getPathDropLastPath(it) }.filter { !pathsForChecked.contains(it) }.toSet()

            if (parentPaths.isNotEmpty()) {
                if (!tagRepository.existPathByType(parentPaths.count().toLong(), type.idType, parentPaths.joinToString(",", "{", "}"))) {
                    throw EntityNotFoundException("Tag with type $type, paths $parentPaths not found")
                }
            }
        }

        val tagEntities = tags.map { tagDto ->
            if (tagRepository.existByTypeAndNameAndPath(tagDto.type.idType, tagDto.name, tagDto.path)) {
                existTags.add(getTag(tagDto.type, tagDto.name, tagDto.path, false)!!)
            }
            if (tagDto.path != null && tagRepository.existPathByType(tagDto.type.idType, "{${tagDto.path}}"))
                throw EntityExistException("exception.tag.path.exist")

            TagEntity(
                id = TagPK.fromDto(tagDto),
                name = tagDto.name,
            ).apply {
                path = tagDto.path
            }
        }

        val savedTags = tagRepository.saveAll(tagEntities.filter { !existTags.contains(Tag.fromEntity(it)) })

        return savedTags.map { Tag.fromEntity(it) }.plus(existTags).let {
            getParentByTags(it)
        }
    }

    fun createTag(@Valid dto: TagDto, withRandom: Boolean): Tag {
        if (tagRepository.existByTypeAndNameAndPath(dto.type.idType, dto.name, dto.path))
            return getTag(dto.type, dto.name, dto.path, true)!!

        if (dto.path != null && tagRepository.existPathByType(dto.type.idType, "{${dto.path}}"))
            throw EntityExistException("exception.tag.path.exist")

        var id: TagPK = TagPK.fromDto(dto = dto, idFromNameIfNull = true, randomId = false)

        if (tagRepository.existsById(id)) {
            if (withRandom) {
                id = TagPK.fromDto(dto = dto, idFromNameIfNull = true, randomId = true)
            } else {
                throw EntityExistException("exception.tag.exist")
            }
        }

        TagEntity(id = TagPK.fromDto(dto), name = dto.name).apply {
            getPathDropLastPath(dto.path)?.also {
                if (!tagRepository.existPathByType(dto.type.idType, "{$it}")) {
                    logger.error { "Path $it not found" }
                    throw ValidationException("validation.tag.path.not-exist")
                }
            }

            path = dto.path

            val entity = tagRepository.save(this)

            Tag.fromEntity(entity).apply {
                parent = getParent(dto.type, this.path)
                return this
            }
        }
    }

    fun getTags(ids: Set<TagId>, withParents: Boolean = false): List<Tag> {
        return tagRepository.findAllById(ids.map { it.toEntityId() }).map {
            Tag.fromEntity(it).apply {
                if (withParents) {
                    parent = getParent(it.id.type, it.path)
                }
            }
        }
    }

    fun getTags(
        ids: List<String>?,
        names: List<String>?,
        types: List<TagType>?,
        paths: List<String>?,
        withParents: Boolean,
        page: PageRequest = PageRequest(0, 30)
    ): List<Tag> {
        return tagRepository.findAll(
            ids,
            names,
            types?.map { it.idType },
            paths?.joinToString(",", "{", "}") ?: "",
            page
        ).content.map {
            Tag.fromEntity(it).apply {
                if (withParents) {
                    parent = getParent(it.id.type, it.path)
                }
            }
        }
    }

    fun getTag(id: TagId, withParents: Boolean = false): Tag? {
        return tagRepository.findById(id.toEntityId()).map {
            if (withParents)
                Tag.fromEntity(it).apply {
                    parent = getParent(it.id.type, it.path)
                }
            else
                Tag.fromEntity(it)
        }.orElse(null)
    }

    fun getTag(type: TagType, name: String, path: String? = null, withParents: Boolean = false): Tag? {
        return tagRepository.getByTypeAndNameAndPath(type.idType, name, path)?.let {
            val tag = Tag.fromEntity(it)
            if (withParents) {
                tag.parent = getParent(type, it.path)
            }
            tag
        }
    }

    private fun getParent(type: TagType, originalPath: String?): Tag? {
        val parentPath = getPathDropLastPath(originalPath) ?: return null
        val tagEntity =
            tagRepository.getByTypeAndPath(type.idType, parentPath) ?: throw EntityNotFoundException("exception.tag.not-found")

        val tag = Tag.fromEntity(tagEntity).apply {
            this.parent = getParent(type, this.path)
        }

        return tag
    }

    private fun getParentByTags(list: List<Tag>): List<Tag> {
        val mapTagsByType = list.groupBy { it.id.type }
        val resultTag = mutableListOf<Tag>()
        mapTagsByType.forEach { (tagType, tags) ->
            val paths = tags.mapNotNull { it.path }.toSet()
            val parentsPaths = mutableSetOf<String>()

            paths.forEach { tagPath ->
                var path: String? = tagPath

                while (path != null) {
                    path = getPathDropLastPath(path)?.also {
                        parentsPaths.add(it)
                    }
                }
            }

            val parents =
                tagRepository.getTagsByPathsAndType(tagType.idType, parentsPaths.joinToString(",", "{", "}")).map { Tag.fromEntity(it) }

            resultTag.addAll(
                tags.map {
                    it.parent = getParentByTag(it, parents)
                    it
                }
            )
        }

        return resultTag
    }

    private fun getParentByTag(tag: Tag, listParents: List<Tag>): Tag? {
        val tagParentPath = getPathDropLastPath(tag.path) ?: return null

        val tagsParentByPath = listParents.associateBy { it.path }

        val tagParent = tagsParentByPath.getOrDefault(tagParentPath, null)

        tagParent?.apply {
            parent = getParentByTag(this, listParents)
        }

        return tagParent
    }

    private fun getPathDropLastPath(path: String?): String? {
        if (path == null) return null
        if (path.split(".").size == 1) return null

        return path.replaceAfterLast('.', "").dropLast(1)
    }
}
