package io.github.ppoonk.airgo_master.sharedViewModel

import androidx.lifecycle.ViewModel
import io.github.ppoonk.ac.utils.ValidationResult
import io.github.ppoonk.ac.utils.ValidationUtils
import io.github.ppoonk.ac.utils.onFailure
import io.github.ppoonk.ac.utils.onSuccess
import io.github.ppoonk.airgo_master.component.EditType
import io.github.ppoonk.airgo_master.repository.Repository
import io.github.ppoonk.airgo_master.repository.remote.model.AlipayConfig
import io.github.ppoonk.airgo_master.repository.remote.model.EmailConfig
import io.github.ppoonk.airgo_master.repository.remote.model.EpayConfig
import io.github.ppoonk.airgo_master.repository.remote.model.Payment
import io.github.ppoonk.airgo_master.repository.remote.model.PaymentType
import io.github.ppoonk.airgo_master.repository.remote.model.Push
import io.github.ppoonk.airgo_master.repository.remote.model.PushType
import io.github.ppoonk.airgo_master.repository.remote.model.Security
import io.github.ppoonk.airgo_master.repository.remote.model.Status
import io.github.ppoonk.airgo_master.repository.remote.model.StripeConfig
import io.github.ppoonk.airgo_master.repository.remote.model.TgBotConfig
import io.github.ppoonk.airgo_master.repository.remote.model.TronConfig
import io.github.ppoonk.airgo_master.repository.remote.model.TronToken
import io.github.ppoonk.airgo_master.repository.remote.model.UpdatePaymentListReq
import io.github.ppoonk.airgo_master.repository.remote.model.UpdatePushListReq
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class ConfigurationVM : ViewModel() {
    private val _currentPayment = MutableStateFlow<Payment?>(null)

    private val _paymentList = MutableStateFlow<List<Payment>>(emptyList())
    val paymentList: StateFlow<List<Payment>> = _paymentList
    suspend fun getPaymentList(): Unit {
        Repository.remote.getPaymentList()
            .onSuccess { r ->
                r.data?.list?.let {
                    _paymentList.value = it
                }
            }
    }

    private val _paymentWidget = MutableStateFlow(PaymentWidget())
    val paymentWidget: StateFlow<PaymentWidget> = _paymentWidget


    fun initEditPayment(editType: EditType, current: Payment? = null): Unit {
        when (editType) {
            EditType.CREATE -> _paymentWidget.value = PaymentWidget()

            EditType.UPDATE -> {

                current?.let { p ->
                    _currentPayment.value = current
                    _paymentWidget.value = PaymentWidget(
                        name = p.name,
                        status = Status.entries[p.status],
                        paymentType = PaymentType.valueOf(p.paymentType.uppercase()),
                        alipayConfig = p.alipayConfig ?: AlipayConfig(),
                        epayConfig = p.epayConfig ?: EpayConfig(),
                        stripeConfig = p.stripeConfig ?: StripeConfig(),
                        tronConfig = p.tronConfig ?: TronConfig(),
                        editType = editType,
                        paymentList = _paymentList.value.filter { it.name != p.name }  // 先移除当前支付，点击更新按钮时再添加到列表
                    )
                }
            }
        }
    }


    fun refreshPaymentName(v: String): Unit {
        // TODO 判空
        val isExist = when (_paymentWidget.value.editType) {
            EditType.CREATE -> _paymentList.value.any { it.name == v }
            EditType.UPDATE -> _paymentWidget.value.paymentList.any { it.name == v }
        }
        val nameError = if (isExist) "该名称已存在" else ""
        _paymentWidget.value = _paymentWidget.value.copy(name = v, nameError = nameError)
    }

    fun refreshPaymentStatus(v: Boolean): Unit {
        _paymentWidget.value =
            _paymentWidget.value.copy(status = if (v) Status.ENABLE else Status.DISABLE)
    }

    fun refreshPaymentTypeExpanded(): Unit {
        _paymentWidget.value =
            _paymentWidget.value.copy(paymentTypeExpanded = !_paymentWidget.value.paymentTypeExpanded)
    }

    fun refreshPaymentType(v: PaymentType): Unit {
        _paymentWidget.value =
            _paymentWidget.value.copy(paymentType = v, paymentTypeExpanded = false)
    }

    fun refreshAlipayConfig(v: AlipayConfig): Unit {
        _paymentWidget.value = _paymentWidget.value.copy(alipayConfig = v)
    }

    fun refreshEpayConfig(v: EpayConfig): Unit {
        _paymentWidget.value = _paymentWidget.value.copy(epayConfig = v)
    }

    fun refreshStripeConfig(v: StripeConfig): Unit {
        _paymentWidget.value = _paymentWidget.value.copy(stripeConfig = v)
    }

    fun refreshTronConfig(v: TronConfig): Unit {
        _paymentWidget.value = _paymentWidget.value.copy(tronConfig = v)
    }

    fun refreshTronToken(v: TronToken, isAdd: Boolean): Unit {
        val tokens = _paymentWidget.value.tronConfig.acceptTokens.toMutableList()
        if (isAdd) {
            tokens.add(v.name)
        } else {
            tokens.remove(v.name)
        }
        _paymentWidget.value = _paymentWidget.value.copy(
            tronConfig = _paymentWidget.value.tronConfig.copy(acceptTokens = tokens.toList())
        )
    }


    fun updatePaymentReq(): UpdatePaymentListReq {
        val updatedList = when (_paymentWidget.value.editType) {
            EditType.CREATE -> _paymentList.value.plus(_paymentWidget.value.toPayment())

            EditType.UPDATE -> _paymentWidget.value.paymentList.plus(_paymentWidget.value.toPayment())
        }
        return UpdatePaymentListReq(updatedList)
    }

    fun deletePaymentReq(): UpdatePaymentListReq {
        return UpdatePaymentListReq(_paymentWidget.value.paymentList)
    }


    suspend fun getSecurity(): Unit {
        Repository.remote.getSecurity()
            .onFailure { }
            .onSuccess { r ->
                r.data?.let {
                    _securityWidget.value = _securityWidget.value.copy(
                        adminPath = it.adminPath,
                        tokenSign = it.tokenSign,
                        tokenDuration = it.tokenDuration.toString()
                    )
                }
            }
    }

    private val _securityWidget = MutableStateFlow<SecurityWidget>(SecurityWidget())
    val securityWidget: StateFlow<SecurityWidget> = _securityWidget


    fun localApiUrl(v: String): Unit {
        _securityWidget.value = _securityWidget.value.copy(localApiUrl = v)
        Repository.local.setBaseUrl(v)
    }

    fun localAdminPath(v: String): Unit {
        _securityWidget.value = _securityWidget.value.copy(localAdminPath = v)
        Repository.local.setAdminPath(v)
    }

    fun refreshAdminPath(v: String): Unit {
        val err = when (val r = ValidationUtils.validateEmpty(v)) {
            is ValidationResult.Failure -> r.error
            is ValidationResult.Success -> ""
        }
        _securityWidget.value = _securityWidget.value.copy(
            adminPath = v,
            adminPathError = err
        )
    }

    fun refreshTokenSign(v: String): Unit {
        val err = when (val r = ValidationUtils.validateEmpty(v)) {
            is ValidationResult.Failure -> r.error
            is ValidationResult.Success -> ""
        }

        _securityWidget.value = _securityWidget.value.copy(
            tokenSign = v,
            tokenSignError = err
        )
    }

    fun refreshTokenDuration(v: String): Unit {
        val err =
            when (val r = ValidationUtils.validateNumber(v, 60..3600 * 720 * 365)) { // 60秒 ～ 1年
                is ValidationResult.Failure -> r.error
                is ValidationResult.Success -> ""
            }
        _securityWidget.value = _securityWidget.value.copy(
            tokenDuration = v,
            tokenDurationError = err
        )
    }


    fun initEditPush(editType: EditType, p: Push? = null): Unit {
        when (editType) {
            EditType.CREATE -> _pushWidget.value = PushWidget()

            EditType.UPDATE -> {
                _currentPush.value = p
                p?.let { p ->
                    _pushWidget.value = PushWidget(
                        name = p.name,
                        status = Status.entries[p.status],
                        pushType = PushType.valueOf(p.pushType),
                        emailConfig = p.emailConfig ?: EmailConfig(),
                        tgBotConfig = p.tgBotConfig ?: TgBotConfig(),
                        editType = editType,
                        pushList = _pushList.value.filter { it.name != p.name }
                    )
                }
            }
        }
    }

    private val _currentPush = MutableStateFlow<Push?>(null)

    private val _pushList = MutableStateFlow<List<Push>>(emptyList())
    val pushList: StateFlow<List<Push>> = _pushList
    suspend fun getPushList(): Unit {
        Repository.remote.getPushList()
            .onFailure { }
            .onSuccess { r ->
                r.data?.list?.let {
                    _pushList.value = it
                }
            }
    }

    private val _pushWidget = MutableStateFlow<PushWidget>(PushWidget())
    val pushWidget: StateFlow<PushWidget> = _pushWidget

    fun refreshPushName(v: String): Unit {
        // TODO 判空
        // 重复
        _pushWidget.value = _pushWidget.value.copy(name = v)
    }

    fun refreshPushStatus(v: Boolean): Unit {
        _pushWidget.value =
            _pushWidget.value.copy(status = if (v) Status.ENABLE else Status.DISABLE)
    }

    fun refreshPushTypeExpanded(): Unit {
        _pushWidget.value =
            _pushWidget.value.copy(pushTypeExpanded = !_pushWidget.value.pushTypeExpanded)
    }

    fun refreshPushType(v: PushType): Unit {
        _pushWidget.value =
            _pushWidget.value.copy(pushType = v, pushTypeExpanded = false)
    }

    fun refreshTgBotConfig(v: TgBotConfig): Unit {
        _pushWidget.value = _pushWidget.value.copy(tgBotConfig = v)
    }

    fun refreshEmailConfig(v: EmailConfig): Unit {
        _pushWidget.value = _pushWidget.value.copy(emailConfig = v)
    }

    fun updatePushReq(): UpdatePushListReq {
        val updatedList = when (_pushWidget.value.editType) {
            EditType.CREATE -> _pushList.value.plus(_pushWidget.value.toPush())

            EditType.UPDATE -> _pushWidget.value.pushList.plus(_pushWidget.value.toPush())
        }
        return UpdatePushListReq(updatedList)
    }

    fun deletePushReq(): UpdatePushListReq {
        return UpdatePushListReq(_pushWidget.value.pushList)
    }


    //日志
    private val _logList = MutableStateFlow<List<String>>(emptyList())
    val logList: StateFlow<List<String>> = _logList

    private val _displayLog = MutableStateFlow<Boolean>(false)
    val displayLog: StateFlow<Boolean> = _displayLog

    fun displayLog(v: Boolean): Unit {
        _displayLog.value = v
    }

    fun addLog(log: String): Unit {
        _logList.update {
            (it + log).takeLast(1000) // 保持最新1000条
        }
    }

    fun clearLog(): Unit {
        _logList.value = emptyList()
    }


}

data class SecurityWidget(
    // 登录时设置的
    val localApiUrl: String = Repository.local.getBaseUrl() ?: "",
    val localAdminPath: String = Repository.local.getAdminPath() ?: "",

    val adminPath: String = "",
    val tokenSign: String = "",
    val tokenDuration: String = "",

    val adminPathError: String = "",
    val tokenSignError: String = "",
    val tokenDurationError: String = ""
) {
    val isUpdateValid: Boolean
        get() = adminPathError.isEmpty() &&
                tokenSignError.isEmpty() &&
                tokenDurationError.isEmpty()

    fun toSecurity(): Security {
        return Security(
            adminPath = this.adminPath,
            tokenSign = this.tokenSign,
            tokenDuration = this.tokenDuration.toInt()
        )
    }
}


data class PaymentWidget(
    val id: UInt = 0u,
    val name: String = "",
    val status: Status = Status.ENABLE,
    val paymentType: PaymentType = PaymentType.ALIPAY,
    val alipayConfig: AlipayConfig = AlipayConfig(),
    val epayConfig: EpayConfig = EpayConfig(),
    val stripeConfig: StripeConfig = StripeConfig(),
    val tronConfig: TronConfig = TronConfig(),

    val editType: EditType = EditType.CREATE,
    val paymentList: List<Payment> = emptyList(),
    val nameError: String = "",

    val paymentTypeExpanded: Boolean = false,

    ) {
    val createIsValid: Boolean
        get() = name.isNotEmpty() &&
                when (paymentType) {
                    PaymentType.ALIPAY -> alipayConfig.isValid
                    PaymentType.EPAY -> epayConfig.isValid
                    PaymentType.STRIPE -> stripeConfig.isValid
                    PaymentType.TRON -> tronConfig.isValid
                }

    val updateIsValid: Boolean // TODO 优化
        get() = id > 0u


    fun toPayment(): Payment {
        var p = Payment(
            name = name,
            status = status.ordinal,
            paymentType = paymentType.name,
            alipayConfig = null,
            epayConfig = null,
            stripeConfig = null,
            tronConfig = null,
        )
        p = when (paymentType) {
            PaymentType.ALIPAY -> p.copy(alipayConfig = alipayConfig)
            PaymentType.EPAY -> p.copy(epayConfig = epayConfig)
            PaymentType.STRIPE -> p.copy(stripeConfig = stripeConfig)
            PaymentType.TRON -> p.copy(tronConfig = tronConfig)
        }
        return p
    }
}

data class PushWidget(
    val name: String = "",
    val status: Status = Status.ENABLE,
    val pushType: PushType = PushType.EMAIL,
    val emailConfig: EmailConfig = EmailConfig(),
    val tgBotConfig: TgBotConfig = TgBotConfig(),

    val nameError: String = "",
    val pushTypeExpanded: Boolean = false,

    val pushList: List<Push> = emptyList(),
    val editType: EditType = EditType.CREATE,

    ) {
    fun toPush(): Push {
        var p = Push(
            name = name,
            status = status.ordinal,
            pushType = pushType.name,
            emailConfig = null,
            tgBotConfig = null
        )
        p = when (pushType) {
            PushType.TG_BOT -> p.copy(tgBotConfig = tgBotConfig)
            PushType.EMAIL -> p.copy(emailConfig = emailConfig)
        }
        return p
    }
}