package io.github.ppoonk.airgo_master.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.ppoonk.ac.ui.component.ACIconDefault
import io.github.ppoonk.ac.ui.component.ACIconSmall
import io.github.ppoonk.ac.ui.component.ACTopAppBar
import io.github.ppoonk.ac.ui.component.AutoSizeFade
import io.github.ppoonk.airgo_master.LocalDrawerState
import io.github.ppoonk.airgo_master.LocalNavController
import kotlinx.coroutines.launch


@Composable
fun HomeScreen() {
    val vm = viewModel { HomeViewModel() }
    val navController = LocalNavController.current

    Scaffold(
        topBar = { HomeScreenTopBar() }
    ) {
        Column(
            modifier = Modifier.padding(it)
        ) {

        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenTopBar(): Unit {
    val navController = LocalNavController.current
    val drawerState = LocalDrawerState.current
    val scope = rememberCoroutineScope()

    ACTopAppBar(
        title = {
            Text(
                text = "主页"
            )
        },
        navigationIcon = {
            AutoSizeFade(
                compact = {
                    IconButton(onClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    }) {
                        ACIconSmall(ACIconDefault.DrawerOpen, null)
                    }
                }
            )
        },
        actions = {
            Row {
                IconButton(onClick = {
                    scope.launch {
                    }
                }) { ACIconSmall(ACIconDefault.Search, null) }
            }
        }
    )
}
