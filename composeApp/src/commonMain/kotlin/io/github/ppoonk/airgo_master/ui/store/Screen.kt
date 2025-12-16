package io.github.ppoonk.airgo_master.ui.store

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import io.github.ppoonk.ac.ui.component.ACIconDefault
import io.github.ppoonk.ac.ui.component.ACIconSmall
import io.github.ppoonk.ac.ui.component.ACTopAppBar
import io.github.ppoonk.ac.ui.component.AutoSizeFade
import io.github.ppoonk.ac.ui.component.NavigationRow
import io.github.ppoonk.airgo_master.LocalDrawerState
import kotlinx.coroutines.launch

@Composable
fun StoreScreen(list: List<StoreDestination> = StoreDestination.entries) {
    val pageState = rememberPagerState { list.size }
    Scaffold(
        topBar = { StoreScreenTopBar(pageState, list) }
    ) { paddingValues ->
        HorizontalPager(
            pageState,
            modifier = Modifier.padding(paddingValues)
        ) {
            list[pageState.currentPage].content.invoke()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreScreenTopBar(pageState: PagerState, list: List<StoreDestination>): Unit {
    val scope = rememberCoroutineScope()
    val drawerState = LocalDrawerState.current
    ACTopAppBar(
        navigationIcon = {
            AutoSizeFade(compact = {
                IconButton(onClick = {
                    scope.launch { drawerState.open() }
                }) {
                    ACIconSmall(ACIconDefault.DrawerOpen, null)
                }
            })
        },
        title = {
            NavigationRow(pageState, list)
        }
    )
}