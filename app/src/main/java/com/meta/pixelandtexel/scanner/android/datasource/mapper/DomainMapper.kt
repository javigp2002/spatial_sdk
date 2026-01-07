package com.meta.pixelandtexel.scanner.android.datasource.mapper

import com.meta.pixelandtexel.scanner.models.devices.domain.Domain
import com.meta.pixelandtexel.scanner.models.devices.domain.SensorDomain
import com.meta.pixelandtexel.scanner.models.devices.domain.SwitchDomain

object DomainMapper {

    fun fromEntityId(entityId: String): Domain? {
        val domainString = entityId.substringBefore(".", missingDelimiterValue = "unknown")

        return when (domainString) {
            "switch" -> SwitchDomain(
                value = false,
                services = listOf("turn_on", "turn_off")
            )

            "sensor" -> SensorDomain(
                value = "",
                services = emptyList()
            )

            "binary_sensor" -> SensorDomain(
                value = "OFF",
                services = emptyList()
            )

            else -> null
        }
    }

    fun fromOtherDomainNewValue(domain: Domain, newValue: String): Domain {
        return when (domain) {
            is SwitchDomain -> {
                val newValueBoolean =
                    newValue.equals("ON", ignoreCase = true) || newValue.equals(
                        "true",
                        ignoreCase = true
                    )
                domain.copy(value = newValueBoolean)
            }

            is SensorDomain -> domain.copy(value = newValue)
        }
    }
}