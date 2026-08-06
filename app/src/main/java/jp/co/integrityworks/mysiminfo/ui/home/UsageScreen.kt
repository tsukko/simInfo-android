package jp.co.integrityworks.mysiminfo.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import jp.co.integrityworks.mysiminfo.R

@Composable
fun UsageScreen(viewModel: HomeViewModel) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.updateUsageStats(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Mobile Data Card
        UsageSectionCard(
            title = stringResource(id = R.string.dataUsageSectionTitle),
            items = listOf(
                R.string.dataUsageToday to viewModel.dailyUsage,
                R.string.dataUsageYesterday to viewModel.yesterdayUsage,
                R.string.dataUsageWeek to viewModel.weeklyUsage,
                R.string.dataUsageMonth to viewModel.monthlyUsage,
                R.string.dataUsageDownload to viewModel.monthlyDownload,
                R.string.dataUsageUpload to viewModel.monthlyUpload
            )
        )

        // WiFi Data Card
        UsageSectionCard(
            title = stringResource(id = R.string.wifi_details_section_title),
            items = listOf(
                R.string.dataUsageWifiToday to viewModel.dailyWifiUsage,
                R.string.dataUsageWifiMonth to viewModel.monthlyWifiUsage
            )
        )
    }
}

@Composable
fun UsageSectionCard(title: String, items: List<Pair<Int, String>>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items.chunked(2).forEach { rowItems ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        rowItems.forEach { (labelId, value) ->
                            UsageItem(
                                label = stringResource(id = labelId),
                                value = value,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
