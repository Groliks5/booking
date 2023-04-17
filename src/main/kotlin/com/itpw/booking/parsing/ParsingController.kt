package com.itpw.booking.parsing

import com.itpw.booking.media.FilesUploadService
import com.itpw.booking.notice.ConditionType
import com.itpw.booking.notice.RoomsCount
import com.itpw.booking.properties.FileStorageProperties
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import org.apache.commons.csv.CSVRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.BufferedReader
import java.io.BufferedWriter
import java.nio.file.Files
import java.util.logging.Logger
import kotlin.io.path.Path

@RestController
@RequestMapping("/parsing")
class ParsingController @Autowired constructor(
    private val parsingService: ParsingService,
    private val fileStorageProperties: FileStorageProperties,
) {
//    @PostMapping("/notices")
//    fun parseNotices(
//        @ModelAttribute request: ParseFileRequest
//    ) {
//        val users = parsingService.parse(request)
//        val printer = CSVPrinter(Files.newBufferedWriter(Path("${fileStorageProperties.uploadDir}/users.csv")), CSVFormat.DEFAULT)
//        users.forEach {
//            printer.printRecord(it.login, it.password)
//        }
//        printer.flush()
//    }

    @GetMapping("/normalize_image")
    fun normalizeImage() {
        parsingService.normalizeImages()
    }
}