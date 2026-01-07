package com.meta.pixelandtexel.scanner.android.views.smarthome.dynamic_smart_thing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meta.pixelandtexel.scanner.android.views.components.smart.EntityRow
import com.meta.pixelandtexel.scanner.android.views.components.smart.MediaPlayerComposable
import com.meta.pixelandtexel.scanner.models.devices.Device
import com.meta.pixelandtexel.scanner.models.devices.domain.MediaPlayerDomain
import com.meta.pixelandtexel.scanner.models.devices.domain.SensorDomain
import com.meta.pixelandtexel.scanner.models.devices.domain.SwitchDomain
import com.meta.pixelandtexel.scanner.utils.mytheme.MyPaddings

@Composable
fun DynamicSmartThingScreen(
    device: Device,
    viewModel: DynamicSmartThingViewmodel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(device) {
        viewModel.initialize(device)
    }

    val sensors = uiState.entities.filter { it.domain is SensorDomain }
    val controllers = uiState.entities.filter { it.domain !is SensorDomain }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(MyPaddings.M),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(MyPaddings.M)
        ) {
            Text(
                text = uiState.deviceName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = MyPaddings.M)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(MyPaddings.S)
            ) {
                if (controllers.isNotEmpty()) {
                    item {
                        Text(
                            text = "Controls",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = MyPaddings.S)
                        )
                    }
                }

                items(
                    items = controllers,
                    key = { it.id }
                ) { entity ->
                    if (entity.domain is SwitchDomain) {
                        EntityRow(
                            title = entity.name,
                            isUpdating = entity.isUpdating,
                            actualValue = entity.domain.value,
                            onSwitchToggle = { newValue ->
                                viewModel.onSwitchToggled(entity, newValue)
                            }
                        )
                    } else if (entity.domain is MediaPlayerDomain) {
                        MediaPlayerComposable(
                            title = entity.name,
                            mediaPlayerDomain = entity.domain,
                            onStartChange = { newValue ->
//                                viewModel.onSwitchToggled(entity, newValue)
                            },
                            onMuteChange = { newValue ->
//                                viewModel.onSwitchToggled(entity, newValue, "volume_mute", "is_volume_muted")
                            },
                            onVolumenChange = { newValue ->
//                                viewModel.onSliderActionChanged(entity, newValue, "volume_set", "volume_level")
                            }
                        )


                    }
                }

                if (sensors.isNotEmpty()) {
                    item {
                        Column {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = MyPaddings.M)
                            )
                            Text(
                                text = "Sensors",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = MyPaddings.M)
                            )
                        }
                    }

                    items(
                        items = sensors.chunked(3),
                    ) { rowEntities ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = MyPaddings.M),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            rowEntities.forEach { entity ->
                                val domain = entity.domain as SensorDomain
                                InfoColumn(
                                    label = entity.name,
                                    value = domain.value,
                                )
                            }
                            repeat(3 - rowEntities.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoColumn(
    label: String,
    value: String?,
    modifier: Modifier = Modifier
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value ?: "--",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
        }
    }
}

@Preview(widthDp = 400, heightDp = 100)
@Composable
fun EntityRowPreview() {
    EntityRow(
        title = "Living Room Light",
        isUpdating = false,
        actualValue = 0.2f,
        onSwitchToggle = {},
        onSliderChange = {}
    )
}