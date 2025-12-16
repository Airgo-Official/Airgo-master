package io.github.ppoonk.airgo_master.ui.user.ticket

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ppoonk.ac.ui.component.ACIconDefault
import io.github.ppoonk.ac.ui.component.ACIconSmall
import io.github.ppoonk.ac.ui.component.ACTopAppBar
import io.github.ppoonk.airgo_master.LocalDrawerState
import kotlinx.coroutines.launch

@Composable
fun TicketScreen() {
    Scaffold(
        topBar = { TicketScreenTopBar() }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(it).padding(horizontal = 16.dp).imePadding(),
        ) {
            item {
                ElevatedButton({}) {
                    Text("获取 Ticket list")
                }
            }

        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketScreenTopBar() {
    val scope = rememberCoroutineScope()
    val drawerState = LocalDrawerState.current
    ACTopAppBar(
        title = { Text("工单") },
        navigationIcon = { IconButton(onClick = {
            scope.launch {
                drawerState.open()
            }
        }){
            ACIconSmall(ACIconDefault.DrawerOpen, null)
        }  }
    )
}
