package io.github.ppoonk.airgo_master.ui.configuration.payment.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ppoonk.ac.ui.component.ACCard
import io.github.ppoonk.ac.ui.component.ACIconDefault
import io.github.ppoonk.ac.ui.component.ACIconSmall
import io.github.ppoonk.ac.ui.component.ACTopAppBar
import io.github.ppoonk.airgo_master.LocalNavController
import io.github.ppoonk.airgo_master.LocalSharedVM
import io.github.ppoonk.airgo_master.component.EditType
import io.github.ppoonk.airgo_master.component.EmptyPlaceholder
import io.github.ppoonk.airgo_master.navigation.toEditPayment
import io.github.ppoonk.airgo_master.repository.remote.model.Status
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen() {
    val sharedVM = LocalSharedVM.current
    val list by sharedVM.configurationVM.paymentList.collectAsState()
    val navController = LocalNavController.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        sharedVM.configurationVM.getPaymentList()
    }
    Scaffold(
        topBar = {
            ACTopAppBar(
                title = { },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            sharedVM.configurationVM.initEditPayment(EditType.CREATE)
                            navController.toEditPayment()
                        }
                    }) {
                        ACIconSmall(ACIconDefault.Plus, null)
                    }
                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(it).padding(horizontal = 16.dp).imePadding(),
        ) {
            if (list.isEmpty()) {
                item {
                    EmptyPlaceholder()
                }
            } else {
                items(list) { p ->
                    ACCard(
                        enabled = p.status == Status.ENABLE.ordinal,
                        modifier = Modifier.fillMaxWidth()
                            .clickable {
                                sharedVM.configurationVM.initEditPayment(EditType.UPDATE, p)
                                navController.toEditPayment()
                            },
                    ) {
                        Column(
                            Modifier.fillMaxWidth().padding(16.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(p.paymentType)
                                Text(p.name)
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}
