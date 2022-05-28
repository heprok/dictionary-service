package com.briolink.dictionaryservice.model

import com.fasterxml.jackson.annotation.JsonProperty

enum class TagType(val value: Int) {
    @JsonProperty
    Industry(1),

    @JsonProperty
    Keyword(2),

    @JsonProperty
    Vertical(3),

    @JsonProperty
    ProductCode(4),

    @JsonProperty
    InvestorType(5),

    @JsonProperty
    OwnerShipStatus(6),

    @JsonProperty
    Universe(7),

    @JsonProperty
    DealStatus(8),

    @JsonProperty
    ServiceProviderType(9);

    companion object {
        private val map = values().associateBy(TagType::value)

        fun ofValue(type: Int): TagType = map[type] ?: throw IllegalArgumentException("$type is not a valid TagType")
    }
}
