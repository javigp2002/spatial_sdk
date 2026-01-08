package com.meta.pixelandtexel.scanner.android.views.components.smart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.meta.pixelandtexel.scanner.utils.mytheme.MyPaddings

@Composable
fun EntityRow(
    title: String,
    isUpdating: Boolean,
    actualValue: Any,
    onSwitchToggle: ((Boolean) -> Unit)? = null,
    onSliderChange: ((Float) -> Unit)? = null,
    buttonIcon: ImageVector = Icons.Default.PlayArrow,
    onButtonToggled: (() -> Unit)? = null,

) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MyPaddings.M),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(MyPaddings.S)
            ) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = null,
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (isUpdating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                if (actualValue is Boolean) {
                    var switchValue by remember { mutableStateOf(actualValue) }
                    if (actualValue != switchValue) {
                        switchValue = actualValue
                    }
                    Switch(
                        checked = switchValue,
                        onCheckedChange = { isChecked ->
                            onSwitchToggle?.invoke(isChecked)
                            switchValue = isChecked
                        }
                    )
                } else if (actualValue is Float) {
                    var sliderPosition by remember { mutableFloatStateOf(actualValue) }
                    if (actualValue != sliderPosition) {
                        sliderPosition = actualValue
                    }
                    Slider(
                        value = sliderPosition,
                        onValueChangeFinished = {
                            onSliderChange?.invoke(sliderPosition)
                        },
                        onValueChange = { volume ->
                            sliderPosition = volume
                        },
                        valueRange = 0f..1f,
                    )
                } else if (onButtonToggled != null) {
                    IconButton(
                        onClick = {
                            onButtonToggled.invoke()
                        }
                    ) {
                        Icon(
                            imageVector = buttonIcon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}