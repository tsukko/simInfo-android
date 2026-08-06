package jp.co.integrityworks.mysiminfo.ui.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Voicemail
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import jp.co.integrityworks.mysiminfo.R

@Composable
fun SimScreen(viewModel: HomeViewModel) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var hasPermission by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.entries.all { it.value }
        hasPermission = granted
        if (granted) {
            viewModel.initParameters(context)
        }
    }

    LaunchedEffect(Unit) {
        val permissions = arrayOf(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_PHONE_NUMBERS
        )
        if (permissions.all {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }
        ) {
            hasPermission = true
            viewModel.initParameters(context)
        } else {
            permissionLauncher.launch(permissions)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (!hasPermission) {
            Text(
                text = stringResource(id = R.string.phone_permission_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        SimCard(
            title = stringResource(id = R.string.sim_info_section_title),
            items = listOf(
                SimInfoItem(
                    label = stringResource(id = R.string.phoneNumberLabel),
                    value = viewModel.line1Number.ifEmpty { stringResource(id = R.string.restricted_by_android) },
                    icon = Icons.Default.Phone
                ),
                SimInfoItem(
                    label = stringResource(id = R.string.simOperatorNameLabel),
                    value = viewModel.simOperatorName,
                    icon = Icons.Default.CellTower
                ),
                SimInfoItem(
                    label = stringResource(id = R.string.simOperatorLabel),
                    value = viewModel.simOperator,
                    icon = Icons.Default.Badge
                ),
                SimInfoItem(
                    label = stringResource(id = R.string.simStateLabel),
                    value = viewModel.simState,
                    icon = Icons.Default.Info
                )
            )
        )

        SimCard(
            title = stringResource(id = R.string.sim_hardware_section_title),
            items = listOf(
                SimInfoItem(
                    label = stringResource(id = R.string.deviceIdLabel),
                    value = viewModel.deviceId,
                    icon = Icons.Default.Public
                ),
                SimInfoItem(
                    label = stringResource(id = R.string.androidIdLabel),
                    value = viewModel.androidId,
                    icon = Icons.Default.ConfirmationNumber
                ),
                SimInfoItem(
                    label = stringResource(id = R.string.simSerialNumberLabel),
                    value = viewModel.simSerialNumber,
                    icon = Icons.Default.ConfirmationNumber
                ),
                SimInfoItem(
                    label = stringResource(id = R.string.simCountryIsoLabel),
                    value = viewModel.simCountryIso.uppercase(),
                    icon = Icons.Default.Language
                )
            )
        )

        SimCard(
            title = stringResource(id = R.string.sim_others_section_title),
            items = listOf(
                SimInfoItem(
                    label = stringResource(id = R.string.voiceMailNumberLabel),
                    value = viewModel.voiceMailNumber.ifEmpty { "None" },
                    icon = Icons.Default.Voicemail
                )
            )
        )
    }
}

data class SimInfoItem(val label: String, val value: String, val icon: ImageVector)

@Composable
fun SimCard(title: String, items: List<SimInfoItem>) {
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
