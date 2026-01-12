package io.github.ppoonk.airgo_master.ui.node.node.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import io.github.ppoonk.ac.ui.component.ACIcon
import io.github.ppoonk.ac.ui.component.ACIconDefault
import io.github.ppoonk.ac.ui.component.ACIconSmall
import io.github.ppoonk.ac.ui.component.ACLabelInfo
import io.github.ppoonk.ac.ui.component.ACTopAppBar
import io.github.ppoonk.ac.ui.component.AutoSizeFade
import io.github.ppoonk.ac.utils.TimeUtils
import io.github.ppoonk.airgo_master.LocalNavController
import io.github.ppoonk.airgo_master.LocalSharedVM
import io.github.ppoonk.airgo_master.Res
import io.github.ppoonk.airgo_master.component.EditType
import io.github.ppoonk.airgo_master.navigation.toEditNode
import io.github.ppoonk.airgo_master.navigation.toEditProtocol
import io.github.ppoonk.airgo_master.node_config
import io.github.ppoonk.airgo_master.node_created_at
import io.github.ppoonk.airgo_master.node_detail
import io.github.ppoonk.airgo_master.node_id
import io.github.ppoonk.airgo_master.node_info
import io.github.ppoonk.airgo_master.node_name
import io.github.ppoonk.airgo_master.node_status
import io.github.ppoonk.airgo_master.protocol_list
import io.github.ppoonk.airgo_master.repository.remote.model.Node
import io.github.ppoonk.airgo_master.repository.remote.model.Protocol
import io.github.ppoonk.airgo_master.repository.remote.model.Status
import io.github.ppoonk.airgo_master.sharedViewModel.EditNodeWidget
import io.github.ppoonk.airgo_master.sharedViewModel.SharedVM
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@ExperimentalLayoutApi
fun NodeDetailsScreen() {
    val scope = rememberCoroutineScope()
    val sharedVM = LocalSharedVM.current
    val currentNodeProtocolList by sharedVM.nodeVM.currentNodeProtocolList.collectAsState()
    val currentNode by sharedVM.nodeVM.currentNode.collectAsState()
    val editNodeWidget by sharedVM.nodeVM.editNodeWidget.collectAsState()
    val navController = LocalNavController.current

    LaunchedEffect(Unit) {
        sharedVM.nodeVM.getCurrentNodeProtocolList()
    }

    Scaffold(
        topBar = {
            ACTopAppBar(
                title = { Text(stringResource(Res.string.node_detail)) },
                navigationIcon = {
                    IconButton(onClick = {
                        scope.launch {
                            navController.popBackStack()
                        }
                    }) {
                        ACIconSmall(ACIconDefault.AngleLeft, null)
                    }
                }
            )
        }
    ) { paddingValues ->

        AutoSizeFade(
            compact = {
                compact(
                    Modifier.padding(paddingValues).padding(horizontal = 16.dp),
                    navController,
                    sharedVM,
                    currentNode,
                    currentNodeProtocolList,
                    editNodeWidget
                )
            },
            medium = {
                compact(
                    Modifier.padding(paddingValues).padding(horizontal = 16.dp),
                    navController,
                    sharedVM,
                    currentNode,
                    currentNodeProtocolList,
                    editNodeWidget
                )
            },
            expanded = {
                expanded(
                    Modifier.padding(paddingValues).padding(horizontal = 16.dp),
                    navController,
                    sharedVM,
                    currentNode,
                    currentNodeProtocolList,
                    editNodeWidget
                )
            },
        )


    }
}

@Composable
private fun compact(
    modifier: Modifier,
    navController: NavHostController,
    sharedVM: SharedVM,
    currentNode: Node?,
    currentNodeProtocolList: List<Protocol>,
    editNodeWidget: EditNodeWidget
): Unit {
    LazyColumn(
        modifier = modifier
    ) {
        // 节点信息
        item {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 16.dp).fillMaxWidth()
            ) {
                Text(stringResource(Res.string.node_info))
                IconButton(
                    onClick = {
                        navController.toEditNode()
                    },
                ) {
                    ACIconSmall(ACIconDefault.Edit, null)
                }
            }
        }
        item {
            ElevatedCard(
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                currentNode?.let { n ->
                    Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stringResource(Res.string.node_id))
                            Text(n.id.toString())
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stringResource(Res.string.node_name))
                            Text(n.name)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stringResource(Res.string.node_status))
                            Text(Status.i18nByOrdinal(n.status))
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stringResource(Res.string.node_created_at))
                            Text(TimeUtils.toLocalDateString(n.createdAt))
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .clickable { sharedVM.nodeVM.editNodeWidgetShowConfig() },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stringResource(Res.string.node_config))
                            ACIcon(
                                if (editNodeWidget.showNodeConfig) ACIconDefault.AngleUp else ACIconDefault.AngleDown,
                                null
                            )
                        }
                        AnimatedVisibility(
                            editNodeWidget.showNodeConfig,
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            TextField(
                                value = n.config,
                                onValueChange = {},
                                readOnly = true,
                                singleLine = false
                            )
                        }
                    }
                }

            }
        }

        // 节点关联的协议
        item {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 16.dp).fillMaxWidth()
            ) {
                Text(stringResource(Res.string.protocol_list))
                IconButton(
                    onClick = {
                        sharedVM.nodeVM.initEditProtocol(EditType.CREATE)
                        navController.toEditProtocol()
                    },
                ) {
                    ACIconSmall(ACIconDefault.Plus, null)
                }
            }
        }
        item {
            currentNode?.let { node ->
                currentNodeProtocolList.forEach { p ->
                    Card(
                        modifier = Modifier.padding(bottom = 8.dp).clickable {
                            sharedVM.nodeVM.initEditProtocol(EditType.UPDATE, p)
                            navController.toEditProtocol()
                        }
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(16.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            )
                            {
                                ACLabelInfo(p.getProtocolType())
                                Text(p.address)
                            }
                        }
                    }
                }
            }
        }
    }

}

@Composable
private fun expanded(
    modifier: Modifier,
    navController: NavHostController,
    sharedVM: SharedVM,
    currentNode: Node?,
    currentNodeProtocolList: List<Protocol>,
    editNodeWidget: EditNodeWidget
): Unit {
    Row(modifier = modifier) {
        Column(modifier = Modifier.padding(end = 16.dp).weight(1f)) {
            // 节点信息
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 16.dp).fillMaxWidth()
            ) {
                Text(stringResource(Res.string.node_info))
                IconButton(
                    onClick = {
                        navController.toEditNode()
                    },
                ) {
                    ACIconSmall(ACIconDefault.Edit, null)
                }
            }
            ElevatedCard(
                modifier = Modifier.padding(bottom = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                currentNode?.let { n ->
                    Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stringResource(Res.string.node_id))
                            Text(n.id.toString())
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stringResource(Res.string.node_name))
                            Text(n.name)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stringResource(Res.string.node_status))
                            Text(Status.i18nByOrdinal(n.status))
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stringResource(Res.string.node_created_at))
                            Text(TimeUtils.toLocalDateString(n.createdAt))
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .clickable { sharedVM.nodeVM.editNodeWidgetShowConfig() },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stringResource(Res.string.node_config))
                            ACIcon(
                                if (editNodeWidget.showNodeConfig) ACIconDefault.AngleUp else ACIconDefault.AngleDown,
                                null
                            )
                        }
                        AnimatedVisibility(
                            editNodeWidget.showNodeConfig,
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            OutlinedTextField(
                                value = n.config,
                                onValueChange = {},
                                readOnly = true,
                                singleLine = false
                            )
                        }
                    }

                }
            }
        }
        LazyColumn(modifier = Modifier.weight(2f)) {
            // 节点关联的协议
            item {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 16.dp).fillMaxWidth()
                ) {
                    Text(stringResource(Res.string.protocol_list))
                    IconButton(
                        onClick = {
                            sharedVM.nodeVM.initEditProtocol(EditType.CREATE)
                            navController.toEditProtocol()
                        },
                    ) {
                        ACIconSmall(ACIconDefault.Plus, null)
                    }
                }
            }
            item {
                currentNode?.let { node ->
                    currentNodeProtocolList.forEach { p ->
                        Card(
                            modifier = Modifier.padding(bottom = 8.dp).clickable {
                                sharedVM.nodeVM.initEditProtocol(EditType.UPDATE, p)
                                navController.toEditProtocol()
                            }
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(16.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                {
                                    ACLabelInfo(p.getProtocolType())
                                    Text(p.address)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}


//AutoSizeFade(
//compact = {
//    compact(
//        modifier = Modifier.padding(paddingValues).padding(horizontal = 16.dp),
//    )
//},
//medium = {
//    compact(
//        modifier = Modifier.padding(paddingValues).padding(horizontal = 16.dp),
//    )
//},
//expanded = {
//    expanded(
//        modifier = Modifier.padding(paddingValues).padding(horizontal = 16.dp),
//    )
//},
//)



//@Composable
//private fun compact(modifier: Modifier): Unit {
//
//}
//
//@Composable
//private fun expanded(modifier: Modifier): Unit {
//
//}