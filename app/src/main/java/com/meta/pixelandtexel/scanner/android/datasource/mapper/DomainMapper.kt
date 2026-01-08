package com.meta.pixelandtexel.scanner.android.datasource.mapper

import com.meta.pixelandtexel.scanner.android.datasource.dto.Attributes
import com.meta.pixelandtexel.scanner.models.devices.domain.Domain
import com.meta.pixelandtexel.scanner.models.devices.domain.DomainServices
import com.meta.pixelandtexel.scanner.models.devices.domain.MediaPlayerAttributes
import com.meta.pixelandtexel.scanner.models.devices.domain.MediaPlayerDomain
import com.meta.pixelandtexel.scanner.models.devices.domain.SensorDomain
import com.meta.pixelandtexel.scanner.models.devices.domain.SwitchDomain

object DomainMapper {

    fun fromEntityId(entityId: String): Domain? {
        val domainString = entityId.substringBefore(".", missingDelimiterValue = "unknown")

        return when (domainString) {
            "switch" -> SwitchDomain(
                value = false,
                services = listOf(DomainServices.TURN_OFF, DomainServices.TURN_ON)
            )

            "sensor" -> SensorDomain(
                value = "",
                services = emptyList()
            )

            "binary_sensor" -> SensorDomain(
                value = "OFF",
                services = emptyList()
            )

            "media_player" -> MediaPlayerDomain(
                value = false,
                services = listOf(
                    DomainServices.TURN_OFF, DomainServices.TURN_ON, DomainServices.VOLUME_SET,
                    DomainServices.VOLUME_MUTE, DomainServices.MEDIA_PLAY
                ),
                attributes = MediaPlayerAttributes(
                    volumeLevel = null,
                    isMuted = null,
                    source = null
                )
            )

            else -> null
        }
    }

    fun fromOtherDomainNewValue(domain: Domain, newValue: String, attributes: Attributes?): Domain {
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

            is MediaPlayerDomain -> {
                val newValueBoolean =
                    newValue.equals("ON", ignoreCase = true) || newValue.equals(
                        "true",
                        ignoreCase = true
                    )
                domain.copy(
                    value = newValueBoolean,
                    attributes = MediaPlayerAttributes(
                        volumeLevel = attributes?.volumeLevel,
                        isMuted = attributes?.isVolumeMuted,
                        source = attributes?.source
                    )
                )
            }
        }
    }
}