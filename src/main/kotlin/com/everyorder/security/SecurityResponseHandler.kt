package com.everyorder.security

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

class SecurityResponseHandler {

    private val objectMapper = ObjectMapper()

    fun sendErrorResponse(response: HttpServletResponse, status: HttpStatus, message: String) {
        response.status = status.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = "UTF-8"

        val errorResponse = mapOf(
            "status" to status.value(),
            "error" to status.reasonPhrase,
            "message" to message
        )
        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }

}
