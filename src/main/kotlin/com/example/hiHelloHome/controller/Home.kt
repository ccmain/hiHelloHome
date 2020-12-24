package com.example.hiHelloHome.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class Home {

    @GetMapping("/")
    fun home(): String {
        return "hi hello home"
    }
}