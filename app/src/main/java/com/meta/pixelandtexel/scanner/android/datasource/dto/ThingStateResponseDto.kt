package com.meta.pixelandtexel.scanner.android.datasource.dto

import com.google.gson.annotations.SerializedName

data class ThingsResponseDto(
    @SerializedName("entity_id") val entityId: String,
    val state: String,
    @SerializedName("attributes") val attributes: Attributes?,
    @SerializedName("last_changed") val lastChanged: String?,
    @SerializedName("last_reported") val lastReported: String?,
    @SerializedName("last_updated") val lastUpdated: String?,
    val context: Context?
)

data class Attributes(
    @SerializedName("device_class") val deviceClass: String? = null,
    @SerializedName("friendly_name") val friendlyName: String? = null,
    @SerializedName("state_class") val stateClass: String? = null,
    @SerializedName("unit_of_measurement") val unitOfMeasurement: String? = null,
    @SerializedName("volume_level") val volumeLevel: Float? = null,
    @SerializedName("is_volume_muted") val isVolumeMuted: Boolean? = null,
    @SerializedName("source_list") val source: List<String>? = null
)

data class Context(
    val id: String,
    @SerializedName("parent_id") val parentId: String? = null,
    @SerializedName("user_id") val userId: String? = null
)