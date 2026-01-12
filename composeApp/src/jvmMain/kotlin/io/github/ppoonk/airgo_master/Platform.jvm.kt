package io.github.ppoonk.airgo_master

import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


class JVMPlatform : Platform {
    @Composable
    override fun HorizontalScrollbar(state: LazyListState, modifier: Modifier) {
        HorizontalScrollbar(
            modifier = modifier,
            adapter = rememberScrollbarAdapter(
                scrollState = state
            )
        )
    }

    @Composable
    override fun VerticalScrollbar(
        state: LazyListState,
        modifier: Modifier
    ) {
        VerticalScrollbar(
            modifier = modifier,
            adapter = rememberScrollbarAdapter(
                scrollState = state
            )
        )
    }

}

actual fun Platform(): Platform = JVMPlatform()