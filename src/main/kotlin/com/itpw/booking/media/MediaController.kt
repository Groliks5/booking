package com.itpw.booking.media

import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.logging.Logger

@RestController
@RequestMapping("/media")
class MediaController @Autowired constructor(
    private val filesUploadService: FilesUploadService,
) {

    @GetMapping("/get/{*path_parts}")
    fun getFile(
        @PathVariable("path_parts") path: String,
        request: HttpServletRequest
    ): ResponseEntity<Resource> {
        val pathParts = path.split('/').filter { it.isNotBlank() }
        val catalogs = pathParts.dropLast(1)
        val fileName = pathParts.last()
        val resource = filesUploadService.loadFileAsResource(catalogs, fileName)

        val contentType = try {
            request.servletContext.getMimeType(resource?.file?.absolutePath)
        } catch (e: Exception) {
            "application/octet-stream"
        }

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${resource?.filename}\\")
            .body(resource)
    }

    @PostMapping("/save")
    fun saveFile(
        authentication: Authentication,
        @ModelAttribute request: UploadImageRequest,
    ): List<ResourceHrefResponse> {
        Logger.getLogger("fdsf").info("fdsf")
        val catalogs = listOf<String>()
        val response = request.images.mapIndexed { index, multipartFile ->
            val link = filesUploadService.saveFile(catalogs, "${authentication.name}_${System.currentTimeMillis()}_${index}", multipartFile, "media/get")
            ResourceHrefResponse(link)
        }
        return response
    }
}