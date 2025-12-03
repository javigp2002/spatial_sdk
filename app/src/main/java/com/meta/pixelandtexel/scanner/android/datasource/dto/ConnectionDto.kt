package com.meta.pixelandtexel.scanner.android.datasource.dto

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@InternalSerializationApi
@Serializable
data class ConnectionDto(
    val connected: Boolean
)