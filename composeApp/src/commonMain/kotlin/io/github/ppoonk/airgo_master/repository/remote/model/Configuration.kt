package io.github.ppoonk.airgo_master.repository.remote.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import io.github.ppoonk.airgo_master.Res
import io.github.ppoonk.airgo_master.logoAlipay
import io.github.ppoonk.airgo_master.logoEpay
import io.github.ppoonk.airgo_master.logoStripe
import io.github.ppoonk.airgo_master.logoTron
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource

@Serializable
data class Security(
    val adminPath: String,
    val tokenSign: String,
    val tokenDuration: Int  // 秒
)

@Serializable
data class UpdatePaymentListReq(
    val list: List<Payment>
)

@Serializable
data class GetPaymentListRes(
    val total: Int,
    val list: List<Payment>
)


@Serializable
data class Payment(
    val name: String,
    val status: Int,
    val paymentType: String,
    val alipayConfig: AlipayConfig?,
    val epayConfig: EpayConfig?,
    val stripeConfig: StripeConfig?,
    val tronConfig: TronConfig?,
)

@Serializable
data class AlipayConfig(
    val appId: String = "",
    val appPrivateKey: String = "",
    val alipayPublicCert: String = ""
) {
    val isValid: Boolean
        get() = appId.isNotEmpty() && appPrivateKey.isNotEmpty() && alipayPublicCert.isNotEmpty()
}

@Serializable
data class EpayConfig(
    val url: String = "",
    val key: String = "",
    val pid: String = ""
) {
    val isValid: Boolean
        get() = url.isNotEmpty() && key.isNotEmpty() && pid.isNotEmpty()
}

@Serializable
data class StripeConfig(
    val key: String = "",
    val endpointSecret: String = "",
    val successURL: String = "",
    val cancelURL: String = "",
) {
    val isValid: Boolean
        get() = key.isNotEmpty() && endpointSecret.isNotEmpty() && successURL.isNotEmpty() && cancelURL.isNotEmpty()
}

@Serializable
data class TronConfig(
    val address: String = "",
    val apiKey: String = "",
    val acceptTokens: List<String> = emptyList()
) {
    val isValid: Boolean
        get() = address.isNotEmpty() && apiKey.isNotEmpty() && acceptTokens.isNotEmpty()
}

enum class PaymentType {
    ALIPAY,
    EPAY,
    STRIPE,
    TRON;

    @Composable
    fun getImage(): Painter {
        return when (this) {
            ALIPAY -> painterResource(Res.drawable.logoAlipay)
            EPAY -> painterResource(Res.drawable.logoEpay)
            STRIPE -> painterResource(Res.drawable.logoStripe)
            TRON -> painterResource(Res.drawable.logoTron)
        }
    }
}

enum class TronToken {
    USDT,
    USDC
}

@Serializable
data class Push(
    val name: String,
    val status: Int,
    val pushType: String,
    val emailConfig: EmailConfig?,
    val tgBotConfig: TgBotConfig?
)

@Serializable
data class UpdatePushListReq(
    val list: List<Push>
)

@Serializable
data class GetPushListRes(
    val total: Int,
    val list: List<Push>
)

enum class PushType {
    TG_BOT,
    EMAIL,
}

@Serializable
data class EmailConfig(
    val host: String = "",
    val port: Int = 0,
    val username: String = "",
    val password: String = "",
)

@Serializable
data class TgBotConfig(
    val botToken: String = "",
    val proxyURL: String = "",
)
