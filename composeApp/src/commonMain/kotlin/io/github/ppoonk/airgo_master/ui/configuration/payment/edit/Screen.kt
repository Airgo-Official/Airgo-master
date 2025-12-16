package io.github.ppoonk.airgo_master.ui.configuration.payment.edit

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.github.ppoonk.ac.ui.component.ACButtonError
import io.github.ppoonk.ac.ui.component.ACIconDefault
import io.github.ppoonk.ac.ui.component.ACIconSmall
import io.github.ppoonk.ac.ui.component.ACTextField
import io.github.ppoonk.ac.ui.component.ACTopAppBar
import io.github.ppoonk.ac.utils.onFailure
import io.github.ppoonk.ac.utils.onSuccess
import io.github.ppoonk.airgo_master.LocalNavController
import io.github.ppoonk.airgo_master.LocalSharedVM
import io.github.ppoonk.airgo_master.component.EditType
import io.github.ppoonk.airgo_master.repository.Repository
import io.github.ppoonk.airgo_master.repository.remote.model.PaymentType
import io.github.ppoonk.airgo_master.repository.remote.model.Status
import io.github.ppoonk.airgo_master.repository.remote.model.TronToken
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPaymentScreen() {
    val sharedVM = LocalSharedVM.current
    val vm = sharedVM.configurationVM
    val widget by sharedVM.configurationVM.paymentWidget.collectAsState()
    val scope = rememberCoroutineScope()
    val navController = LocalNavController.current

    Scaffold(
        topBar = {
            ACTopAppBar(
                title = {
                    Text(
                        when (widget.editType) {
                            EditType.CREATE -> "新建支付"
                            EditType.UPDATE -> "编辑支付"
                        }
                    )
                },
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
                    when (widget.editType) {
                        EditType.CREATE -> {
                            // 新建
                            IconButton(onClick = {
                                scope.launch {
                                    Repository.remote.updatePaymentList(vm.updatePaymentReq())
                                        .onFailure {
                                            sharedVM.dialogVM.openDialog(
                                                title = { Text(it.code.toString()) },
                                                text = { Text(it.message) }
                                            )
                                        }
                                        .onSuccess {
                                            navController.popBackStack()
                                        }
                                }
                            }) { ACIconSmall(ACIconDefault.Check, null) }
                        }

                        EditType.UPDATE -> {
                            // 更新
                            IconButton(onClick = {
                                scope.launch {
                                    Repository.remote.updatePaymentList(vm.updatePaymentReq())
                                        .onFailure {
                                            sharedVM.dialogVM.openDialog(
                                                title = { Text(it.code.toString()) },
                                                text = { Text(it.message) }
                                            )
                                        }
                                        .onSuccess {
                                            navController.popBackStack()
                                        }
                                }
                            }) { ACIconSmall(ACIconDefault.Check, null) }
                            // 删除
                            IconButton(onClick = {
                                sharedVM.dialogVM.openDialog(
                                    title = { Text("提示") },
                                    text = { Text("删除后数据无法恢复，确认删除吗?") }
                                ) {
                                    ACButtonError(
                                        onClick = {
                                            scope.launch {
                                                Repository.remote.updatePaymentList(vm.deletePaymentReq())
                                                    .onFailure {
                                                        sharedVM.dialogVM.openDialog(
                                                            title = { Text(it.code.toString()) },
                                                            text = { Text(it.message) }
                                                        )
                                                    }
                                                    .onSuccess {
                                                        sharedVM.dialogVM.closeDialog()
                                                        navController.popBackStack()
                                                    }
                                            }
                                        },
                                    ) {
                                        Text("确认")
                                    }
                                }
                            }) { ACIconSmall(ACIconDefault.Trash, null) }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->

        LazyColumn(modifier = Modifier.padding(paddingValues).imePadding().padding(horizontal = 16.dp)) {
            item {
                Text("名称")
                ACTextField(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                    value = widget.name,
                    isError = widget.nameError.isNotEmpty(),
                    supportingText = { Text(widget.nameError) },
                    onValueChange = { vm.refreshPaymentName(it) },
                )
            }
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp)
                ) {
                    Text("状态")
                    Switch(
                        checked = widget.status == Status.ENABLE,
                        onCheckedChange = { vm.refreshPaymentStatus(it) }
                    )
                }
            }
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp)
                ) {
                    Text("类型")
                    ExposedDropdownMenuBox(
                        expanded = widget.paymentTypeExpanded,
                        onExpandedChange = { vm.refreshPaymentTypeExpanded() },
                    ) {
                        ACTextField(
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            value = widget.paymentType.name,
                            leadingIcon = {
                                Image(
                                    widget.paymentType.getImage(), null,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                        )
                        ExposedDropdownMenu(
                            expanded = widget.paymentTypeExpanded,
                            onDismissRequest = { vm.refreshPaymentTypeExpanded() },
                        ) {
                            PaymentType.entries.forEach { p ->
                                DropdownMenuItem(
                                    text = { Text(p.name) },
                                    onClick = { vm.refreshPaymentType(p) }
                                )
                            }
                        }
                    }
                }
            }
            item {
                when (widget.paymentType) {
                    PaymentType.ALIPAY -> {
                        Text("appId")
                        ACTextField(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                            value = widget.alipayConfig.appId,
                            onValueChange = { vm.refreshAlipayConfig(widget.alipayConfig.copy(appId = it)) }
                        )
                        Text("appPrivateKey")
                        ACTextField(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                            value = widget.alipayConfig.appPrivateKey,
                            onValueChange = {
                                vm.refreshAlipayConfig(
                                    widget.alipayConfig.copy(
                                        appPrivateKey = it
                                    )
                                )
                            }
                        )
                        Text("alipayPublicCert")
                        ACTextField(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                            value = widget.alipayConfig.alipayPublicCert,
                            onValueChange = {
                                vm.refreshAlipayConfig(
                                    widget.alipayConfig.copy(
                                        alipayPublicCert = it
                                    )
                                )
                            }
                        )
                    }

                    PaymentType.EPAY -> {
                        Text("url")
                        ACTextField(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                            value = widget.epayConfig.url,
                            onValueChange = { vm.refreshEpayConfig(widget.epayConfig.copy(url = it)) }
                        )
                        Text("key")
                        ACTextField(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                            value = widget.epayConfig.key,
                            onValueChange = { vm.refreshEpayConfig(widget.epayConfig.copy(key = it)) }
                        )
                        Text("pid")
                        ACTextField(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                            value = widget.epayConfig.pid,
                            onValueChange = { vm.refreshEpayConfig(widget.epayConfig.copy(pid = it)) }
                        )
                    }

                    PaymentType.STRIPE -> {
                        Text("key")
                        ACTextField(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                            value = widget.stripeConfig.key,
                            onValueChange = { vm.refreshStripeConfig(widget.stripeConfig.copy(key = it)) }
                        )
                        Text("endpointSecret")
                        ACTextField(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                            value = widget.stripeConfig.endpointSecret,
                            onValueChange = {
                                vm.refreshStripeConfig(
                                    widget.stripeConfig.copy(
                                        endpointSecret = it
                                    )
                                )
                            }
                        )
                        Text("successURL")
                        ACTextField(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                            value = widget.stripeConfig.successURL,
                            onValueChange = { vm.refreshStripeConfig(widget.stripeConfig.copy(successURL = it)) }
                        )
                        Text("cancelURL")
                        ACTextField(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                            value = widget.stripeConfig.cancelURL,
                            onValueChange = { vm.refreshStripeConfig(widget.stripeConfig.copy(cancelURL = it)) }
                        )
                    }

                    PaymentType.TRON -> {
                        Text("apiKey")
                        ACTextField(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                            value = widget.tronConfig.apiKey,
                            onValueChange = { vm.refreshTronConfig(widget.tronConfig.copy(apiKey = it)) }
                        )
                        Text("address")
                        ACTextField(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                            value = widget.tronConfig.address,
                            onValueChange = { vm.refreshTronConfig(widget.tronConfig.copy(address = it)) }
                        )
                        Text("acceptTokens")
                        FlowRow(
                            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            TronToken.entries.forEach { t ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Checkbox(
                                        checked = widget.tronConfig.acceptTokens.contains(t.name),
                                        onCheckedChange = { checked ->
                                            vm.refreshTronToken(t, checked)
                                        }
                                    )
                                    Text(t.name)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
