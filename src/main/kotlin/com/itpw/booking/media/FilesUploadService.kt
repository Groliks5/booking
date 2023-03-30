package com.itpw.booking.media

import com.itpw.booking.exceptions.DetailException
import com.itpw.booking.exceptions.NotFoundException
import com.itpw.booking.properties.FileStorageProperties
import com.itpw.booking.properties.ProxyProperties
import com.itpw.booking.util.Translator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.io.IOException
import java.net.MalformedURLException
import java.net.URI
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.logging.Logger
import kotlin.io.path.deleteIfExists


@Service
class FilesUploadService @Autowired constructor(
    private val fileStorageProperties: FileStorageProperties,
    private val proxyProperties: ProxyProperties,
    private val translator: Translator
) {
    private var fileStorageLocation: Path = Paths.get(fileStorageProperties.uploadDir).toAbsolutePath().normalize()

    init {
        fileStorageLocation = Paths.get(fileStorageProperties.uploadDir).toAbsolutePath().normalize()
        try {
            Files.createDirectories(fileStorageLocation)
        } catch (e: Exception) {
            throw e
        }
    }

    fun saveFile(catalogs: List<String>, targetFileName: String, file: MultipartFile, requestPath: String): String {
        val fileType = file.originalFilename!!.split('.').last()
        val targetPath = catalogs.fold(fileStorageLocation) { path, catalog -> path.resolve(catalog) }
        val fileName: String = StringUtils.cleanPath("$targetFileName.$fileType")
        Files.createDirectories(targetPath)
        try {
            if (fileName.contains("..")) {
                throw DetailException(translator.toLocale("wrong_file_name"))
            }

            val targetLocation: Path = targetPath.resolve(fileName)
            Files.copy(file.inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING)
            return ServletUriComponentsBuilder.fromUri(URI.create(proxyProperties.uri))
                .path("$requestPath/${catalogs.joinToString("/")}/$targetFileName.$fileType")
                .toUriString()
        } catch (e: IOException) {
            throw e
        }
    }

    fun loadFileAsResource(catalogs: List<String>, fileName: String): Resource? {
        return try {
            val filePath = catalogs.fold(fileStorageLocation) { path, catalog -> path.resolve(catalog) }.resolve(fileName).normalize()
            val resource: Resource = UrlResource(filePath.toUri())
            if (resource.exists()) {
                resource
            } else {
                throw NotFoundException(translator.toLocale("file_not_found"))
            }
        } catch (ex: MalformedURLException) {
            throw NotFoundException(translator.toLocale("file_not_found"))
        }
    }

    fun downloadFile(url: String) {
        val targetFileName = url.split("/").takeLast(2).joinToString("_")
        if (!url.filterNot { it.isWhitespace() }.startsWith("https")) {
            Logger.getLogger("fdsf").info(url)
        }
        val stream = URL(url).openStream()
        val targetPath = listOf<String>().fold(fileStorageLocation) { path, catalog -> path.resolve(catalog) }
        val fileName: String = StringUtils.cleanPath("$targetFileName")
        Files.createDirectories(targetPath)
        try {
            if (fileName.contains("..")) {
                throw DetailException(translator.toLocale("wrong_file_name"))
            }

            val targetLocation: Path = targetPath.resolve(fileName)
            Files.copy(stream, targetLocation, StandardCopyOption.REPLACE_EXISTING)
        } catch (e: Exception) {

        }
    }

    fun deleteFile(catalogs: List<String>, fileName: String) {
        try {
            val filePath = catalogs.fold(fileStorageLocation) { path, catalog -> path.resolve(catalog) }.resolve(fileName).normalize()
            filePath.deleteIfExists()
        } catch (ex: MalformedURLException) {
            throw NotFoundException("Файл не найден")
        }
    }

    fun deleteFile(href: String) {
        val pathParts = href.split('/').dropWhile { it != "media" }.drop(1)
        val catalogs = pathParts.dropLast(1)
        val file = pathParts.last()
        deleteFile(catalogs, file)
    }

    fun moveFile(fromCatalogs: List<String>, toCatalogs: List<String>, fileName: String): String {
        try {
            val sourceFile = fromCatalogs.fold(fileStorageLocation) { path, catalog -> path.resolve(catalog) }.resolve(fileName).normalize()
            val targetDirectory = toCatalogs.fold(fileStorageLocation) { path, catalog -> path.resolve(catalog) }
            val targetFile = targetDirectory.resolve(fileName).normalize()
            Files.createDirectories(targetDirectory)
            Files.copy(sourceFile, targetFile)
            deleteFile(fromCatalogs, fileName)
            return "${targetDirectory.joinToString("/")}/$fileName"
        } catch (ex: MalformedURLException) {
            throw DetailException("Файл не найден")
        }
    }

    fun getFileUploadPathForUri(): String {
        return fileStorageProperties.uploadDir.split('/').dropWhile { it != "media" }.joinToString(separator = "/")
    }

    enum class CATALOG (val catalog: String) {
        IMAGES("images")
    }
}