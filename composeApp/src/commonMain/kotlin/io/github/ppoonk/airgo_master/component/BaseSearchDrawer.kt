package io.github.ppoonk.airgo_master.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import io.github.ppoonk.ac.ui.component.ACClearVisibilityToggle
import io.github.ppoonk.ac.ui.component.ACDragHandle
import io.github.ppoonk.ac.ui.component.ACIconDefault
import io.github.ppoonk.ac.ui.component.ACIconSmall
import io.github.ppoonk.ac.ui.component.ACLabelInfo
import io.github.ppoonk.ac.ui.component.ACModalBottomSheet
import io.github.ppoonk.ac.ui.component.ACSingleChoiceRow
import io.github.ppoonk.ac.ui.component.ACTextField
import io.github.ppoonk.ac.ui.component.SegmentedButtonItem
import io.github.ppoonk.ac.utils.TimeUtils
import io.github.ppoonk.ac.utils.onlyNumber
import io.github.ppoonk.airgo_master.Res
import io.github.ppoonk.airgo_master.confirm
import io.github.ppoonk.airgo_master.repository.remote.model.Status
import io.github.ppoonk.airgo_master.search_history
import io.github.ppoonk.airgo_master.search_key
import io.github.ppoonk.airgo_master.status
import io.github.ppoonk.airgo_master.time_range
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalLayoutApi
@Composable
fun BaseSearchDrawer(
    vm: BaseSearchVM,
): Unit {

    val widget by vm.searchWidget.collectAsState()

    ACModalBottomSheet(
        modifier = Modifier,
        expanded = widget.bottomDrawerExpanded,
        onDismissRequest = { vm.closeSearchDrawer() },
        dragHandle = {
            ACDragHandle(
                end = {
                    TextButton(onClick = { vm.resetSearch() }) {
                        Text("重置")
                    }
                },
            )
        }
    ) {

        LazyColumn(
            modifier = Modifier.padding(16.dp).imePadding()
        ) {

            // 状态
            item {
                Text(stringResource(Res.string.status))
                ACSingleChoiceRow(
                    modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
                    selected = widget.status.ordinal,
                    list = Status.entries.map { status ->
                        SegmentedButtonItem(
                            label = status.i18n(),
                            onClick = { vm.refreshStatus(status) }
                        )
                    }
                )
            }

            // 时间范围
            item {
                Text(stringResource(Res.string.time_range))
                DateRange(
                    modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
                    date = Pair(widget.datePickerStart, widget.datePickerEnd),
                    onClick = { vm.refreshDatePickerExpanded() }
                )
            }

            // 搜索
            item {
                Text(stringResource(Res.string.search_key))
                ACTextField(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                    value = widget.search,
                    onValueChange = { vm.refreshSearch(it) },
                    keyboardOptions = if (widget.searchType == SearchType.ID) KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ) else KeyboardOptions.Default,
                    leadingIcon = {
                        Box {
                            Text(
                                text = widget.searchType.name,
                                modifier = Modifier.padding(horizontal = 16.dp)
                                    .clickable { vm.refreshSelectSearchType() })
                            DropdownMenu(
                                expanded = widget.searchTypeExpanded,
                                onDismissRequest = { vm.refreshSelectSearchType() },
                            ) {
                                SearchType.entries.forEach { t ->
                                    DropdownMenuItem(
                                        text = { Text(t.name) },
                                        onClick = { vm.refreshSearchType(t) }
                                    )
                                }
                            }
                        }
                    },
                    trailingIcon = {
                        ACClearVisibilityToggle(
                            isVisible = widget.clearInput,
                            onClick = { vm.refreshSearch("") })
                    },
                )
            }

            // 搜索历史
            item {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp),
                ) {
                    Text(
                        text = stringResource(Res.string.search_history),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    ACIconSmall(
                        ACIconDefault.Trash,
                        null,
                        modifier = Modifier.clickable {
                            vm.setSearchHistory(clearAll = true)
                            vm.getSearchHistory()
                        })
                }
            }

            item {
                FlowRow(
                    modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    widget.searchHistory.filter { h ->
                        h.searchType == widget.searchType.name
                    }.forEach { history ->
                        ACLabelInfo(history.search, modifier = Modifier.clickable {
                            vm.refreshSearch(history.search)
                        })
                    }
                }
            }

            // 确认按钮
            item {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 48.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Button(
                        onClick = { vm.onConfirm() },
                        modifier = Modifier.width(200.dp)
                    ) {
                        Text(stringResource(Res.string.confirm))
                    }
                }
            }
        }
    }
    // 日期选择对话框
    ACDateRangePickerDialog(
        expanded = widget.datePickerExpanded,
        onDateRangeSelected = { pair -> vm.refreshDate(pair) },
        onDismiss = { vm.refreshDatePickerExpanded() }
    )
}


class BaseSearchVM : ViewModel() {
    private val _searchWidget = MutableStateFlow(BaseSearchWidget())
    val searchWidget: StateFlow<BaseSearchWidget> = _searchWidget

    fun resetSearch(): Unit {
        _searchWidget.value = _searchWidget.value.copy(
            bottomDrawerExpanded = true,
            search = "",
            status = Status.ALL,
            datePickerStart = null,
            datePickerEnd = null,
        )
    }

    fun closeSearchDrawer(): Unit {
        _searchWidget.value =
            _searchWidget.value.copy(bottomDrawerExpanded = false)
    }

    fun openSearchDrawer(
        getSearchHistory: () -> List<SearchHistory> = { emptyList() },
        setSearchHistory: (SearchHistory?) -> Unit = {},
        onConfirm: (BaseSearchWidget) -> Unit = {}
    ): Unit {
        // 通过函数引用判断是否需要重置搜索参数
        if (_searchWidget.value.getSearchHistory != getSearchHistory) {
            resetSearch()
        }

        _searchWidget.value =
            _searchWidget.value.copy(
                bottomDrawerExpanded = true,
                getSearchHistory = getSearchHistory,
                setSearchHistory = setSearchHistory,
                onConfirm = onConfirm,
            )

    }
    fun getSearchHistory(): Unit {
        _searchWidget.value = _searchWidget.value.copy(
            searchHistory = _searchWidget.value.getSearchHistory()
        )
    }

    fun setSearchHistory(clearAll: Boolean = false): Unit {
        if (clearAll) {
            _searchWidget.value.setSearchHistory(null)
        } else {
            _searchWidget.value.setSearchHistory(
                SearchHistory(
                    _searchWidget.value.search,
                    _searchWidget.value.searchType.name
                )
            )
        }
    }

    fun onConfirm(): Unit {
        if (_searchWidget.value.search.isNotEmpty()) {
            setSearchHistory()
            getSearchHistory()
        }
        _searchWidget.value.onConfirm(_searchWidget.value)
        closeSearchDrawer()
    }

    fun refreshStatus(v: Status): Unit {
        _searchWidget.value =
            _searchWidget.value.copy(status = v)
    }

    fun refreshDatePickerExpanded(): Unit {
        _searchWidget.value =
            _searchWidget.value.copy(datePickerExpanded = !_searchWidget.value.datePickerExpanded)
    }

    fun refreshDate(pair: Pair<Long?, Long?>): Unit {
        if (pair.first != null && pair.second != null) {
            _searchWidget.value =
                _searchWidget.value.copy(
                    datePickerStart = TimeUtils.timestampToUTCNoOffset(pair.first!!),
                    datePickerEnd = TimeUtils.timestampToUTCNoOffset(pair.second!!),
                )
        }
        refreshDatePickerExpanded()
    }

    fun refreshSearch(v: String): Unit {
        val s = if (_searchWidget.value.searchType == SearchType.ID) v.onlyNumber() else v
        _searchWidget.value = _searchWidget.value.copy(
            search = s,
            clearInput = s.isNotEmpty()
        )
    }

    fun refreshSelectSearchType(): Unit {
        _searchWidget.value =
            _searchWidget.value.copy(searchTypeExpanded = !_searchWidget.value.searchTypeExpanded)
    }

    fun refreshSearchType(v: SearchType): Unit {
        _searchWidget.value = _searchWidget.value.copy(
            search = "",
            searchType = v,
            searchTypeExpanded = !_searchWidget.value.searchTypeExpanded
        )
    }

}


// 常用的搜索参数
data class BaseSearchWidget(
    val moreMenuExpanded: Boolean = false,

    val bottomDrawerExpanded: Boolean = false,

    val status: Status = Status.ALL,

    val datePickerExpanded: Boolean = false,
    val datePickerStart: Instant? = null,
    val datePickerEnd: Instant? = null,

    var searchTypeExpanded: Boolean = false,
    val search: String = "",
    val searchType: SearchType = SearchType.ID,
    val clearInput: Boolean = false,

    val onConfirm: (BaseSearchWidget) -> Unit = {},
    val searchHistory: List<SearchHistory> = emptyList(),
    val getSearchHistory: () -> List<SearchHistory> = { emptyList() },
    val setSearchHistory: (SearchHistory?) -> Unit = {},

    )

enum class SearchType {
    ID,
    NAME
}


@Serializable
data class SearchHistory(
    var search: String = "",
    var searchType: String = ""
)


