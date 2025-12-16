package io.github.ppoonk.airgo_master.component

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun Snackbar(vm: SnackbarVM, snackbarHostState: SnackbarHostState): Unit {

    val scope = rememberCoroutineScope()
    val snackbarWidget by vm.snackbarWidget.collectAsState()

    LaunchedEffect(snackbarWidget.show) {
        if (snackbarWidget.show) {
            scope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = snackbarWidget.message,
                    actionLabel = snackbarWidget.actionLabel,
                    withDismissAction = true
                )
                when (result) {
                    SnackbarResult.ActionPerformed -> {
                        snackbarWidget.action()
                    }

                    SnackbarResult.Dismissed -> {
                        vm.closeSnackbar()
                    }
                }
            }
        }
    }
}

class SnackbarVM : ViewModel() {
    private val _snackbarWidget = MutableStateFlow(SnackbarWidget())
    val snackbarWidget: StateFlow<SnackbarWidget> = _snackbarWidget

    fun openSnackbar(
        message: String,
        actionLabel: String? = null,
        action: () -> Unit = {},
    ): Unit {
        _snackbarWidget.value = SnackbarWidget(
            show = true,
            message = message,
            actionLabel = actionLabel,
            action = action,
        )
    }

    fun closeSnackbar(): Unit {
        _snackbarWidget.value = SnackbarWidget()
    }
}

data class SnackbarWidget(
    val show: Boolean = false,
    val message: String = "",
    val action: () -> Unit = {},
    val actionLabel: String? = null,
)
