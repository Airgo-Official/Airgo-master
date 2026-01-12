package io.github.ppoonk.airgo_master.ui.store.coupon.edit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.collectAsLazyPagingItems
import io.github.ppoonk.ac.ui.component.ACDragHandle
import io.github.ppoonk.ac.ui.component.ACIconDefault
import io.github.ppoonk.ac.ui.component.ACIconSmall
import io.github.ppoonk.ac.ui.component.ACModalBottomSheet
import io.github.ppoonk.ac.ui.component.ACTopAppBar
import io.github.ppoonk.ac.utils.onFailure
import io.github.ppoonk.ac.utils.onSuccess
import io.github.ppoonk.airgo_master.LocalNavController
import io.github.ppoonk.airgo_master.LocalSharedVM
import io.github.ppoonk.airgo_master.Res
import io.github.ppoonk.airgo_master.component.EditType
import io.github.ppoonk.airgo_master.coupon_associated_product
import io.github.ppoonk.airgo_master.coupon_code
import io.github.ppoonk.airgo_master.coupon_create
import io.github.ppoonk.airgo_master.coupon_discount
import io.github.ppoonk.airgo_master.coupon_min_order_amount
import io.github.ppoonk.airgo_master.coupon_name
import io.github.ppoonk.airgo_master.coupon_status
import io.github.ppoonk.airgo_master.coupon_type
import io.github.ppoonk.airgo_master.coupon_update
import io.github.ppoonk.airgo_master.repository.Repository
import io.github.ppoonk.airgo_master.repository.remote.model.CouponType
import io.github.ppoonk.airgo_master.repository.remote.model.Status
import io.github.ppoonk.airgo_master.reset
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCouponScreen(): Unit {
    val scope = rememberCoroutineScope()
    val navController = LocalNavController.current
    val sharedVM = LocalSharedVM.current
    val widget by sharedVM.storeVM.editCouponWidget.collectAsState()
    val productList = sharedVM.storeVM.productList.collectAsLazyPagingItems()
    val vm = sharedVM.storeVM

    Scaffold(
        topBar = {
            ACTopAppBar(
                title = {
                    Text(
                        stringResource(
                            when (widget.editType) {
                                EditType.CREATE -> Res.string.coupon_create
                                EditType.UPDATE -> Res.string.coupon_update
                            }
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        ACIconSmall(ACIconDefault.AngleLeft, null)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                when (widget.editType) {
                                    EditType.CREATE -> {
                                        Repository.remote.createCoupon(widget.toCreateCouponReq())
                                            .onFailure {
                                                sharedVM.dialogVM.openDialog(
                                                    title = { Text(it.code.toString()) },
                                                    text = { Text(it.message) }
                                                )
                                            }
                                            .onSuccess {
                                                //                                TODO 刷新节点数据
                                                navController.popBackStack()
                                            }
                                    }

                                    EditType.UPDATE -> {
                                        Repository.remote.updateCoupon(widget.toUpdateCouponReq())
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
                                }

                            }
                        },
                        enabled = widget.createIsValid
                    ) {
                        ACIconSmall(ACIconDefault.Check, null)
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(paddingValues).imePadding()
                .padding(horizontal = 16.dp).widthIn(max = 600.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
        ) {
            item {
                Text(stringResource(Res.string.coupon_name))
                TextField(
                    value = widget.name,
                    onValueChange = { vm.editCouponWidgetName(it) },
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp)
                )
            }
            item {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp)
                ) {
                    Text(stringResource(Res.string.coupon_status))
                    Switch(
                        checked = widget.status == Status.ENABLE,
                        onCheckedChange = { vm.editCouponWidgetStatus(it) },
                        modifier = Modifier.fillMaxHeight()
                    )
                }
            }
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp)
                ) {
                    Text(stringResource(Res.string.coupon_type))
                    ExposedDropdownMenuBox(
                        expanded = widget.expandCouponType,
                        onExpandedChange = { vm.editCouponWidgetExpandCouponType(it) },
                    ) {
                        TextField(
                            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                            value = widget.couponType.name,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ACIconSmall(ACIconDefault.Sort, null) }
                        )
                        ExposedDropdownMenu(
                            expanded = widget.expandCouponType,
                            onDismissRequest = { vm.editCouponWidgetExpandCouponType(false) },
                        ) {
                            CouponType.entries.forEach { t ->
                                DropdownMenuItem(
                                    text = { Text(t.name) },
                                    onClick = { vm.editCouponWidgetCouponType(t) }
                                )
                            }
                        }
                    }
                }
            }
            item {
                Text(stringResource(Res.string.coupon_code))
                TextField(
                    value = widget.couponCode,
                    onValueChange = { vm.editCouponWidgetCouponCode(it) },
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp)
                )
            }
            item {

                Text(stringResource(Res.string.coupon_discount))
                TextField(
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp),
                    value = widget.discount,
                    onValueChange = { vm.editCouponWidgetDiscount(it) },
                    isError = widget.discountError.isNotEmpty(),
                    supportingText = { Text(widget.discountError) }

                )
            }
            item {
                Text(stringResource(Res.string.coupon_min_order_amount))
                TextField(
                    value = widget.minOrderAmount,
                    onValueChange = { vm.editCouponWidgetMinOrderAmount(it) },
                    isError = widget.minOrderAmountError.isNotEmpty(),
                    supportingText = { Text(widget.minOrderAmountError) },
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp)
                )
            }
            item {
                TextField(
                    value = "${stringResource(Res.string.coupon_associated_product)}: ${widget.productIdList.size}",
                    onValueChange = {},
                    enabled = false,
                    trailingIcon = { ACIconSmall(ACIconDefault.Sort, null) },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp).clickable {
                        vm.editCouponWidgetExpandSelectProduct(true)
                    }
                )
            }
        }

        // 关联商品
        ACModalBottomSheet(
            expanded = widget.expandSelectProduct,
            onDismissRequest = { vm.editCouponWidgetExpandSelectProduct(false) },
            dragHandle = {
                ACDragHandle(
                    start = {
                        IconButton(onClick = { vm.editCouponWidgetExpandSelectProduct(false) }) {
                            ACIconSmall(ACIconDefault.AngleLeft, null)
                        }
                    },
                    end = {
                        TextButton(
                            onClick = { vm.editCouponWidgetClearCheckedProduct() },
                            modifier = Modifier.padding(end = 16.dp)
                        ) {
                            Text(stringResource(Res.string.reset))
                        }
                    },
                )
            },
            modifier = Modifier.fillMaxWidth().padding(WindowInsets.statusBars.asPaddingValues())
        ) {
            LazyColumn {
                productList.itemSnapshotList.items.forEach { p ->
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                                .padding(start = 16.dp, bottom = 16.dp)
                        ) {
                            Checkbox(
                                checked = widget.productIdList.contains(p.id),
                                onCheckedChange = { vm.editCouponWidgetCheckedProduct(p.id, it) }
                            )
                            Text(p.name)
                        }
                    }
                }
            }
        }

    }
}



