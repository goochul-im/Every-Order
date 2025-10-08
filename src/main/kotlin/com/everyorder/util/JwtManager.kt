package com.everyorder.util

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.exceptions.TokenExpiredException
import com.auth0.jwt.interfaces.DecodedJWT
import com.everyorder.domain.member.Member
import com.everyorder.exception.InvalidRefreshTokenException
import com.everyorder.security.CustomUserDetails
import jakarta.servlet.http.HttpServletRequest
import mu.KotlinLogging
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.util.Date
import java.util.concurrent.TimeUnit


@Component
class JwtManager(
    private val redisTemplate: RedisTemplate<String, String>
) {

    private val accessTokenExpireMinutes : Long = 5
    private val refreshTokenExpireHours : Long = JwtConstant.REFRESH_TOKEN_EXPIRE_HOUR

    val jwtSubject = "my-token"
    val algorithm: Algorithm = Algorithm.HMAC512(JwtConstant.ACCESS_SECRET_KET)
    val verifier: JWTVerifier = JWT.require(algorithm).build()
    private val log = KotlinLogging.logger { }

    fun getAccessTokenFromRequest(request : HttpServletRequest): String? {
        return request.getHeader(JwtConstant.JWT_HEADER)?.replace(JwtConstant.JWT_PREFIX,"")?.trim()
    }

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
            throw InvalidRefreshTokenException()
        }
    }

    fun getMemberSocialIdFromToken(token: DecodedJWT): String {
        return token.getClaim(JwtConstant.CLAIM_ID).asString()
    }

    fun getMemberRoleFromToken(token: DecodedJWT): String {
        return token.getClaim(JwtConstant.CLAIM_ROLE).asString()
    }

    fun reissueAccessToken(refreshToken: DecodedJWT): String {
        val socialId = getMemberSocialIdFromToken(refreshToken)
        val role = getMemberRoleFromToken(refreshToken)

        val member = Member.forAuthentication(socialId, role)
        val principal = CustomUserDetails(member)

        return generateAccessToken(principal)
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

    private fun getRedisKey(socialId: String): String {
        return "${JwtConstant.REFRESH_TOKEN_REDIS_PREFIX}$socialId"
    }

    fun saveRefreshToken(socialId: String, refreshToken: String) {
        val key = getRedisKey(socialId)
        redisTemplate.opsForValue().set(key, refreshToken, refreshTokenExpireHours, TimeUnit.HOURS)
    }

    fun removeRefreshToken(socialId: String) {
        val key = getRedisKey(socialId)
        redisTemplate.delete(key)
    }

    /**
     * Retrieves a refresh token associated with the provided social ID from the Redis data store.
     *
     * @param socialId the unique identifier of the user in the social system used as a key to fetch the refresh token
     * @return the refresh token as a string if it exists; null otherwise
     */
    fun getRefreshToken(socialId: String): String? {
        val key = getRedisKey(socialId)
        return redisTemplate.opsForValue().get(key)
    }

}
