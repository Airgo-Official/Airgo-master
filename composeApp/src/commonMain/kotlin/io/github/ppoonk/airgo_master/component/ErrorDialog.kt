package io.github.ppoonk.airgo_master.component

import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import io.github.ppoonk.ac.ui.component.ACIcon
import io.github.ppoonk.ac.ui.component.ACIconDefault
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ErrorDialog(vm: DialogVM): Unit {
    val dialogWidget by vm.dialogWidget.collectAsState()
    if (dialogWidget.show) {
        AlertDialog(
            icon = { ACIcon(ACIconDefault.Info, null) },
            title = dialogWidget.title,
            text = dialogWidget.text,
            onDismissRequest = { vm.closeDialog() },
            confirmButton = dialogWidget.confirmButton,
        )
    }
}
class DialogVM : ViewModel() {
    private val _dialogWidget = MutableStateFlow(DialogWidget())
    val dialogWidget: StateFlow<DialogWidget> = _dialogWidget
    fun openDialog(
        title: @Composable () -> Unit = {},
        text: @Composable () -> Unit = {},
        confirmButton: @Composable () -> Unit = {}
    ): Unit {
        _dialogWidget.value = DialogWidget(
            show = true,
            title = title,
            text = text,
            confirmButton = confirmButton,
        )
    }

    fun closeDialog(): Unit {
        _dialogWidget.value = DialogWidget()
    }
}

data class DialogWidget(
    val show: Boolean = false,
    val title: @Composable () -> Unit = {},
    val text: @Composable () -> Unit = {},
    val confirmButton: @Composable () -> Unit = {},
)