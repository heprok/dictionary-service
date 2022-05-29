package com.briolink.dictionaryservice.model

import com.briolink.lib.common.jpa.type.PersistentEnum
import com.fasterxml.jackson.annotation.JsonProperty

enum class TagType(
    override val value: String,
    val idType: Int = value.toInt(),
    val idIsUUID: Boolean = false,
    val withPath: Boolean = false
) : PersistentEnum {
    @JsonProperty
    Industry("1", withPath = true),

    @JsonProperty
    Keyword("2"),

    @JsonProperty
    Vertical("3", idIsUUID = true),

    @JsonProperty
    ProductCode("4"),

    @JsonProperty
    InvestorType("5"),

    @JsonProperty
    OwnerShipStatus("6"),

    @JsonProperty
    Universe("7"),

    @JsonProperty
    DealStatus("8"),

    @JsonProperty
    ServiceProviderType("9"),

    @JsonProperty
    DealType("10", withPath = true);

    companion object {
        private val map = values().associateBy(TagType::value)

        fun ofValue(type: String): TagType = map[type] ?: throw IllegalArgumentException("$type is not a valid TagType")
    }
}
