package com.everyorder.util

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import kotlin.jvm.java
import kotlin.let

@Converter
class StringListConverter : AttributeConverter<List<String>, String> {

    private val objectMapper = ObjectMapper()

    // List -> String (JSON)으로 변환하여 DB에 저장
    override fun convertToDatabaseColumn(attribute: List<String>?): String? {
        // attribute가 null이 아니면 JSON 문자열로 변환
        return attribute?.let { objectMapper.writeValueAsString(it) }
    }

    // String (JSON) -> List로 변환하여 엔티티에 로드
    override fun convertToEntityAttribute(dbData: String?): List<String>? {
        // dbData가 null이 아니면 List<String> 타입으로 변환
        return dbData?.let {
            objectMapper.readValue(it, List::class.java) as List<String>
        }
    }
}
