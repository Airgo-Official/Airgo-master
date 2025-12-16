package io.github.ppoonk.airgo_master.component

import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


@Composable
fun LoadingDialog(vm:LoadingDialogVM) {

    val loadingWidget by vm.loadingWidget.collectAsState()

    if (loadingWidget.show) AlertDialog(
        icon = {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        },
        onDismissRequest = {
        },
        confirmButton = {
        },
        containerColor = Color.Transparent
    )
}


class LoadingDialogVM  : ViewModel(){
    private val loadingMutex = Mutex()
    private val _loadingWidget = MutableStateFlow(LoadingWidget())
    val loadingWidget: StateFlow<LoadingWidget> = _loadingWidget

    fun openLoading(): Unit {
        viewModelScope.launch {
            loadingMutex.withLock {
                _loadingWidget.value = _loadingWidget.value.copy(show = true)
            }
        }
    }

    fun closeLoading(): Unit {
        viewModelScope.launch {
            if (!_loadingWidget.value.show) return@launch
            delay(200)
            loadingMutex.withLock {
                if (_loadingWidget.value.show) {
                    _loadingWidget.value = _loadingWidget.value.copy(show = false)
                }
            }
        }
    }
}
data class LoadingWidget(
    val show: Boolean = false,
)
