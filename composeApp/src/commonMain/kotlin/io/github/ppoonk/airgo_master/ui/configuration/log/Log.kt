package io.github.ppoonk.airgo_master.ui.configuration.log

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ppoonk.ac.ui.component.ACSingleChoiceRow
import io.github.ppoonk.ac.ui.component.ACTextHighlight
import io.github.ppoonk.ac.ui.component.KeywordColor
import io.github.ppoonk.ac.ui.component.SegmentedButtonItem
import io.github.ppoonk.ac.utils.Logger
import io.github.ppoonk.airgo_master.LocalSharedVM
import io.github.ppoonk.airgo_master.Res
import io.github.ppoonk.airgo_master.component.EmptyPlaceholder
import io.github.ppoonk.airgo_master.configuration_log_clear
import io.github.ppoonk.airgo_master.sharedViewModel.LogStatus
import org.jetbrains.compose.resources.stringResource


@Composable
fun LogScreen() {

    val sharedVM = LocalSharedVM.current
    val logList by sharedVM.configurationVM.logList.collectAsState()
    val displayLog by sharedVM.configurationVM.logStatus.collectAsState()
    val listState = rememberLazyListState()
    val keywordsColorList = listOf<KeywordColor>(
        KeywordColor("Debug", MaterialTheme.colorScheme.primary),
        KeywordColor("Error", MaterialTheme.colorScheme.error),
    )

    LaunchedEffect(logList) {
        if (logList.isNotEmpty()) {
            // 当items变化时，滚动到最后一项
            listState.animateScrollToItem(logList.size - 1)
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {
        stickyHeader {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth().background(
                        MaterialTheme.colorScheme.background,
                    ).padding(vertical = 16.dp)
            ) {
                ACSingleChoiceRow(
                    selected = displayLog.ordinal,
                    modifier = Modifier.width(200.dp),
                    list = LogStatus.entries.map { item ->
                        SegmentedButtonItem(
                            label = item.i18n(),
                            onClick = {
                                sharedVM.configurationVM.logStatus(item)
                                Logger.displayLog(item == LogStatus.RECORDING) { s ->
                                    sharedVM.configurationVM.addLog(s)
                                }
                            }
                        )
                    },
                )

                TextButton(
                    onClick = {
                        sharedVM.configurationVM.clearLog()
                    }
                ) {
                    Text(stringResource(Res.string.configuration_log_clear))
                }

            }
            Spacer(Modifier.height(16.dp))
        }

        if (logList.isEmpty()) {
            item {
                EmptyPlaceholder()
            }
        } else {
            items(logList) {
                Column(
                    modifier = Modifier.fillParentMaxWidth().padding(horizontal = 16.dp)
                ) {
                    ACTextHighlight(
                        it,
                        keywordsColorList,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(8.dp))
                }

            }
        }
    }
}





