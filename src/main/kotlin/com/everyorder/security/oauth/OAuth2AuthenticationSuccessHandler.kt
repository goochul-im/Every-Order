package com.everyorder.security.oauth

import com.everyorder.security.CustomUserDetails
import com.everyorder.util.CookieProvider
import com.everyorder.util.JwtConstant
import com.everyorder.util.JwtManager
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class OAuth2AuthenticationSuccessHandler(
    val jwtManager: JwtManager
) : AuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val details = authentication.principal as CustomUserDetails

        val accessToken = jwtManager.generateAccessToken(details)
        val refreshToken = jwtManager.generateRefreshToken(details)

        val refreshTokenCookie = CookieProvider.createCookie(
            JwtConstant.REFRESH_TOKEN_NAME,
            refreshToken,
            TimeUnit.HOURS.toSeconds(JwtConstant.REFRESH_TOKEN_EXPIRE_HOUR)
        )

        //TODO: 프론트엔드의 URI로 직접 리다이렉트 시키면서 jwt 발급
        response.addHeader(JwtConstant.JWT_HEADER, "${JwtConstant.JWT_PREFIX}$accessToken")
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
    }
}
