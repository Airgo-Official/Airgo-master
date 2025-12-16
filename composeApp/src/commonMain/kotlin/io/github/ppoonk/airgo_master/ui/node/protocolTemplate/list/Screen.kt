package io.github.ppoonk.airgo_master.ui.node.protocolTemplate.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import app.cash.paging.compose.collectAsLazyPagingItems
import io.github.ppoonk.ac.ui.component.ACCard
import io.github.ppoonk.ac.ui.component.ACIconDefault
import io.github.ppoonk.ac.ui.component.ACIconSmall
import io.github.ppoonk.ac.ui.component.ACTopAppBar
import io.github.ppoonk.airgo_master.LocalNavController
import io.github.ppoonk.airgo_master.LocalSharedVM
import io.github.ppoonk.airgo_master.component.EditType
import io.github.ppoonk.airgo_master.component.EmptyPlaceholder
import io.github.ppoonk.airgo_master.component.rememberLazyListState
import io.github.ppoonk.airgo_master.navigation.toEditProtocolTemplate
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProtocolTemplateScreen() {
    val scope = rememberCoroutineScope()
    val sharedVM = LocalSharedVM.current
    val list = sharedVM.nodeVM.protocolTemplateList.collectAsLazyPagingItems()
    val navController = LocalNavController.current


    Scaffold(
        topBar = {
            ACTopAppBar(
                title = { },
                navigationIcon = {},
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            sharedVM.nodeVM.initEditProtocolTemplate(EditType.UPDATE)
                            navController.toEditProtocolTemplate()
                        }
                    }) { ACIconSmall(ACIconDefault.Plus, null) }
                }
            )
        }
    ) {

        LazyColumn(
            state = list.rememberLazyListState(),
            modifier = Modifier.fillMaxWidth().padding(it).padding(horizontal = 16.dp).imePadding(),
        ) {
            when {
                list.loadState.refresh is LoadState.Error -> {
                    item {
                        EmptyPlaceholder((list.loadState.refresh as LoadState.Error).error.message.toString())
                    }
                }

                list.loadState.append is LoadState.Error -> {
                    item {
                        EmptyPlaceholder((list.loadState.append as LoadState.Error).error.message.toString())
                    }
                }

                else -> {
                    if (list.itemSnapshotList.isEmpty()) {
                        item {
                            EmptyPlaceholder()
                        }
                    } else {
                        items(list.itemSnapshotList.items) { pt ->
                            ACCard(
                                modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth()
                                    .clickable {
                                        sharedVM.nodeVM.initEditProtocolTemplate(EditType.CREATE)
                                        navController.toEditProtocolTemplate()
                                        //                              TODO  list.refresh()
                                    }) {
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("ID: ${pt.id}")
                                        Text(pt.name)

                                    }
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(pt.getProtocolType().ifEmpty { "未知协议" })
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
