package io.github.ppoonk.airgo_master.ui.node.node.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ppoonk.ac.ui.component.ACButtonError
import io.github.ppoonk.ac.ui.component.ACCard
import io.github.ppoonk.ac.ui.component.ACIconDefault
import io.github.ppoonk.ac.ui.component.ACIconSmall
import io.github.ppoonk.ac.ui.component.ACLabelInfo
import io.github.ppoonk.ac.ui.component.ACTopAppBar
import io.github.ppoonk.ac.utils.onFailure
import io.github.ppoonk.ac.utils.onSuccess
import io.github.ppoonk.airgo_master.LocalNavController
import io.github.ppoonk.airgo_master.LocalSharedVM
import io.github.ppoonk.airgo_master.Res
import io.github.ppoonk.airgo_master.component.EditType
import io.github.ppoonk.airgo_master.confirm
import io.github.ppoonk.airgo_master.delete_confirmation
import io.github.ppoonk.airgo_master.navigation.toEditNode
import io.github.ppoonk.airgo_master.navigation.toEditProtocol
import io.github.ppoonk.airgo_master.node_details
import io.github.ppoonk.airgo_master.protocol_list
import io.github.ppoonk.airgo_master.repository.Repository
import io.github.ppoonk.airgo_master.repository.remote.model.DeleteNodeReq
import io.github.ppoonk.airgo_master.ui.node.node.list.NodeInfoProfile
import io.github.ppoonk.airgo_master.warning
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
    val navController = LocalNavController.current

    LaunchedEffect(Unit) {
        sharedVM.nodeVM.getCurrentNodeProtocolList()
    }

    Scaffold(
        topBar = {
            ACTopAppBar(
                title = { Text(stringResource(Res.string.node_details)) },
                navigationIcon = {
                    IconButton(onClick = {
                        scope.launch {
                            navController.popBackStack()
                        }
                    }) {
                        ACIconSmall(ACIconDefault.AngleLeft, null)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.toEditNode()
                    }) {
                        ACIconSmall(ACIconDefault.Edit, null)
                    }
                    IconButton(onClick = {
                        sharedVM.dialogVM.openDialog(
                            title = { Text(stringResource(Res.string.warning)) },
                            text = { Text(stringResource(Res.string.delete_confirmation)) },
                        ) {
                            ACButtonError(onClick = {
                                scope.launch {
                                    Repository.remote.deleteNode(DeleteNodeReq(id = currentNode!!.id))
                                        .onFailure {
                                            sharedVM.dialogVM.openDialog(
                                                title = { Text(it.code.toString()) },
                                                text = { Text(it.message) },
                                            )
                                        }
                                        .onSuccess {
                                            sharedVM.dialogVM.closeDialog()
                                            navController.popBackStack()
                                        }
                                }
                            }) {
                                Text(stringResource(Res.string.confirm))
                            }
                        }
                    }) {
                        ACIconSmall(ACIconDefault.Trash, null)
                    }
                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier.padding(it).padding(horizontal = 16.dp)
        ) {
            // 节点简介
            stickyHeader {
                currentNode?.let { node ->
                    NodeInfoProfile(
                        node = node,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(Modifier.height(16.dp))
            }
            // 节点关联的协议
            item {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
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
                Spacer(Modifier.height(16.dp))
            }
            item {
                currentNode?.let { node ->
                    currentNodeProtocolList.forEach { p ->
                        ACCard(
                            modifier = Modifier.fillMaxWidth().clickable {
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
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}