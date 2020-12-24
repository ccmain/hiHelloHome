package com.example.hiHelloHome.controller

import com.example.hiHelloHome.dto.ImageUploadAWSResponse
import com.example.hiHelloHome.service.ImageService
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/image")
class Images(
        private val imageService: ImageService
) {
    @PostMapping("/upload")
    fun upload(request: HttpServletRequest): ImageUploadAWSResponse? {
        return imageService.imageUpload(request)
    }
}