package com.everyorder.util

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.exceptions.TokenExpiredException
import com.auth0.jwt.interfaces.DecodedJWT
import com.everyorder.security.oauth.CustomUserDetails
import com.nimbusds.oauth2.sdk.token.RefreshToken
import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.util.Date
import java.util.concurrent.TimeUnit


@Component
class JwtManager {

    private val accessTokenExpireMinutes : Long = 5
    private val refreshTokenExpireHours : Long = JwtConstant.REFRESH_TOKEN_EXPIRE_HOUR

    val jwtSubject = "my-token"
    val algorithm: Algorithm = Algorithm.HMAC512(JwtConstant.ACCESS_SECRET_KET)
    val verifier: JWTVerifier = JWT.require(algorithm).build()
    private val log = KotlinLogging.logger { }

    fun generateAccessToken(principal: CustomUserDetails): String {

        val expireDate = Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(accessTokenExpireMinutes))

        val role = principal.authorities.first().authority

        return generateToken(expireDate, principal, role)
    }

    fun generateRefreshToken(principal: CustomUserDetails): String {
        val expireDate = Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(refreshTokenExpireHours))

        val role = principal.authorities.first().authority

        return generateToken(expireDate, principal, role)
    }

    fun validateAccessToken(accessToken: String) : DecodedJWT {
        try {
            return verifier.verify(accessToken)
        } catch (e: TokenExpiredException) {
            log.error { "access token expired : $e" }
            throw e
        } catch (e: JWTVerificationException) {
            log.error { "Invalid refresh Token : $e" }
            throw RuntimeException("Invalid refresh Token")
        }
    }

    fun validateRefreshToken(refreshToken: String): DecodedJWT {
        try {
            return verifier.verify(refreshToken)
        } catch (e: JWTVerificationException) {
            log.error { "Invalid refresh token : $e" }
            throw RuntimeException("Invalid refresh token")
        }
    }

    fun generateToken(
        expireDate: Date,
        principal: CustomUserDetails,
        role: String?
    ): String = JWT.create()
        .withSubject(jwtSubject)
        .withIssuedAt(Date())
        .withExpiresAt(expireDate)
        .withClaim(JwtConstant.CLAIM_ID, principal.name)
        .withClaim(JwtConstant.CLAIM_ROLE, role)
        .sign(algorithm)

}
