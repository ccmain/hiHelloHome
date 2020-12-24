package com.example.hiHelloHome.service

import com.example.hiHelloHome.common.ApiCallService
import com.example.hiHelloHome.common.ResourceRouteName
import com.example.hiHelloHome.common.constants.Env
import com.example.hiHelloHome.common.constants.Image
import com.example.hiHelloHome.dto.ImageUploadAWSResponse
import org.springframework.core.ParameterizedTypeReference
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest
import javax.servlet.http.HttpServletRequest

@Service
class ImageService(
        private val env: Environment,
        private val resourceClient: ApiCallService
) {
    fun imageUpload(request: HttpServletRequest): ImageUploadAWSResponse? {
        try {
            getResourceFromRequest(request).let { fileResources ->
                val resources: MultiValueMap<String, Any> = LinkedMultiValueMap()
                // image 서버에서 체크함
                resources.add(Image.UPLOAD_IMAGE_KEY, Image.UPLOAD_IMAGE_VALUE)

                // image 서버에서 체크함
                resources.add(Image.UPLOAD_RESOURCE_KEY, fileResources[0])

                // prod 이외의 환경에서는 origin server 가 dev 환경만 있기 때문에 각 front domain 을 분기할 env 추가
                resources.add("env", Env.getEnv(env))

                resourceClient.postForFileUploadResponseObject(
                        ResourceRouteName.UPLOAD_AWS,
                        resources,
                        Image.UPLOAD_PATH,
                        object : ParameterizedTypeReference<ImageUploadAWSResponse>() {}
                ).run {
                    return this
                }
            }
        } catch (e: RuntimeException) {

        } finally {
            return null
        }
    }

    private fun getResourceFromRequest(request: HttpServletRequest): MutableList<MultipartFile> {
        val multipartHttpServletRequest = request as MultipartHttpServletRequest
        return multipartHttpServletRequest.getFiles("file")
    }
}