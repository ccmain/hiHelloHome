package com.example.hiHelloHome.common.exception

import org.springframework.http.HttpHeaders
import org.springframework.web.client.RestClientResponseException
import java.nio.charset.Charset

class ApiCallException(e: Throwable) : RuntimeException(e) {
    /**
     * Exception 발생 시 사용한 url
     */
    var resourceUrl: String? = null

    /**
     * api에서 응답한 HttpStatus 정보
     */
    var httpStatus = 0

    /**
     * api에서 응답한 Body 내용을 raw data(byte type)를 전달한다.
     */
    var responseBody: ByteArray = ByteArray(0)

    /**
     * api에서 응답한 Header 정보
     */
    var responseHeaders: HttpHeaders? = null

    fun getResponseBodyString(charset: Charset) = String(responseBody, charset)
    fun getResponseBodyString() = getResponseBodyString(Charset.forName("UTF-8"))

    constructor(resourceUrl: String, e: RestClientResponseException) : this(e) {
        this.resourceUrl = resourceUrl
        this.responseBody = e.responseBodyAsByteArray
        this.httpStatus = e.rawStatusCode
        this.responseHeaders = e.responseHeaders
    }

    constructor(resourceUrl: String, e: Exception) : this(e) {
        this.resourceUrl = resourceUrl
    }
}