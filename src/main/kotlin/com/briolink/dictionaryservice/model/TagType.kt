package com.briolink.dictionaryservice.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue

enum class TagType(@JsonValue val value: Int) {
    @JsonProperty
    Industry(1),

    @JsonProperty
    Keyword(2),

    @JsonProperty
    Vertical(3),

    @JsonProperty
    ProductCode(4),

    @JsonProperty
    ServiceProvider(5),

    @JsonProperty
    InvestorType(6),

    @JsonProperty
    OwnerShipStatus(7),

    @JsonProperty
    Universe(8),

    @JsonProperty
    DealStatus(9),

    @JsonProperty
    ServiceProviderType(10);

    companion object {
        private val map = values().associateBy(TagType::value)
        fun ofValue(type: Int): TagType = map[type] ?: throw IllegalArgumentException("$type is not a valid TagType")
    }
}
