package io.github.ppoonk.airgo_master.repository.remote

import io.github.ppoonk.ac.utils.ApiConfig
import io.github.ppoonk.ac.utils.ApiHttpClient
import io.github.ppoonk.ac.utils.Result
import io.github.ppoonk.airgo_master.repository.remote.model.CreateCouponReq
import io.github.ppoonk.airgo_master.repository.remote.model.CreateNodeReq
import io.github.ppoonk.airgo_master.repository.remote.model.CreateProductReq
import io.github.ppoonk.airgo_master.repository.remote.model.CreateProtocolReq
import io.github.ppoonk.airgo_master.repository.remote.model.CreateProtocolTemplateReq
import io.github.ppoonk.airgo_master.repository.remote.model.CreateUserReq
import io.github.ppoonk.airgo_master.repository.remote.model.DeleteCouponReq
import io.github.ppoonk.airgo_master.repository.remote.model.DeleteNodeReq
import io.github.ppoonk.airgo_master.repository.remote.model.DeleteProductReq
import io.github.ppoonk.airgo_master.repository.remote.model.DeleteProtocolReq
import io.github.ppoonk.airgo_master.repository.remote.model.DeleteProtocolTemplateReq
import io.github.ppoonk.airgo_master.repository.remote.model.DeleteUserReq
import io.github.ppoonk.airgo_master.repository.remote.model.GetCouponListReq
import io.github.ppoonk.airgo_master.repository.remote.model.GetCouponListRes
import io.github.ppoonk.airgo_master.repository.remote.model.GetNodeListReq
import io.github.ppoonk.airgo_master.repository.remote.model.GetNodeListRes
import io.github.ppoonk.airgo_master.repository.remote.model.GetPaymentListRes
import io.github.ppoonk.airgo_master.repository.remote.model.GetProductListReq
import io.github.ppoonk.airgo_master.repository.remote.model.GetProductListRes
import io.github.ppoonk.airgo_master.repository.remote.model.GetProtocolListReq
import io.github.ppoonk.airgo_master.repository.remote.model.GetProtocolListRes
import io.github.ppoonk.airgo_master.repository.remote.model.GetProtocolTemplateListReq
import io.github.ppoonk.airgo_master.repository.remote.model.GetProtocolTemplateListRes
import io.github.ppoonk.airgo_master.repository.remote.model.GetPushListRes
import io.github.ppoonk.airgo_master.repository.remote.model.GetUserListReq
import io.github.ppoonk.airgo_master.repository.remote.model.GetUserListRes
import io.github.ppoonk.airgo_master.repository.remote.model.Security
import io.github.ppoonk.airgo_master.repository.remote.model.SignInReq
import io.github.ppoonk.airgo_master.repository.remote.model.SignInRes
import io.github.ppoonk.airgo_master.repository.remote.model.UpdateCouponReq
import io.github.ppoonk.airgo_master.repository.remote.model.UpdateNodeReq
import io.github.ppoonk.airgo_master.repository.remote.model.UpdatePaymentListReq
import io.github.ppoonk.airgo_master.repository.remote.model.UpdateProductReq
import io.github.ppoonk.airgo_master.repository.remote.model.UpdateProtocolReq
import io.github.ppoonk.airgo_master.repository.remote.model.UpdateProtocolTemplateReq
import io.github.ppoonk.airgo_master.repository.remote.model.UpdatePushListReq
import io.github.ppoonk.airgo_master.repository.remote.model.UpdateUserReq
import io.github.ppoonk.airgo_master.repository.remote.model.User
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod


object ApiService {
    private var baseUrl: () -> String = { "" }
    private var token: () -> String = { "" }
    private var adminPath: () -> String = { "" }
    private var httpClient = ApiHttpClient()

    fun setBaseUrl(v: () -> String): Unit {
        baseUrl = v
    }

    fun setAdminPath(v: () -> String): Unit {
        adminPath = v
    }

    fun setToken(v: () -> String): Unit {
        token = v
    }

    fun setHttpClient(v: ApiHttpClient): Unit {
        httpClient = v
    }


    // user open
    suspend fun signIn(params: SignInReq): Result<SignInRes> {
        return httpClient.request(
            config = ApiConfig(
                url = baseUrl() + "/user/sign_in",
                method = HttpMethod.Post,
                headers = emptyMap(),
            ), params = params
        )
    }

    // user
    suspend fun getUserInfo(): Result<User> {
        return httpClient.request(
            config = ApiConfig(
                url = baseUrl() + "/user/info",
                method = HttpMethod.Get,
                headers = mapOf(Pair(HttpHeaders.Authorization, token()))
            ), params = null
        )
    }

    // admin user
    suspend fun getUserList(params: GetUserListReq): Result<GetUserListRes> {
        return httpClient.request(
            config = ApiConfig(
                url = baseUrl() + adminPath() + "/user/list",
                method = HttpMethod.Post,
                headers = mapOf(Pair(HttpHeaders.Authorization, token()))
            ), params = params
        )
    }

    suspend fun updateUser(params: UpdateUserReq): Result<String> {
        return httpClient.request(
            config = ApiConfig(
                url = baseUrl() + adminPath() + "/user",
                method = HttpMethod.Put,
                headers = mapOf(Pair(HttpHeaders.Authorization, token()))
            ), params = params
        )
    }

    suspend fun createUser(params: CreateUserReq): Result<String> {
        return httpClient.request(
            config = ApiConfig(
                url = baseUrl() + adminPath() + "/user",
                method = HttpMethod.Post,
                headers = mapOf(Pair(HttpHeaders.Authorization, token()))
            ), params = params
        )
    }

    suspend fun deleteUser(params: DeleteUserReq): Result<String> {
        return httpClient.request(
            config = ApiConfig(
                url = baseUrl() + adminPath() + "/user",
                method = HttpMethod.Delete,
                headers = mapOf(Pair(HttpHeaders.Authorization, token()))
            ), params = params
        )
    }

    // admin node
    suspend fun createNode(params: CreateNodeReq): Result<String> {
        return httpClient.request(
            config = ApiConfig(
                url = baseUrl() + adminPath() + "/node",
                method = HttpMethod.Post,
                headers = mapOf(Pair(HttpHeaders.Authorization, token()))
            ), params = params
        )
    }

    suspend fun deleteNode(params: DeleteNodeReq): Result<String> {
        return httpClient.request(
            config = ApiConfig(
                url = baseUrl() + adminPath() + "/node",
                method = HttpMethod.Delete,
                headers = mapOf(Pair(HttpHeaders.Authorization, token()))
            ), params = params
        )
    }

    suspend fun updateNode(params: UpdateNodeReq): Result<String> {
        return httpClient.request(
            config = ApiConfig(
                url = baseUrl() + adminPath() + "/node",
                method = HttpMethod.Put,
                headers = mapOf(Pair(HttpHeaders.Authorization, token()))
            ), params = params
        )
    }

    suspend fun getNodeList(params: GetNodeListReq): Result<GetNodeListRes> {
        return httpClient.request(
            config = ApiConfig(
                url = baseUrl() + adminPath() + "/node/list",
                method = HttpMethod.Post,
                headers = mapOf(Pair(HttpHeaders.Authorization, token()))
            ), params = params
        )
    }

    // admin protocol
    suspend fun createProtocol(params: CreateProtocolReq): Result<String> {
        return httpClient.request(
            config = ApiConfig(
                url = baseUrl() + adminPath() + "/node/protocol",
                method = HttpMethod.Post,
                headers = mapOf(Pair(HttpHeaders.Authorization, token()))
            ), params = params
        )
    }

    suspend fun deleteProtocol(params: DeleteProtocolReq): Result<String> {
        return httpClient.request(
            config = ApiConfig(
                url = baseUrl() + adminPath() + "/node/protocol",
                method = HttpMethod.Delete,
                headers = mapOf(Pair(HttpHeaders.Authorization, token()))
            ), params = params
        )
    }

    suspend fun updateProtocol(params: UpdateProtocolReq): Result<String> {
        return httpClient.request(
            config = ApiConfig(
                url = baseUrl() + adminPath() + "/node/protocol",
                method = HttpMethod.Put,
                headers = mapOf(Pair(HttpHeaders.Authorization, token()))
            ), params = params
        )
    }

    suspend fun getProtocolList(params: GetProtocolListReq): Result<GetProtocolListRes> {
        return httpClient.request(
            config = ApiConfig(
                url = baseUrl() + adminPath() + "/node/protocol/list",
                method = HttpMethod.Post,
                headers = mapOf(Pair(HttpHeaders.Authorization, token()))
            ), params = params
        )
    }

    // admin protocol template
    suspend fun createProtocolTemplate(params: CreateProtocolTemplateReq): Result<String> {
        return httpClient.request(
            config = ApiConfig(
                url = baseUrl() + adminPath() + "/node/protocolTemplate",
                method = HttpMethod.Post,
                headers = mapOf(Pair(HttpHeaders.Authorization, token()))
            ), params = params
        )
    }

    suspend fun deleteProtocolTemplate(params: DeleteProtocolTemplateReq): Result<String> {
        return httpClient.request(
            config = ApiConfig(
                url = baseUrl() + adminPath() + "/node/protocolTemplate",
                method = HttpMethod.Delete,
                headers = mapOf(Pair(HttpHeaders.Authorization, token()))
            ), params = params
        )
    }

    suspend fun updateProtocolTemplate(params: UpdateProtocolTemplateReq): Result<String> {
        return httpClient.request(
            config = ApiConfig(
                url = baseUrl() + adminPath() + "/node/protocolTemplate",
                method = HttpMethod.Put,
                headers = mapOf(Pair(HttpHeaders.Authorization, token()))
            ), params = params
        )
    }

    suspend fun getProtocolTemplateList(params: GetProtocolTemplateListReq): Result<GetProtocolTemplateListRes> {
        return httpClient.request(
            config = ApiConfig(
                url = baseUrl() + adminPath() + "/node/protocolTemplate/list",
                method = HttpMethod.Post,
                headers = mapOf(Pair(HttpHeaders.Authorization, token()))
            ), params = params
        )
    }

    // admin product
    suspend fun createProduct(params: CreateProductReq): Result<String> {
        return httpClient.request(
            config = ApiConfig(
                url = baseUrl() + adminPath() + "/product",
                method = HttpMethod.Post,
                headers = mapOf(Pair(HttpHeaders.Authorization, token()))
            ), params = params
        )
    }

    suspend fun deleteProduct(params: DeleteProductReq): Result<String> {
        return httpClient.request(
            config = ApiConfig(
                url = baseUrl() + adminPath() + "/product",
                method = HttpMethod.Delete,
                headers = mapOf(Pair(HttpHeaders.Authorization, token()))
            ), params = params
        )
    }

    suspend fun updateProduct(params: UpdateProductReq): Result<String> {
        return httpClient.request(
            config = ApiConfig(
                url = baseUrl() + adminPath() + "/product",
                method = HttpMethod.Put,
                headers = mapOf(Pair(HttpHeaders.Authorization, token()))
            ), params = params
        )
    }

    suspend fun getProductList(params: GetProductListReq): Result<GetProductListRes> {
        return httpClient.request(
            config = ApiConfig(
                url = baseUrl() + adminPath() + "/product/list",
                method = HttpMethod.Post,
                headers = mapOf(Pair(HttpHeaders.Authorization, token()))
            ), params = params
        )
    }

    // admin configuration
    suspend fun getSecurity(): Result<Security> {
        return httpClient.request(
            config = ApiConfig(
                url = baseUrl() + adminPath() + "/configuration/security",
                method = HttpMethod.Get,
                headers = mapOf(Pair(HttpHeaders.Authorization, token()))
            ), params = null
        )
    }

    suspend fun updateSecurity(params: Security): Result<String> {
        return httpClient.request(
            config = ApiConfig(
                url = baseUrl() + adminPath() + "/configuration/security",
                method = HttpMethod.Put,
                headers = mapOf(Pair(HttpHeaders.Authorization, token()))
            ), params = params
        )
    }

    suspend fun getPaymentList(): Result<GetPaymentListRes> {
        return httpClient.request(
            config = ApiConfig(
                url = baseUrl() + adminPath() + "/configuration/paymentList",
                method = HttpMethod.Get,
                headers = mapOf(Pair(HttpHeaders.Authorization, token()))
            ), params = null
        )
    }

    suspend fun updatePaymentList(params: UpdatePaymentListReq): Result<String> {
        return httpClient.request(
            config = ApiConfig(
                url = baseUrl() + adminPath() + "/configuration/paymentList",
                method = HttpMethod.Put,
                headers = mapOf(Pair(HttpHeaders.Authorization, token()))
            ), params = params
        )
    }

    suspend fun getPushList(): Result<GetPushListRes> {
        return httpClient.request(
            config = ApiConfig(
                url = baseUrl() + adminPath() + "/configuration/pushList",
                method = HttpMethod.Get,
                headers = mapOf(Pair(HttpHeaders.Authorization, token()))
            ), params = null
        )
    }

    suspend fun updatePushList(params: UpdatePushListReq): Result<String> {
        return httpClient.request(
            config = ApiConfig(
                url = baseUrl() + adminPath() + "/configuration/pushList",
                method = HttpMethod.Put,
                headers = mapOf(Pair(HttpHeaders.Authorization, token()))
            ), params = params
        )
    }


    // admin coupon
    suspend fun createCoupon(params: CreateCouponReq): Result<String> {
        return httpClient.request(
            config = ApiConfig(
                url = baseUrl() + adminPath() + "/coupon",
                method = HttpMethod.Post,
                headers = mapOf(Pair(HttpHeaders.Authorization, token()))
            ), params = params
        )
    }

    suspend fun deleteCoupon(params: DeleteCouponReq): Result<String> {
        return httpClient.request(
            config = ApiConfig(
                url = baseUrl() + adminPath() + "/coupon",
                method = HttpMethod.Delete,
                headers = mapOf(Pair(HttpHeaders.Authorization, token()))
            ), params = params
        )
    }

    suspend fun updateCoupon(params: UpdateCouponReq): Result<String> {
        return httpClient.request(
            config = ApiConfig(
                url = baseUrl() + adminPath() + "/coupon",
                method = HttpMethod.Put,
                headers = mapOf(Pair(HttpHeaders.Authorization, token()))
            ), params = params
        )
    }

    suspend fun getCouponList(params: GetCouponListReq): Result<GetCouponListRes> {
        return httpClient.request(
            config = ApiConfig(
                url = baseUrl() + adminPath() + "/coupon/list",
                method = HttpMethod.Post,
                headers = mapOf(Pair(HttpHeaders.Authorization, token()))
            ), params = params
        )
    }
}