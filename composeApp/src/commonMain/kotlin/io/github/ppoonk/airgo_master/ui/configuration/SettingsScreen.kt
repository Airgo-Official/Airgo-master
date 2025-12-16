package io.github.ppoonk.airgo_master.ui.configuration

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.ui.unit.dp
import io.github.ppoonk.ac.ui.component.ACIconDefault
import io.github.ppoonk.ac.ui.component.ACIconSmall
import io.github.ppoonk.ac.ui.component.ACTopAppBar
import io.github.ppoonk.ac.ui.component.AutoSizeFade
import io.github.ppoonk.ac.ui.component.NavigationRow
import io.github.ppoonk.airgo_master.LocalDrawerState
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen() {
    val pageState = rememberPagerState { SettingsDestination.entries.size }
    Scaffold(
        topBar = { SettingsScreenTopBar(pageState, SettingsDestination.entries) }
    ) { paddingValues ->
        HorizontalPager(
            state = pageState,
            userScrollEnabled = false,
            modifier = Modifier.fillMaxWidth().padding(paddingValues).padding(horizontal = 16.dp)
                .imePadding(),
        ) {
            SettingsDestination.entries[pageState.currentPage].content.invoke()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenTopBar(pageState: PagerState, destinations: List<SettingsDestination>): Unit {
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
            // animateScroll = false 时, NavigationRow 内使用 pageState.scrollToPage 切换页面，避免产生多个中间状态导致多次重组
            NavigationRow(pageState = pageState, destinations = destinations, animateScroll = false)
        }
    )
}