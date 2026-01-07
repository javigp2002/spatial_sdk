package com.meta.pixelandtexel.scanner.android.datasource.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceListResponseDto(
    @SerialName("devices")
    val devices: List<SmartDeviceDto>
)

@Serializable
data class SmartDeviceDto(
    @SerialName("name")
    val name: String,
    @SerialName("entities")
    val entities: List<String>
)
