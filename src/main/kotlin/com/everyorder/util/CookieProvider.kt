package com.everyorder.util

import jakarta.servlet.http.HttpServletRequest
import mu.KotlinLogging
import org.springframework.http.ResponseCookie

object CookieProvider {

    private val log = KotlinLogging.logger { }

    fun createCookie(cookieName:String, value:String, maxAge:Long) : ResponseCookie {
        val cookie = ResponseCookie.from(cookieName, value)
            .httpOnly(true)
            .secure(false) //TODO : 이후에 https 적용
            .path("/")
            .maxAge(maxAge)
            .build()

        return cookie
    }

    fun getCookie(request: HttpServletRequest, cookieName: String): String? {
        val cookieValue = request.cookies?.find { it.name == cookieName }?.value

        log.info { "cookieValue : $cookieValue" }

        return cookieValue
    }

}
