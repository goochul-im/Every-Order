package com.everyorder.security.authenticate

import com.auth0.jwt.exceptions.TokenExpiredException
import com.auth0.jwt.interfaces.DecodedJWT
import com.everyorder.domain.member.Member
import com.everyorder.domain.member.Role
import com.everyorder.exception.InvalidRefreshTokenException
import com.everyorder.security.CustomUserDetails
import com.everyorder.security.SecurityResponseHandler
import com.everyorder.util.CookieProvider
import com.everyorder.util.JwtConstant
import com.everyorder.util.JwtManager
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    val jwtManager: JwtManager
) : OncePerRequestFilter() {

    private val log = KotlinLogging.logger { }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain
    ) {

        val token = jwtManager.getAccessTokenFromRequest(request)

        if (token == null) {
            log.error { "Token is null" }
            chain.doFilter(request, response)
            return
        }

        try {
            val decodedJWT = jwtManager.validateAccessToken(token)
            setAuthentication(decodedJWT)
        } catch (e: TokenExpiredException) {
            log.warn("액세스 토큰이 만료되었습니다. 리프레시 토큰을 확인합니다")
            reissueToken(request, response)
        } catch (e: Exception) {
            log.error { "인증된 토큰이 아닙니다. ${e.message}" }
            SecurityContextHolder.clearContext()
        }

        chain.doFilter(request, response)
    }

    private fun setAuthentication(token: DecodedJWT) {
        val socialId = jwtManager.getMemberSocialIdFromToken(token)
        val role = jwtManager.getMemberRoleFromToken(token)

        val member = Member.forAuthentication(socialId, role)

        val principal = CustomUserDetails(member, null)
        val authorities = listOf(SimpleGrantedAuthority(role))

        val authentication = UsernamePasswordAuthenticationToken(
            principal,
            null,
            authorities
        )

        SecurityContextHolder.getContext().authentication = authentication
        log.debug { "id:${socialId} 요청" }
    }

    private fun reissueToken(request: HttpServletRequest, response: HttpServletResponse) {

        try {
            val refreshToken = CookieProvider.getCookie(request, JwtConstant.REFRESH_TOKEN_NAME)
                ?: throw InvalidRefreshTokenException()

            val decodedRefreshToken = jwtManager.validateRefreshToken(refreshToken)
            val newAccessToken = jwtManager.reissueAccessToken(decodedRefreshToken)
            val newDecodedAccessToken = jwtManager.validateAccessToken(newAccessToken)

            setAuthentication(newDecodedAccessToken)
            response.setHeader(JwtConstant.JWT_HEADER, JwtConstant.JWT_PREFIX + newAccessToken)
            log.info { "액세스 토큰이 재발급되었습니다" }

        } catch (e: InvalidRefreshTokenException) {
            log.warn("리프레시 토큰이 유효하지 않습니다 : ${e.message}")
            SecurityContextHolder.clearContext()

            SecurityResponseHandler.sendErrorResponse(
                response,
                HttpStatus.UNAUTHORIZED,
                "리프레시 토큰이 만료되었거나, 유효하지 않습니다"
            )

            return
        } catch (e: Exception) {
            log.error { "알수없는 서버 오류로 액세스 토큰 재발급 실패" }
            SecurityContextHolder.clearContext()
        }

    }

}
