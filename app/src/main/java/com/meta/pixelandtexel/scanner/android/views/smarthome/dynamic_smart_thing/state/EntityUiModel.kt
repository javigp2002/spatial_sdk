package com.meta.pixelandtexel.scanner.android.views.smarthome.dynamic_smart_thing.state

import com.meta.pixelandtexel.scanner.models.devices.domain.Domain

data class EntityUiModel(
    val id: String,
    val name: String,
    val domain: Domain,
    val isUpdating: Boolean = false
)