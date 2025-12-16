package io.github.ppoonk.airgo_master.ui.configuration

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.ppoonk.ac.ui.component.ACDestination
import io.github.ppoonk.airgo_master.ui.configuration.backend.BackendScreen
import io.github.ppoonk.airgo_master.ui.configuration.log.LogScreen
import io.github.ppoonk.airgo_master.ui.configuration.payment.list.PaymentScreen
import io.github.ppoonk.airgo_master.ui.configuration.push.list.EmailScreen

sealed class SettingsDestination(
    override val title: String,
    override val icon: ImageVector?,
    override val content: @Composable (() -> Unit)
) : ACDestination() {
    data object Backend :
        SettingsDestination(icon = null, title = "服务端", content = { BackendScreen() })

    data object Email :
        SettingsDestination(icon = null, title = "邮箱", content = { EmailScreen() })

    data object Pay :
        SettingsDestination(icon = null, title = "支付", content = { PaymentScreen() })

    data object Log :
        SettingsDestination(icon = null, title = "日志", content = { LogScreen() })


    companion object {
        val entries: List<SettingsDestination> = listOf(Backend, Email, Pay, Log)
    }

}
