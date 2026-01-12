package io.github.ppoonk.airgo_master.ui.configuration.payment.edit

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.material3.TextField
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
import io.github.ppoonk.airgo_master.Res
import io.github.ppoonk.airgo_master.configuration_payment_create
import io.github.ppoonk.airgo_master.configuration_payment_name
import io.github.ppoonk.airgo_master.configuration_payment_status
import io.github.ppoonk.airgo_master.configuration_payment_type
import io.github.ppoonk.airgo_master.configuration_payment_update
import io.github.ppoonk.airgo_master.confirm
import io.github.ppoonk.airgo_master.delete_confirmation
import io.github.ppoonk.airgo_master.node_id
import io.github.ppoonk.airgo_master.warning
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPaymentScreen() {
    val sharedVM = LocalSharedVM.current
    val vm = sharedVM.configurationVM
    val widget by sharedVM.configurationVM.editPaymentWidget.collectAsState()
    val scope = rememberCoroutineScope()
    val navController = LocalNavController.current

    Scaffold(
        topBar = {
            ACTopAppBar(
                title = {
                    Text(
                        stringResource(
                            when (widget.editType) {
                                EditType.CREATE -> Res.string.configuration_payment_create
                                EditType.UPDATE -> Res.string.configuration_payment_update
                            }
                        )
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
                                    Repository.remote.updatePaymentList(vm.toUpdatePaymentReq())
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
                                    Repository.remote.updatePaymentList(vm.toUpdatePaymentReq())
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
                                    title = { Text(stringResource(Res.string.warning)) },
                                    text = { Text(stringResource(Res.string.delete_confirmation)) }
                                ) {
                                    ACButtonError(
                                        onClick = {
                                            scope.launch {
                                                Repository.remote.updatePaymentList(vm.toDeletePaymentReq())
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
                                        Text(stringResource(Res.string.confirm))
                                    }
                                }
                            }) { ACIconSmall(ACIconDefault.Trash, null) }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier.padding(paddingValues).imePadding().padding(horizontal = 16.dp).widthIn(max = 600.dp)
        ) {
            item {
                Text(stringResource(Res.string.configuration_payment_name))
                TextField(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                    value = widget.name,
                    isError = widget.nameError.isNotEmpty(),
                    supportingText = { Text(widget.nameError) },
                    onValueChange = { vm.paymentName(it) },
                )
            }
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp)
                ) {
                    Text(stringResource(Res.string.configuration_payment_status))
                    Switch(
                        checked = widget.status == Status.ENABLE,
                        onCheckedChange = { vm.paymentStatus(it) }
                    )
                }
            }
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp)
                ) {
                    Text(stringResource(Res.string.configuration_payment_type))
                    ExposedDropdownMenuBox(
                        expanded = widget.paymentTypeExpanded,
                        onExpandedChange = { vm.paymentTypeExpanded() },
                    ) {
                        TextField(
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
                            onDismissRequest = { vm.paymentTypeExpanded() },
                        ) {
                            PaymentType.entries.forEach { p ->
                                DropdownMenuItem(
                                    text = { Text(p.name) },
                                    onClick = { vm.paymentType(p) }
                                )
                            }
                        }
                    }
                }
            }
            item {
                when (widget.paymentType) {
                    PaymentType.ALIPAY -> {
                        Text("app id")
                        TextField(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                            value = widget.alipayConfig.appId,
                            onValueChange = { vm.alipayConfig(widget.alipayConfig.copy(appId = it)) }
                        )
                        Text("app privateKey")
                        TextField(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                            value = widget.alipayConfig.appPrivateKey,
                            onValueChange = {
                                vm.alipayConfig(
                                    widget.alipayConfig.copy(
                                        appPrivateKey = it
                                    )
                                )
                            }
                        )
                        Text("alipay public cert")
                        TextField(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                            value = widget.alipayConfig.alipayPublicCert,
                            onValueChange = {
                                vm.alipayConfig(
                                    widget.alipayConfig.copy(
                                        alipayPublicCert = it
                                    )
                                )
                            }
                        )
                    }

                    PaymentType.EPAY -> {
                        Text("url")
                        TextField(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                            value = widget.epayConfig.url,
                            onValueChange = { vm.epayConfig(widget.epayConfig.copy(url = it)) }
                        )
                        Text("key")
                        TextField(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                            value = widget.epayConfig.key,
                            onValueChange = { vm.epayConfig(widget.epayConfig.copy(key = it)) }
                        )
                        Text("pid")
                        TextField(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                            value = widget.epayConfig.pid,
                            onValueChange = { vm.epayConfig(widget.epayConfig.copy(pid = it)) }
                        )
                    }

                    PaymentType.STRIPE -> {
                        Text("key")
                        TextField(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                            value = widget.stripeConfig.key,
                            onValueChange = { vm.stripeConfig(widget.stripeConfig.copy(key = it)) }
                        )
                        Text("endpoint secret")
                        TextField(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                            value = widget.stripeConfig.endpointSecret,
                            onValueChange = {
                                vm.stripeConfig(
                                    widget.stripeConfig.copy(
                                        endpointSecret = it
                                    )
                                )
                            }
                        )
                        Text("success URL")
                        TextField(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                            value = widget.stripeConfig.successURL,
                            onValueChange = { vm.stripeConfig(widget.stripeConfig.copy(successURL = it)) }
                        )
                        Text("cancel URL")
                        TextField(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                            value = widget.stripeConfig.cancelURL,
                            onValueChange = { vm.stripeConfig(widget.stripeConfig.copy(cancelURL = it)) }
                        )
                    }

                    PaymentType.TRON -> {
                        Text("api key") // TODO 添加提示
                        TextField(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                            value = widget.tronConfig.apiKey,
                            onValueChange = { vm.tronConfig(widget.tronConfig.copy(apiKey = it)) }
                        )
                        Text("address")
                        TextField(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                            value = widget.tronConfig.address,
                            onValueChange = { vm.tronConfig(widget.tronConfig.copy(address = it)) }
                        )
                        Text("accept tokens")
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
                                            vm.tronToken(t, checked)
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
