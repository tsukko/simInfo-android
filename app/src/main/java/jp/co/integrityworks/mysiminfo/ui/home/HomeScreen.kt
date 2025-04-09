package jp.co.integrityworks.mysiminfo.ui.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import jp.co.integrityworks.mysiminfo.BuildConfig
import jp.co.integrityworks.mysiminfo.R
import jp.co.integrityworks.mysiminfo.ui.theme.MyAppTheme

/**
 * メイン画面のコンポーザブル
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel
) {
    val context = LocalContext.current
    var hasPermission by remember { mutableStateOf(false) }

    // パーミッション要求ランチャー
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
    }

    // 画面が開かれたときにパーミッションを確認 & 必要なら要求
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_PHONE_STATE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.READ_PHONE_STATE)
        } else {
            hasPermission = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (BuildConfig.DEBUG)
                            stringResource(id = R.string.app_name) + " (deb)"
                        else
                            stringResource(id = R.string.app_name)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Cyan
                )
            )
        },
        bottomBar = {
            // AdMobのAdViewをAndroidViewでラップして表示
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp), // 高さを適切に設定（後で動的サイズに変更可能）
                factory = { ctx ->
                    AdView(ctx).apply {
                        // AdView の設定
                        setAdSize(AdSize.BANNER) // ※ `Adaptive Banner` を使う場合は後述
                        adUnitId = ctx.getString(R.string.ad_unit_id)

                        // 広告をロード
                        loadAd(AdRequest.Builder().build())
                    }
                }
            )
        },
        content = { paddingValues ->
            BodyCompose(paddingValues = paddingValues, viewModel = viewModel)
        }
    )
}

/**
 * 電話情報の各項目を表示するコンポーザブル
 */
@Composable
fun InfoItem(title: String, data: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleSmall)
        Text(text = data, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun BodyCompose(
    paddingValues: PaddingValues,
    viewModel: HomeViewModel
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        InfoItem(
            title = stringResource(id = R.string.phoneNumberLabel),
            data = viewModel.line1Number
        )
        InfoItem(
            title = stringResource(id = R.string.simCountryIsoLabel),
            data = viewModel.simCountryIso
        )
        InfoItem(
            title = stringResource(id = R.string.simSerialNumberLabel),
            data = viewModel.simSerialNumber
        )
        InfoItem(
            title = stringResource(id = R.string.deviceIdLabel),
            data = viewModel.deviceId
        )
        InfoItem(
            title = stringResource(id = R.string.androidIdLabel),
            data = viewModel.androidId
        )
        InfoItem(
            title = stringResource(id = R.string.simOperatorLabel),
            data = viewModel.simOperator
        )
        InfoItem(
            title = stringResource(id = R.string.simOperatorNameLabel),
            data = viewModel.simOperatorName
        )
        InfoItem(
            title = stringResource(id = R.string.simStateLabel),
            data = viewModel.simState
        )
        InfoItem(
            title = stringResource(id = R.string.voiceMailNumberLabel),
            data = viewModel.voiceMailNumber
        )
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    MyAppTheme {
        HomeScreen(viewModel = HomeViewModel())
    }
}

//@Composable
//fun MinimalSwitch() {
//    var checked by remember { mutableStateOf(false) }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(horizontal = 8.dp),
//        verticalArrangement = Arrangement.spacedBy(16.dp)
//    ) {
//        Text(
//            text = "ここでは、サンプルの設定画面をイメージしています",
//            style = MaterialTheme.typography.titleSmall
//        )
//        // ローカルにタッチターゲット制約を無効化
//        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Text(text = "プッシュ通知の設定", style = MaterialTheme.typography.titleSmall)
//                Switch(
//                    checked = checked,
//                    onCheckedChange = { checked = it },
//                    colors = SwitchDefaults.colors()
//                )
//            }
//        }
//        Text(text = "設定結果: $checked", style = MaterialTheme.typography.bodyMedium)
//        Text(text = "その次のアプリ設定", style = MaterialTheme.typography.bodyMedium)
//        Text(text = "その次のアプリ設定", style = MaterialTheme.typography.bodyMedium)
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun PreviewMinimalSwitch() {
//    MinimalSwitch()
//}