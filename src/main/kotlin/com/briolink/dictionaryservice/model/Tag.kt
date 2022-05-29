package com.briolink.dictionaryservice.model

import com.briolink.lib.dictionary.enumeration.TagType
import javax.persistence.EnumType
import javax.persistence.Enumerated

data class Tag(
    var id: String,
    @Enumerated(EnumType.STRING)
    var type: TagType,
    var name: String
) {
    var path: String? = null
    var parent: Tag? = null
}
