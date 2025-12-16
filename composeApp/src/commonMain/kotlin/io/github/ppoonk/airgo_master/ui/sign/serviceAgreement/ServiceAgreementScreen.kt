package io.github.ppoonk.airgo_master.ui.sign.serviceAgreement


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ppoonk.ac.ui.component.ACIconDefault
import io.github.ppoonk.ac.ui.component.ACIconSmall
import io.github.ppoonk.ac.ui.component.ACTopAppBar
import io.github.ppoonk.airgo_master.LocalNavController
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceAgreementScreen(): Unit {
    val scope = rememberCoroutineScope()
    val navController = LocalNavController.current
    Scaffold(
        topBar = {
            ACTopAppBar(
                modifier = Modifier
                    .height(90.dp)
                    .padding(bottom = 5.dp),
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = {
                        scope.launch {
                            navController.popBackStack()
                        }
                    }) {
                        ACIconSmall(ACIconDefault.AngleLeft, null)
                    }
                },
            )
        }

    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 20.dp, vertical = 30.dp)
        ) {
            Text(
                "用户协议",
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(30.dp))
            Text("1. 点同意就是认爹")
            Text("2. 充钱你就是大爷")
        }
    }
}
