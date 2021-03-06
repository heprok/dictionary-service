package com.briolink.dictionaryservice.jpa.repository

import com.briolink.dictionaryservice.jpa.entity.TagEntity
import com.briolink.dictionaryservice.jpa.entity.TagPK
import com.briolink.lib.common.type.interfaces.IBaseSuggestion
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface TagRepository : JpaRepository<TagEntity, TagPK> {

    // fun findByTypeAndNameAndPath(type: Int, name: String, path: String?): TagEntity?
    @Query("SELECT t FROM TagEntity t WHERE t.id._type = ?1 AND t.path = ?2")
    fun getByTypeAndPath(type: Int, path: String): TagEntity?

    // fun findByTypeAndPathIn(type: Int, names: List<String>): List<TagEntity>
    // fun getAllByType(type: Int): Stream<TagEntity>
    @Query("select t from TagEntity t where t.id._type = ?1 and t.name = ?2 and t.path = ?3")
    fun getByTypeAndNameAndPath(type: Int, name: String, path: String?): TagEntity?

    @Query(
        """
        select count(t) > 0 from TagEntity t
        where t.id._type = ?1 and t.name = ?2 and t.path = ?3
    """
    )
    fun existByTypeAndNameAndPath(type: Int, name: String, path: String?): Boolean

    @Query(
        """
        select
            function('concat_ws', ';', e.id._type, e.id) as id,
            e.name as name
        from TagEntity e
        where
            e.id._type = :type and
            (:query is null or function('fts_query', 'simple', e.nameTsv, :query) = true)
        """,
    )
    fun getSuggestion(
        @Param("type") type: Int,
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
            e.id._type = :type and
            (:query is null or function('fts_query', 'simple', e.nameTsv, :query) = true) and
            (:level is null or function('nlevel', e.path) = :level) and
            ((:parents = '' or :parents = '{}') or function('lquery_arr', e.path, :parents) = true) and
            e.path is not null
        """,
    )
    fun getSuggestionWithPath(
        @Param("type") type: Int,
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
            e.id._type = :type and
            function('lquery_arr', e.path, :parents) = true and
            e.path is not null
        """,
    )
    fun existPathByType(
        @Param("type") type: Int,
        @Param("parents") parents: String,
    ): Boolean

    @Query(
        """
        select
            count(e) = :countParents
        from TagEntity e
        where
            e.id._type = :type and
            function('lquery_arr', e.path, :parents) = true and
            e.path is not null
        """,
    )
    fun existPathByType(
        @Param("countParents") countParents: Long,
        @Param("type") type: Int,
        @Param("parents") parents: String,
    ): Boolean

    @Query(
        """
        select
            e
        from TagEntity e
        where
            e.id._type = :type and
            function('lquery_arr', e.path, :parents) = true and
            e.path is not null
        """,
    )
    fun getTagsByPathsAndType(
        @Param("type") type: Int,
        @Param("parents") parents: String,
    ): List<TagEntity>

    @Query(
        """
        select t from TagEntity t
        where
            (:ids is null or t.id.id in :ids) and
            (:names is null or t.name in :names) and
            (:types is null or t.id._type in :types) and
            ((:paths = '' or :paths = '{}') or function('lquery_arr', t.path, :paths) = true)
    """

    )
    fun findAll(
        @Param("ids") ids: Collection<String>?,
        @Param("names") names: Collection<String>?,
        @Param("types") types: Collection<Int>?,
        @Param("paths") paths: String,
        pageable: Pageable = Pageable.ofSize(10)
    ): Page<TagEntity>

    @Query(
        """
        with p1 as (
        select coalesce((select ltree2text(subpath(path, 0, 1)) from tag where name = :parentName1 and type = :type and nlevel(path) = 1), (select cast(coalesce((max(cast(ltree2text(subpath(path, 0, 1)) as int)) + 1), 1) as text) from tag where nlevel(path) = 1 and type = :type)) as p
        ),
        p2 as (
        select coalesce((select ltree2text(subpath(path, 1, 1)) from tag, p1 where name = :parentName2 and type = :type and nlevel(path) = 2 and cast(p1.p as ltree) @> path), (select cast(coalesce((max(cast(ltree2text(subpath(path, 1, 1)) as int)) + 1), 1) as text) from tag, p1 where nlevel(path) = 2 and type = :type and cast(p1.p as ltree) @> path)) as p
        ),
        p3 as (
        select coalesce((select ltree2text(subpath(path, 2, 1)) from tag, p1, p2 where name = :parentName3 and type = :type and nlevel(path) = 3 and cast(concat_ws('.', p1.p, p2.p) as ltree) @> path), (select cast(coalesce((max(cast(ltree2text(subpath(path, 2, 1)) as int)) + 1), 1) as text) from tag, p1, p2 where nlevel(path) = 3 and type = :type and cast(concat_ws('.', p1.p, p2.p) as ltree) @> path)) as p
        )
        select concat_ws('.',
            (select p1.p from p1),
            (select p2.p from p2),
            (select p3.p from p3)
        )
        """,
        nativeQuery = true
    )
    fun findFreeTagPathLevel3(
        @Param("type") type: Int,
        @Param("parentName1") parentName1: String,
        @Param("parentName2") parentName2: String,
        @Param("parentName3") parentName3: String
    ): String

    @Query(
        """
        with p1 as (
        select coalesce((select ltree2text(subpath(path, 0, 1)) from tag where name = :parentName1 and type = :type and nlevel(path) = 1), (select cast(coalesce((max(cast(ltree2text(subpath(path, 0, 1)) as int)) + 1), 1) as text) from tag where nlevel(path) = 1 and type = :type)) as p
        ),
        p2 as (
        select coalesce((select ltree2text(subpath(path, 1, 1)) from tag, p1 where name = :parentName2 and type = :type and nlevel(path) = 2 and cast(p1.p as ltree) @> path), (select cast(coalesce((max(cast(ltree2text(subpath(path, 1, 1)) as int)) + 1), 1) as text) from tag, p1 where nlevel(path) = 2 and type = :type and cast(p1.p as ltree) @> path)) as p
        )
        select concat_ws('.',
            (select p1.p from p1),
            (select p2.p from p2)
        )
        """,
        nativeQuery = true
    )
    fun findFreeTagPathLevel2(
        @Param("type") type: Int,
        @Param("parentName1") parentName1: String,
        @Param("parentName2") parentName2: String,
    ): String
}
