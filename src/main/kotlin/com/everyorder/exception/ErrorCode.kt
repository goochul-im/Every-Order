package com.everyorder.exception

import com.fasterxml.jackson.annotation.JsonFormat

@JsonFormat(shape = JsonFormat.Shape.OBJECT) // json으로 직렬화될수 있도록
enum class ErrorCode(
    val code: String,
    val message: String
) {

    INVALID_INPUT_VALUE("E001", "Invalid Input Value"),
    ENTITY_NOT_FOUND("E002", "Entity Not Found"),
    ENTITY_ALREADY_EXISTS("E003", "Entity Already Exists"),
    INVALID_JWT("E004", "Invalid JWT"),
    SYSTEM_ERROR("E999", "System Error")

}
