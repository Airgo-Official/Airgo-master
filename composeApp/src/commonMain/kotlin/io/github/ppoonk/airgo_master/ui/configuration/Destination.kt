package io.github.ppoonk.airgo_master.ui.configuration

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.ppoonk.ac.ui.component.ACDestination
import io.github.ppoonk.airgo_master.Res
import io.github.ppoonk.airgo_master.configuration_backend
import io.github.ppoonk.airgo_master.configuration_log
import io.github.ppoonk.airgo_master.configuration_payment
import io.github.ppoonk.airgo_master.configuration_push
import io.github.ppoonk.airgo_master.ui.configuration.backend.BackendScreen
import io.github.ppoonk.airgo_master.ui.configuration.log.LogScreen
import io.github.ppoonk.airgo_master.ui.configuration.payment.list.PaymentScreen
import io.github.ppoonk.airgo_master.ui.configuration.push.list.EmailScreen
import org.jetbrains.compose.resources.StringResource

sealed class SettingsDestination(
    override val title: StringResource,
    override val icon: ImageVector?,
    override val content: @Composable (() -> Unit)
) : ACDestination() {
    data object Backend :
        SettingsDestination(icon = null, title = Res.string.configuration_backend, content = { BackendScreen() })

    data object Push :
        SettingsDestination(icon = null, title = Res.string.configuration_push, content = { EmailScreen() })

    data object Pay :
        SettingsDestination(icon = null, title = Res.string.configuration_payment, content = { PaymentScreen() })

    data object Log :
        SettingsDestination(icon = null, title = Res.string.configuration_log, content = { LogScreen() })


    companion object {
        val entries: List<SettingsDestination> = listOf(Backend, Push, Pay, Log)
    }

}
