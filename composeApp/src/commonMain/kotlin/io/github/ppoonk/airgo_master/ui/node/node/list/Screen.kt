package io.github.ppoonk.airgo_master.ui.node.node.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
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
import io.github.ppoonk.ac.ui.component.ACLabelInfo
import io.github.ppoonk.ac.ui.component.ACLabelPrimary
import io.github.ppoonk.ac.ui.component.ACTopAppBar
import io.github.ppoonk.ac.ui.component.AutoSizeFade
import io.github.ppoonk.airgo_master.LocalDrawerState
import io.github.ppoonk.airgo_master.LocalNavController
import io.github.ppoonk.airgo_master.LocalSharedVM
import io.github.ppoonk.airgo_master.Res
import io.github.ppoonk.airgo_master.component.EditType
import io.github.ppoonk.airgo_master.component.EmptyPlaceholder
import io.github.ppoonk.airgo_master.component.rememberLazyListState
import io.github.ppoonk.airgo_master.disable
import io.github.ppoonk.airgo_master.enable
import io.github.ppoonk.airgo_master.navigation.toEditNode
import io.github.ppoonk.airgo_master.navigation.toNodeDetails
import io.github.ppoonk.airgo_master.repository.Repository
import io.github.ppoonk.airgo_master.repository.remote.model.Node
import io.github.ppoonk.airgo_master.repository.remote.model.Status
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ListNodeScreen() {
    val sharedVM = LocalSharedVM.current
    val list = sharedVM.nodeVM.nodeList.collectAsLazyPagingItems()
    val scope = rememberCoroutineScope()
    val navController = LocalNavController.current


    Scaffold(
        topBar = { ListNodeTopBar() },
    ) { paddingValues ->
        // 列表
        LazyColumn(
            state = list.rememberLazyListState(),
            modifier = Modifier.fillMaxWidth().padding(paddingValues).padding(horizontal = 16.dp)
                .imePadding(),
        ) {
            items(list.itemSnapshotList.items) { node ->
                NodeInfoProfile(
                    node = node,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp).fillMaxWidth()
                        .clickable {
                            scope.launch {
                                // TODO 有问题
                                sharedVM.nodeVM.initEditNode(EditType.UPDATE, node)
                                navController.toNodeDetails()
                            }
                        }
                )
            }

            when (list.loadState.refresh) {
                is LoadState.Loading -> item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is LoadState.Error -> {}
                else -> if (list.itemSnapshotList.isEmpty()) item { EmptyPlaceholder() }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListNodeTopBar(): Unit {
    val scope = rememberCoroutineScope()
    val navController = LocalNavController.current
    val drawerState = LocalDrawerState.current
    val sharedVM = LocalSharedVM.current

    ACTopAppBar(
        title = {},
        navigationIcon = {
            AutoSizeFade(compact = {
                IconButton(onClick = {
                    scope.launch { drawerState.open() }
                }) {
                    ACIconSmall(ACIconDefault.DrawerOpen, null)
                }
            })
        },
        actions = {
            // 搜索
            IconButton(onClick = {
                // 打开搜索抽屉，初始化搜索参数
                sharedVM.baseSearchVM.openSearchDrawer(
                    getSearchHistory = Repository.local::getNodeSearchHistory,
                    setSearchHistory = Repository.local::setNodeSearchHistory,
                    onConfirm = sharedVM.nodeVM::refreshUpdateNodeListReq
                )
                // 打开搜索抽屉后，获取搜索历史
                sharedVM.baseSearchVM.getSearchHistory()

            }) {
                ACIconSmall(ACIconDefault.Search, null)
            }
            // 新建
            IconButton(onClick = {
                scope.launch {
                    sharedVM.nodeVM.initEditNode(EditType.CREATE)
                    navController.toEditNode()
                }
            }) {
                ACIconSmall(ACIconDefault.Plus, null)
            }
        },
    )
}


@Composable
fun NodeInfoProfile(
    node: Node,
    modifier: Modifier = Modifier,
) {
    ACCard(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("ID: ${node.id}")
                Text(node.name)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (node.status == Status.ENABLE.ordinal) ACLabelPrimary(
                    // TODO 提取组件
                    stringResource(
                        Res.string.enable
                    ),
                ) else ACLabelInfo(
                    stringResource(Res.string.disable),
                )
            }
        }
    }
}


