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
import androidx.compose.material3.TextButton
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
import io.github.ppoonk.ac.ui.component.ACTextField
import io.github.ppoonk.ac.ui.component.ACTopAppBar
import io.github.ppoonk.ac.utils.onFailure
import io.github.ppoonk.ac.utils.onSuccess
import io.github.ppoonk.airgo_master.LocalNavController
import io.github.ppoonk.airgo_master.LocalSharedVM
import io.github.ppoonk.airgo_master.component.EditType
import io.github.ppoonk.airgo_master.repository.Repository
import io.github.ppoonk.airgo_master.repository.remote.model.CouponType
import io.github.ppoonk.airgo_master.repository.remote.model.Status
import kotlinx.coroutines.launch

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
                        when (widget.editType) {
                            EditType.CREATE -> "创建优惠券"
                            EditType.UPDATE -> "编辑优惠券"
                        }
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
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
        ) {
            item {
                Text("优惠券类型")
                ExposedDropdownMenuBox(
                    expanded = widget.expandCouponType,
                    onExpandedChange = { vm.expandCouponType(it) },
                ) {
                    ACTextField(
                        modifier = Modifier.fillParentMaxWidth()
                            .padding(top = 8.dp, bottom = 16.dp)
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        value = widget.couponType.name,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ACIconSmall(ACIconDefault.Sort, null) }
                    )
                    ExposedDropdownMenu(
                        expanded = widget.expandCouponType,
                        onDismissRequest = { vm.expandCouponType(false) },
                    ) {
                        CouponType.entries.forEach { t ->
                            DropdownMenuItem(
                                text = { Text(t.name) },
                                onClick = { vm.couponType(t) }
                            )
                        }
                    }
                }
            }

            item {
                Text("商品名称")
                ACTextField(
                    placeholder = { Text("商品名称") },
                    value = widget.name,
                    onValueChange = { vm.couponName(it) },
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
                    Text("状态")
                    Switch(
                        checked = widget.status == Status.ENABLE,
                        onCheckedChange = { vm.couponStatus(it) },
                        modifier = Modifier.fillMaxHeight()
                    )
                }
            }
            item {
                Text("优惠码")
                ACTextField(
                    value = widget.couponCode,
                    onValueChange = { vm.couponCode(it) },
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp)
                )
            }
            item {

                Text("优惠值")
                ACTextField(
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp),
                    value = widget.discount,
                    onValueChange = { vm.couponDiscount(it) },
                    isError = widget.discountError.isNotEmpty(),
                    supportingText = { Text(widget.discountError) }

                )
            }
            item {
                Text("订单最低阈值")
                ACTextField(
                    value = widget.minOrderAmount,
                    onValueChange = { vm.couponMinOrderAmount(it) },
                    isError = widget.minOrderAmountError.isNotEmpty(),
                    supportingText = { Text(widget.minOrderAmountError) },
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp)
                )
            }
            item {
                ACTextField(
                    value = "绑定商品数量：${widget.productIdList.size}",
                    onValueChange = {},
                    enabled = false,
                    trailingIcon = { ACIconSmall(ACIconDefault.Sort, null) },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp).clickable{
                        vm.couponExpandSelectProduct(true)
                    }
                )
            }
        }

        // 关联商品
        ACModalBottomSheet(
            expanded = widget.expandSelectProduct,
            onDismissRequest = { vm.couponExpandSelectProduct(false) },
            dragHandle = {
                ACDragHandle(
                    start = {
                        IconButton(onClick = { vm.couponExpandSelectProduct(false) }) {
                            ACIconSmall(ACIconDefault.AngleLeft, null)
                        }
                    },
                    end = {
                        TextButton(
                            onClick = { vm.couponClearCheckedProductId() },
                            modifier = Modifier.padding(end = 16.dp)
                        ) {
                            Text("重置")
                        }
                    },
                )
            },
            modifier = Modifier.fillMaxWidth().padding(WindowInsets.statusBars.asPaddingValues())
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                productList.itemSnapshotList.items.forEach { p ->
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                                .padding(start = 16.dp, bottom = 16.dp)
                        ) {
                            Checkbox(
                                checked = widget.productIdList.contains(p.id),
                                onCheckedChange = { vm.couponCheckedProductId(p.id, it) }
                            )
                            Text(p.name)
                        }
                    }
                }
            }
        }

    }
}



