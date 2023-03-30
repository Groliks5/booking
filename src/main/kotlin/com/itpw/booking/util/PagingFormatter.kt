package com.itpw.booking.util

import com.itpw.booking.properties.ProxyProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI

@Service
class PagingFormatter @Autowired constructor(
    private val proxyProperties: ProxyProperties
) {
    fun <T: Any, R: Any>formPagingResponse(url: String, page: Page<T>, queryParams: Map<String, String> = mapOf(), mapper: (T) -> R): PagingResponse<R> {
        val previous = if (page.hasPrevious()) {
            val prevQueryParams = queryParams.toMutableMap().apply {
                put("page", (page.number - 1).toString())
                put("page_size", (page.size).toString())
            }
            ServletUriComponentsBuilder.fromUri(URI.create(proxyProperties.uri))
                .path("$url")
                .apply {
                    prevQueryParams.forEach {
                        queryParam(it.key, it.value)
                    }
                }
                .encode()
                .toUriString()
        } else {
            null
        }
        val next = if (page.hasNext()) {
            val nextQueryParams = queryParams.toMutableMap().apply {
                put("page", (page.number + 1).toString())
                put("page_size", (page.size).toString())
            }
            ServletUriComponentsBuilder.fromUri(URI.create(proxyProperties.uri))
                .path("$url")
                .apply {
                    nextQueryParams.forEach {
                        queryParam(it.key, it.value)
                    }
                }
                .encode()
                .toUriString()
        } else {
            null
        }
        return PagingResponse(
            count = page.totalElements,
            previous = previous,
            next = next,
            results = page.content.map {
                mapper(it)
            }
        )
    }
}