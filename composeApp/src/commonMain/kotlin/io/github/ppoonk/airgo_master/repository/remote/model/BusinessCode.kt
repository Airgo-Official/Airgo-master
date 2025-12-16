package io.github.ppoonk.airgo_master.repository.remote.model
sealed class BusinessCode(val code: Int, val message: String) {
    // 通用状态码 (1xxxx)
    object Success : BusinessCode(10000, "Success")
    object ServerError : BusinessCode(10001, "Internal server error")

    // 客户端 (2xxxx)
    object BadRequest : BusinessCode(20001, "Invalid request format")
    object Unauthorized : BusinessCode(20002, "Authentication required")
    object Forbidden : BusinessCode(20003, "Insufficient permissions")
    object NotFound : BusinessCode(20004, "Resource not found")
    object MethodNotAllowed : BusinessCode(20005, "HTTP method not allowed")
    object RequestTimeout : BusinessCode(20006, "Request timeout")

    // 认证 (3xxxx)
    object AuthFailed : BusinessCode(30001, "Authentication failed")
    object InvalidToken : BusinessCode(30002, "Invalid authentication token")
    object TokenExpired : BusinessCode(30003, "Authentication token expired")
    object VerifyIdentityFailed : BusinessCode(30004, "Failed to verify the identity of users")

    // 服务端 (5xxxx)
    object InternalError : BusinessCode(50001, "Internal server error")
    object NotImplemented : BusinessCode(50002, "Feature not implemented")
    object ServiceUnavailable : BusinessCode(50003, "Service unavailable")
    object GatewayTimeout : BusinessCode(50004, "Gateway timeout")
    object VersionNotSupported : BusinessCode(50005, "API version not supported")
    object DiskFull : BusinessCode(50006, "Server disk space full")
    object ConfigurationError : BusinessCode(50007, "Server configuration error")
    object JsonError : BusinessCode(50008, "Json error")

    // 数据库 (6xxxx)
    object DBConnectionFailed : BusinessCode(60001, "Database connection failed")
    object DBQueryFailed : BusinessCode(60002, "Database query failed")
    object DBInsertFailed : BusinessCode(60003, "Database insert operation failed")
    object DBUpdateFailed : BusinessCode(60004, "Database update operation failed")
    object DBDeleteFailed : BusinessCode(60005, "Database delete operation failed")
    object DBRecordNotFound : BusinessCode(60006, "Record not found in database")
    object DBRecordExists : BusinessCode(60007, "Record already exists in database")

    // 第三方服务 (7xxxx)
    object ThirdPartyUnavailable : BusinessCode(70001, "Third-party service unavailable")
    object ThirdPartyTimeout : BusinessCode(70002, "Third-party service timeout")
    object PaymentFailed : BusinessCode(70003, "Payment processing failed")
    object SMSFailed : BusinessCode(70004, "SMS delivery failed")
    object EmailFailed : BusinessCode(70005, "Email delivery failed")
    object APILimitExceeded : BusinessCode(70006, "Third-party API limit exceeded")
    object InvalidAPIResponse : BusinessCode(70007, "Invalid third-party API response")

    // 限流/熔断 (8xxxx)
    object TooManyRequests : BusinessCode(80001, "Too many requests")
    object IPBlocked : BusinessCode(80002, "IP address blocked")

    // 业务逻辑 - 优惠券
    object CouponNotExist : BusinessCode(40101, "Coupon not exist")
    object CouponInvalid : BusinessCode(40102, "Coupon invalid")
    object CouponConditionsNotMet : BusinessCode(40103, "Coupon conditions not met")
    object CouponNotApplicable : BusinessCode(40104, "Coupon not applicable for this product")

    // 业务逻辑 - 用户
    object UserInvalidPassword : BusinessCode(40801, "Invalid password")
}