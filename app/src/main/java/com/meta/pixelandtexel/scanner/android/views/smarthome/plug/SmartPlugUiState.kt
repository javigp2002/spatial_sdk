package com.meta.pixelandtexel.scanner.android.views.smarthome.plug

/**
 * @param isPlugOn Indica si el enchufe está encendido.
 * @param isLoading Indica si se está realizando una operación (ej. encender/apagar o refrescar datos).
 * @param consumptionW Consumo actual en Vatios (W). Null si no está disponible.
 * @param currentA Intensidad de la corriente en Amperios (A). Null si no está disponible.
 * @param voltageV Tensión en Voltios (V). Null si no está disponible.
 */
data class SmartPlugUiState(
    val isPlugOn: Boolean = false,
    val isLoading: Boolean = false,
    val consumptionW: Float? = null,
    val currentA: Float? = null,
    val voltageV: Float? = null
)
