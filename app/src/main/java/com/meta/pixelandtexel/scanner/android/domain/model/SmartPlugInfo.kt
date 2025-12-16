package com.meta.pixelandtexel.scanner.android.domain.model

data class SmartPlugInfo(
    val isPlugOn: Boolean,
    val consumptionW: Float?,
    val currentA: Float?,
    val voltageV: Float?
)
