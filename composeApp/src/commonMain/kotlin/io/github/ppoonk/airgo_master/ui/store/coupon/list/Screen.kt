package io.github.ppoonk.airgo_master.ui.store.coupon.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import app.cash.paging.compose.collectAsLazyPagingItems
import io.github.ppoonk.ac.ui.component.ACCard
import io.github.ppoonk.ac.ui.component.ACIconDefault
import io.github.ppoonk.ac.ui.component.ACIconSmall
import io.github.ppoonk.ac.ui.component.ACTopAppBar
import io.github.ppoonk.airgo_master.LocalNavController
import io.github.ppoonk.airgo_master.LocalSharedVM
import io.github.ppoonk.airgo_master.component.EditType
import io.github.ppoonk.airgo_master.component.rememberLazyListState
import io.github.ppoonk.airgo_master.navigation.toEditCoupon
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CouponScreen() {
    val scope = rememberCoroutineScope()
    val navController = LocalNavController.current
    val sharedVM = LocalSharedVM.current
    val couponList = sharedVM.storeVM.couponList.collectAsLazyPagingItems()


    Scaffold(
        topBar = {
            ACTopAppBar(
                title = { },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            // TODO EditType.CREATE
                            navController.toEditCoupon()
                        }
                    }) { ACIconSmall(ACIconDefault.Plus, null) }
                }
            )
        }
    ) {
        LazyColumn(
            state = couponList.rememberLazyListState(),
            modifier = Modifier.fillMaxSize().padding(it).padding(horizontal = 16.dp).imePadding()
        ) {
            items(couponList.itemSnapshotList.items) { coupon ->
                ACCard(
                    modifier = Modifier.fillMaxWidth().clickable {
                        sharedVM.storeVM.initEditCoupon(EditType.UPDATE, coupon)
                        navController.toEditCoupon()
                    },
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = coupon.id.toString(),
                            )
                            Text(
                                text = coupon.name,
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "${coupon.couponType}:${coupon.couponCode}",
                            )
                            Text(
                                text = coupon.discount.toString(),
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }

    }
}

