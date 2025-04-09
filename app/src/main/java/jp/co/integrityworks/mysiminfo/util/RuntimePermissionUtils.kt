package jp.co.integrityworks.mysiminfo.util

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import jp.co.integrityworks.mysiminfo.util.RuntimePermissionAlertDialog as RuntimePermissionAlertDialog1

/**
 * 指定されたパーミッション名に応じた警告ダイアログを表示するComposable。
 *
 * @param permission 表示するパーミッションの名前
 * @param onDismissRequest ダイアログを閉じる際のコールバック
 */
@Composable
fun RuntimePermissionAlertDialog(
    permission: String,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = "Permission Denied") },
        text = { Text(text = "$permission の権限が必要です。設定から許可してください。") },
        confirmButton = {
            TextButton(
                onClick = {
                    // システムのアプリ設定画面へ遷移
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + context.packageName)
                    ).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    onDismissRequest()
                }
            ) {
                Text("アプリ情報")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("キャンセル")
            }
        }
    )
}

@Preview
@Composable
fun RuntimePermissionAlertDialog() {
    val showDialog = remember { mutableStateOf(true) }
    if (showDialog.value) {
//        RuntimePermissionAlertDialog1(
//            permission = "必要なパーミッション",
//            onDismissRequest = { showDialog.value = false }
//        )
    }
}