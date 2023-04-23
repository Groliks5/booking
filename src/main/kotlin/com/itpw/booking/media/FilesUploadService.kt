package com.itpw.booking.media

import com.itpw.booking.exceptions.DetailException
import com.itpw.booking.exceptions.NotFoundException
import com.itpw.booking.properties.FileStorageProperties
import com.itpw.booking.properties.ProxyProperties
import com.itpw.booking.util.Translator
import org.imgscalr.Scalr
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.awt.image.BufferedImageOp
import java.awt.image.ConvolveOp
import java.awt.image.Kernel
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.MalformedURLException
import java.net.URI
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.logging.Logger
import javax.imageio.ImageIO
import kotlin.io.path.deleteIfExists
import kotlin.math.min


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
            Files.copy(makeImage(file.inputStream, fileType), targetLocation, StandardCopyOption.REPLACE_EXISTING)
            return ServletUriComponentsBuilder.fromUri(URI.create(proxyProperties.uri))
                .path("$requestPath/${catalogs.joinToString("/")}/$targetFileName.$fileType")
                .toUriString()
        } catch (e: IOException) {
            throw e
        }
    }

    fun makeImage(input: InputStream, fileType: String): ByteArrayInputStream {
        var sourceImage = ImageIO.read(input)
        val resizedImage = Scalr.resize(sourceImage, Scalr.Method.AUTOMATIC, Scalr.Mode.FIT_EXACT, 1350, 900)
        val bluredImage = blur(resizedImage, 25)
        if (sourceImage.height != 900 || sourceImage.width != 1350) {
            var heightCoefficient = 900f / sourceImage.height
            var widthCoefficient = 1350f / sourceImage.width
            var coefficient = min(heightCoefficient, widthCoefficient)
            sourceImage = Scalr.resize(sourceImage, Scalr.Method.AUTOMATIC, Scalr.Mode.FIT_EXACT, (sourceImage.width * coefficient).toInt(), (sourceImage.height * coefficient).toInt())
        }
        addImage(bluredImage, sourceImage, 1f, 675 - sourceImage.width / 2, 450 - sourceImage.height / 2)
        val byteArray = ByteArrayOutputStream()
        ImageIO.write(bluredImage, fileType, byteArray)
        return ByteArrayInputStream(byteArray.toByteArray())
    }

    private fun addImage(
        buff1: BufferedImage, buff2: BufferedImage,
        opaque: Float, x: Int, y: Int
    ) {
        val g2d = buff1.createGraphics()
        g2d.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opaque)
        g2d.drawImage(buff2, x, y, null)
        g2d.dispose()
    }

    fun blur(source: BufferedImage, radius: Int): BufferedImage {
        val img = BufferedImage(
            source.width + radius
                    * 2, source.height + radius * 2,
            BufferedImage.TYPE_4BYTE_ABGR
        )
        val g2 = img.graphics as Graphics2D
        g2.color = Color(0, 0, 0)
        g2.fillRect(
            0, 0, source.width + radius * 2,
            source.height + radius * 2
        )
        g2.drawImage(source, radius, radius, null)
        g2.dispose() //w w w . ja  va2 s . c  o m
        val square = radius * radius
        var sum = 0f
        val matrix = FloatArray(square)
        for (i in 0 until square) {
            val dx = i % radius - radius / 2
            val dy = i / radius - radius / 2
            matrix[i] = (radius - Math.sqrt((dx * dx + dy * dy).toDouble())).toFloat()
            sum += matrix[i]
        }
        for (i in 0 until square) {
            matrix[i] /= sum
        }
        val op: BufferedImageOp = ConvolveOp(
            Kernel(
                radius, radius,
                matrix
            ), ConvolveOp.EDGE_ZERO_FILL, null
        )
        val res = op.filter(img, null)
        val out = BufferedImage(
            source.width,
            source.height, source.type
        )
        val g3 = out.graphics as Graphics2D
        g3.drawImage(res, -radius, -radius, null)
        g3.dispose()
        return out
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