package com.briolink.dictionaryservice.jpa.repository

import com.briolink.dictionaryservice.jpa.entity.TagEntity
import com.briolink.dictionaryservice.model.TagType
import com.briolink.lib.common.type.interfaces.IBaseSuggestion
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface TagRepository : JpaRepository<TagEntity, UUID> {

    fun countByTypeAndId(type: TagType, id: String): Long

    // fun findByTypeAndNameAndPath(type: Int, name: String, path: String?): TagEntity?
    fun findByTypeAndPath(type: TagType, path: String): TagEntity?

    // fun findByTypeAndPathIn(type: Int, names: List<String>): List<TagEntity>
    // fun getAllByType(type: Int): Stream<TagEntity>
    fun findByTypeAndId(type: TagType, id: String): TagEntity?

    @Query(
        """
        select
            e.id as id,
            e.name as name
        from TagEntity e
        where
            e.type = :type and
            (:query is null or function('fts_query', 'simple', e.nameTsv, :query) = true)
        """,
    )
    fun getSuggestion(
        @Param("type") type: TagType,
        @Param("query") query: String? = null,
        pageable: Pageable = Pageable.ofSize(10)
    ): List<IBaseSuggestion>

    @Query(
        """
        select
            e.path as id,
            e.name as name
        from TagEntity e
        where
            e.type = :type and
            (:query is null or function('fts_query', 'simple', e.nameTsv, :query) = true) and
            (:level is null or function('nlevel', e.path) = :level) and
            ((:parents = '' or :parents = '{}') or function('lquery_arr', e.path, :parents) = true) and
            e.path is not null
        """,
    )
    fun getSuggestionWithPath(
        @Param("type") type: TagType,
        @Param("level") level: Int? = null,
        @Param("query") query: String? = null,
        @Param("parents") parents: String = "",
        pageable: Pageable = Pageable.ofSize(10)
    ): List<IBaseSuggestion>

    @Query(
        """
        select
            count(e) > 0
        from TagEntity e
        where
            e.type = :type and
            function('lquery_arr', e.path, :parents) = true and
            e.path is not null
        """,
    )
    fun existPathByType(
        @Param("type") type: TagType,
        @Param("parents") parents: String,
    ): Boolean

    // @Query(
    //     """
    //     select
    //         e
    //     from TagEntity e
    //     where
    //         e.type = :type and
    //         (:query is null or function('fts_query', 'simple', e.nameTsv, :query) = true)
    //     """,
    // )
    // fun getByType(
    //     @Param("type") type: TagType,
    //     @Param("query") query: String? = null,
    //     pageable: Pageable = Pageable.ofSize(10)
    // ): List<TagEntity>
}
