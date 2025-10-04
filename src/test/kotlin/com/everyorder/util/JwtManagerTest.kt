package com.everyorder.util

import com.auth0.jwt.exceptions.TokenExpiredException
import com.everyorder.domain.member.Member
import com.everyorder.exception.InvalidRefreshTokenException
import com.everyorder.security.CustomUserDetails
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.*

class JwtManagerTest {

    private val jwtManager = JwtManager()

    private fun createTestUserDetails(): CustomUserDetails {
        return CustomUserDetails(
            Member.createFakeMember()
        )
    }

    @Test
    @DisplayName("액세스 토큰 발급 및 검증 성공")
    fun `validateAccessToken with valid token should return decoded JWT`() {
        // given
        val userDetails = createTestUserDetails()
        val expireDate = Date(System.currentTimeMillis() + 60000) // 1분 후 만료
        val token = jwtManager.generateToken(expireDate, userDetails, "ROLE_USER")

        // when
        val decodedJWT = jwtManager.validateAccessToken(token)

        // then
        assertEquals(userDetails.name, decodedJWT.getClaim(JwtConstant.CLAIM_ID).asString())
        assertEquals("ROLE_USER", decodedJWT.getClaim(JwtConstant.CLAIM_ROLE).asString())
    }

    @Test
    @DisplayName("만료된 액세스 토큰 검증 시 TokenExpiredException 발생")
    fun `validateAccessToken with expired token should throw TokenExpiredException`() {
        // given
        val userDetails = createTestUserDetails()
        val expireDate = Date(System.currentTimeMillis() - 1000) // 1초 전 만료
        val token = jwtManager.generateToken(expireDate, userDetails, "ROLE_USER")

        // when & then
        assertThrows(TokenExpiredException::class.java) {
            jwtManager.validateAccessToken(token)
        }
    }

    @Test
    @DisplayName("잘못된 형식의 액세스 토큰 검증 시 RuntimeException 발생")
    fun `validateAccessToken with invalid token should throw RuntimeException`() {
        // given
        val invalidToken = "invalid.token.string"

        // when & then
        val exception = assertThrows(RuntimeException::class.java) {
            jwtManager.validateAccessToken(invalidToken)
        }
        assertEquals("Invalid refresh Token", exception.message)
    }

    @Test
    @DisplayName("리프레시 토큰 발급 및 검증 성공")
    fun `validateRefreshToken with valid token should return decoded JWT`() {
        // given
        val userDetails = createTestUserDetails()
        val expireDate = Date(System.currentTimeMillis() + 60000) // 1분 후 만료
        val token = jwtManager.generateToken(expireDate, userDetails, "ROLE_USER")

        // when
        val decodedJWT = jwtManager.validateRefreshToken(token)

        // then
        assertEquals(userDetails.name, decodedJWT.getClaim(JwtConstant.CLAIM_ID).asString())
        assertEquals("ROLE_USER", decodedJWT.getClaim(JwtConstant.CLAIM_ROLE).asString())
    }

    @Test
    @DisplayName("잘못된 형식의 리프레시 토큰 검증 시 RuntimeException 발생")
    fun `validateRefreshToken with invalid token should throw RuntimeException`() {
        // given
        val invalidToken = "invalid.token.string"

        // when & then
        val exception = assertThrows(InvalidRefreshTokenException::class.java) {
            jwtManager.validateRefreshToken(invalidToken)
        }
        assertEquals(InvalidRefreshTokenException().message, exception.message)
    }

    // generateToken is private, so we test it indirectly via the validation methods.
    // However, if we make it public for testing, this is how we'd test it.
    // For this case, I'll add a test for the private method using reflection.
    @Test
    @DisplayName("generateToken으로 생성된 토큰 검증")
    fun `generateToken should create a valid JWT`() {
        // given
        val userDetails = createTestUserDetails()
        val expireDate = Date(System.currentTimeMillis() + 60000)

        // when
        // Using reflection to test the private method
        val method = jwtManager.javaClass.getDeclaredMethod("generateToken", Date::class.java, CustomUserDetails::class.java, String::class.java)
        method.isAccessible = true
        val token = method.invoke(jwtManager, expireDate, userDetails, "ROLE_USER") as String

        // then
        val decodedJWT = jwtManager.verifier.verify(token)
        assertEquals(Member.FAKE_SOCIAL_ID, decodedJWT.getClaim(JwtConstant.CLAIM_ID).asString())
        assertEquals("ROLE_USER", decodedJWT.getClaim(JwtConstant.CLAIM_ROLE).asString())
        assertEquals(jwtManager.jwtSubject, decodedJWT.subject)
    }
}
