package io.github.ppoonk.airgo_master.component

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import app.cash.paging.compose.LazyPagingItems

/**
 * 解决使用 Navigation 时 LazyPagingItems 滚动位置丢失的问题
 *
 * 当使用 collectAsLazyPagingItems() 收集分页数据并配合导航组件使用时，
 * 在页面切换过程中 LazyColumn 的滚动位置会丢失。该扩展函数通过区分
 * 数据为空和非空两种情况来正确维护滚动状态。
 *
 * @see <a href="https://issuetracker.google.com/issues/177245496">相关 issue：https://issuetracker.google.com/issues/177245496</a>
 */
@Composable
fun <T : Any> LazyPagingItems<T>.rememberLazyListState(): LazyListState {
    return when (itemCount) {
        // 数据为空时，返回固定位置的 LazyListState 实例
        0 -> remember(this) { LazyListState(0, 0) }
        // 数据非空时，使用标准的 rememberLazyListState 方法
        else -> androidx.compose.foundation.lazy.rememberLazyListState()
    }
}