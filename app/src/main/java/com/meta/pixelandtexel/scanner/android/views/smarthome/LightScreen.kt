package com.meta.pixelandtexel.scanner.android.views.smarthome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.meta.pixelandtexel.scanner.android.views.components.smart.ControlRow
import com.meta.pixelandtexel.scanner.android.views.components.Panel
import com.meta.pixelandtexel.scanner.android.views.components.smart.SmartHomeHeader
import com.meta.pixelandtexel.scanner.utils.mytheme.AppTextStyles
import com.meta.pixelandtexel.scanner.utils.mytheme.MyPaddings
import com.meta.spatial.uiset.theme.SpatialTheme


@Composable
fun LightControlCard(
    viewModel: LightViewModel = viewModel(),
    onClose: (() -> Unit)? = null,
) {
    val state by viewModel.uiState.collectAsState()

    val pinkColor = Color(0xFFF2C0C0)
    val textColor = Color.White

    SpatialTheme{
        Panel {
            Column(
                modifier = Modifier.padding(MyPaddings.M),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(MyPaddings.M)
            ) {
                SmartHomeHeader(
                    title = "Smart Light",
                    onClose = onClose
                )

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = MyPaddings.S),
                    thickness = 1.dp,
                    color = Color.White,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "On/Off",
                        color = textColor,
                        style= AppTextStyles.Subtitle,
                    )
                    Switch(
                        checked = state.isLightOn,
                        onCheckedChange = { viewModel.toggleLight(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = pinkColor,
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color.White,
                            uncheckedBorderColor = Color.Gray
                        )
                    )
                }

                ControlRow(
                    label = "Intensity",
                    value = state.intensity,
                    onValueChange = { viewModel.updateIntensity(it) },
                    thumbColor = pinkColor,
                    textColor = textColor
                )

                ControlRow(
                    label = "Color",
                    value = state.colorValue,
                    onValueChange = { viewModel.updateColor(it) },
                    thumbColor = pinkColor,
                    textColor = textColor
                )
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun LightCardPreview() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LightControlCard(viewModel = LightViewModel())
    }
}
