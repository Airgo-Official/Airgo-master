package io.github.ppoonk.airgo_master.ui.main

import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import io.github.ppoonk.ac.ui.component.ACNavigationDrawer
import io.github.ppoonk.airgo_master.LocalDrawerState
import io.github.ppoonk.airgo_master.component.LogoProfile
import io.github.ppoonk.airgo_master.component.UserAccountProfile


@Composable
fun MainScreen() {
    val drawerState = LocalDrawerState.current
    val pageState = rememberPagerState { Destination.entries.size }

    ACNavigationDrawer(
        drawerState = drawerState,
        pageState = pageState,
        destinations = Destination.entries,
        topContent = { LogoProfile() },
        bottomContent = { UserAccountProfile() }
    )
}