package com.example.hiHelloHome.dto

data class ImageUploadAWSResponse(
        var name: String = "",
        var extension: String = "",
        var width: Int = 0,
        var height: Int = 0,
        var size: Int = 0,
        var mimeType: String = "",
        var uri: String = ""
)