package jp.co.integrityworks.mysiminfo.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import jp.co.integrityworks.mysiminfo.R

@Composable
fun DeviceScreen(viewModel: HomeViewModel) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DeviceCard(
            title = stringResource(id = R.string.os_info_card_title),
            items = listOf(
                DeviceInfoItem(
                    label = stringResource(id = R.string.android_version_label),
                    value = viewModel.androidVersion,
                    icon = Icons.Default.Android
                ),
                DeviceInfoItem(
                    label = stringResource(id = R.string.api_level_label),
                    value = viewModel.apiLevel,
                    icon = Icons.Default.DeveloperMode
                ),
                DeviceInfoItem(
                    label = stringResource(id = R.string.security_patch_label),
                    value = viewModel.securityPatch,
                    icon = Icons.Default.Security
                )
            )
        )

        DeviceCard(
            title = stringResource(id = R.string.hardware_info_card_title),
            items = listOf(
                DeviceInfoItem(
                    label = stringResource(id = R.string.model_label),
                    value = viewModel.deviceModel,
                    icon = Icons.Default.Settings
                ),
                DeviceInfoItem(
                    label = stringResource(id = R.string.manufacturer_label),
                    value = viewModel.manufacturer,
                    icon = Icons.Default.Build
                ),
                DeviceInfoItem(
                    label = stringResource(id = R.string.kernel_version_label),
                    value = viewModel.kernelVersion,
                    icon = Icons.Default.Memory
                )
            )
        )
    }
}

data class DeviceInfoItem(val label: String, val value: String, val icon: ImageVector)

@Composable
fun DeviceCard(title: String, items: List<DeviceInfoItem>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            items.forEachIndexed { index, item ->
                ListItem(
                    headlineContent = { Text(item.value) },
                    supportingContent = { Text(item.label) },
                    leadingContent = {
                        Icon(item.icon, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
                if (index < items.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.12f)
                    )
                }
            }
        }
    }
}
