package com.everyorder.util

object JwtConstant {

    const val JWT_HEADER = "Authorization"
    const val CLAIM_ID = "id"
    const val CLAIM_ROLE = "role"
    const val JWT_PREFIX = "Bearer "
    const val ACCESS_SECRET_KET = "secret"
    const val REFRESH_SECRET_KEY = "refreshSecret"
    const val REFRESH_TOKEN_NAME = "every_order_refresh_token"
    const val REFRESH_TOKEN_EXPIRE_HOUR : Long = 48

}
