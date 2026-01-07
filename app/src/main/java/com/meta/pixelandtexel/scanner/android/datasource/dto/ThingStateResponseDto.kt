package com.meta.pixelandtexel.scanner.android.datasource.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ThingsResponseDto(
    @SerialName("entity_id") val entityId: String,
    val state: String,
    val attributes: Attributes?,
    @SerialName("last_changed") val lastChanged: String?,
    @SerialName("last_reported") val lastReported: String?,
    @SerialName("last_updated") val lastUpdated: String?,
    val context: Context?
)

@Serializable
data class Attributes(
    @SerialName("device_class") val deviceClass: String? = null,
    @SerialName("friendly_name") val friendlyName: String? = null,
    @SerialName("state_class") val stateClass: String? = null,
    @SerialName("unit_of_measurement") val unitOfMeasurement: String? = null
)

@Serializable
data class Context(
    val id: String,
    @SerialName("parent_id") val parentId: String? = null,
    @SerialName("user_id") val userId: String? = null
)