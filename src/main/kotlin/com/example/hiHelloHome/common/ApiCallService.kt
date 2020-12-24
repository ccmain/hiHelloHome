package com.example.hiHelloHome.common

import com.example.hiHelloHome.common.exception.ApiCallException
import org.apache.http.conn.ConnectTimeoutException
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.MultiValueMap
import org.springframework.web.client.*
import java.net.SocketTimeoutException

@Service
class ApiCallService(
        private val restTemplate: RestTemplate,
        @Value("\${green.resource-routes.upload-aws.url}") private val uploadAws: String
) {
    private fun getDomain(routeName: String) = when (routeName) {
        ResourceRouteName.UPLOAD_AWS     -> uploadAws
        else                             -> ""
    }

    @JvmOverloads
    fun <T> getForResponseObject(routeName: String,
                                 resource: String,
                                 typeReference: ParameterizedTypeReference<T>,
                                 headers: HttpHeaders = HttpHeaders()
    ) = exchange("""${getDomain(routeName)}${resource}""",
                 HttpMethod.GET,
                 HttpEntity<Any>(headers),
                 typeReference).body

    @JvmOverloads
    fun <T> getForResponseEntity(routeName: String,
                                 resource: String,
                                 typeReference: ParameterizedTypeReference<T>,
                                 headers: HttpHeaders = HttpHeaders()
    ) = exchange("""${getDomain(routeName)}${resource}""",
                 HttpMethod.GET,
                 HttpEntity<Any>(headers),
                 typeReference)

    @JvmOverloads
    fun <T> postForResponseObject(routeName: String,
                                  obj: Any?,
                                  resource: String,
                                  typeReference: ParameterizedTypeReference<T>,
                                  headers: HttpHeaders = HttpHeaders()
    ) = exchange("""${getDomain(routeName)}${resource}""",
                 HttpMethod.POST,
                 HttpEntity(obj, headers),
                 typeReference).body

    @JvmOverloads
    fun <T> postForFileUploadResponseObject(routeName: String,
                                            multiValueMap: MultiValueMap<String, Any>,
                                            resource: String,
                                            typeReference: ParameterizedTypeReference<T>,
                                            headers: HttpHeaders = HttpHeaders()
    ) = restTemplate.exchange("""${getDomain(routeName)}${resource}""",
                              HttpMethod.POST,
                              HttpEntity(multiValueMap, headers.apply {
                                  contentType = MediaType.MULTIPART_FORM_DATA
                              }),
                              typeReference).body

    private fun <T> exchange(uri: String,
                             method: HttpMethod,
                             requestEntity: HttpEntity<Any>,
                             responseType: ParameterizedTypeReference<T>
    ) = try {
        restTemplate.exchange(uri, method, requestEntity, responseType)
    } catch (ex: HttpClientErrorException) {
        // httpsStatus가 4xx, 5xx series일 경우
        throw ApiCallException(uri, ex);
    } catch (ex: HttpServerErrorException) {
        // httpsStatus가 4xx, 5xx series일 경우
        throw ApiCallException(uri, ex);
    } catch (ex: UnknownHttpStatusCodeException) {
        // 스프링에서 정의되지 않은 httpStatus 일 경우
        throw ApiCallException(uri, ex);
    } catch (ex: ResourceAccessException) {
        // Request 관련 timeout 예외가 발생한 경우.
        if (ex.cause is SocketTimeoutException || ex.cause is ConnectTimeoutException) {
            // timeout Exception
            throw ApiCallException(ex);
        }
        throw ApiCallException(uri, ex);
    } catch (ex: Exception) {
        // 그 외 기타 Exception 일 경우
        throw ApiCallException(uri, ex);
    }
}