package com.everyorder.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class CheckController {

    @GetMapping("/health")
    fun healthCheck() = "hello"

    @GetMapping("/auth")
    fun authCheck() = "auth"

}
