package io.github.ppoonk.airgo_master

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface Platform {
    @Composable
    fun HorizontalScrollbar(state: LazyListState, modifier: Modifier)

    @Composable
    fun VerticalScrollbar(state: LazyListState, modifier: Modifier)
}

expect fun Platform(): Platform