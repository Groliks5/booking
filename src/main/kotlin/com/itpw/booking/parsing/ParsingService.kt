package com.itpw.booking.parsing

import com.itpw.booking.additional_feature.AdditionalFeatureService
import com.itpw.booking.condition.ConditionService
import com.itpw.booking.media.FilesUploadService
import com.itpw.booking.notice.*
import com.itpw.booking.properties.FileStorageProperties
import com.itpw.booking.user.EditUserRequest
import com.itpw.booking.user.RegisterUserRequest
import com.itpw.booking.user.UserService
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.juli.logging.Log
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.logging.Logger
import javax.imageio.ImageIO

@Service
class ParsingService @Autowired constructor(
    private val filesUploadService: FilesUploadService,
    private val fileStorageProperties: FileStorageProperties,
    private val userService: UserService,
    private val additionalFeatureService: AdditionalFeatureService,
    private val conditionService: ConditionService,
    private val noticeService: NoticeService
) {
    fun parse(request: ParseFileRequest): List<CreateUserCreditianals> {
        val parser = CSVParser(request.file.inputStream.bufferedReader(), CSVFormat.DEFAULT)
        val users = mutableListOf<ParsedUser>()
        var isShowLog = true
        for ((index, row) in parser.withIndex()) {
            val socialMedia: List<String> = row[2].split(',').map { it.filterNot { it.isWhitespace() || it == '\'' || it == '[' || it == ']' } }
            val user = ParsedUser(
                name = row[1],
                viber = socialMedia.firstOrNull { it.startsWith("viber://") },
                whatsApp = socialMedia.firstOrNull { it.startsWith("https://wa.me") },
                telegram = socialMedia.firstOrNull { it.startsWith("https://t.me") },
                vk = socialMedia.firstOrNull { it.startsWith("https://vk.com") },
                phone = row[8].filter { it.isDigit() },
            )
            val prices = row[0].split("),")
                .map { it.filterNot { it == '\'' || it == '(' || it.isWhitespace() || it == '[' || it == ']' || it == ')' } }
                .map {
                    val (type, price) = it.split(',')
                    type to price
                }
            val conditions =
                row[10].split("', ").map { it.filterNot { it == '[' || it == ']' || it == '\'' }.replace("\\n", "") }.filterNot { it == "None" }
            val additionalFeatures =
                row[11].split("', ").map { it.filterNot { it == '[' || it == ']' || it == '\'' }.replace("\\n", "") }.filterNot { it == "None" }
            val deposit = conditions.firstOrNull { it.lowercase().contains("залог") }?.let {
                if (it == "Нужен залог") {
                    ConditionType.YES
                } else if (it == "Без залога") {
                    ConditionType.NO
                } else {
                    ConditionType.BY_AGREEMENT
                }
            }
            val prePayment = conditions.firstOrNull { it.lowercase().contains("предоплат") }?.let {
                if (it == "Нужна предоплата") {
                    ConditionType.YES
                } else if (it == "Без предоплаты") {
                    ConditionType.NO
                } else {
                    ConditionType.BY_AGREEMENT
                }
            }
            val (floor, maxFloor) = if (row[7].isNotBlank()) {
                val floors: MutableList<Int?> =
                    row[7].split('/').map { it.filter { it.isDigit() }.toInt() }.toMutableList()
                if (floors.size == 1) {
                    floors.add(null)
                }
                floors
            } else {
                listOf<Int?>(null, null)
            }
            val notice = ParsedNotice(
                address = row[4],
                roomsCount = when (row[5].first()) {
                    '1' -> RoomsCount.ONE
                    '2' -> RoomsCount.TWO
                    '3' -> RoomsCount.THREE
                    else -> RoomsCount.FOUR_OR_MORE
                },
                title = row[3],
                images = row[12].split(',').filterNot { it.contains(".webp") }
                    .map { it.filterNot { it == '\'' || it == '[' || it == ']' || it.isWhitespace() } }
                    .filterNot { it.isBlank() },
                description = row[9].replace("<p>", "").replace("<br/>", "").replace("</p>", "").replace("<br>", "")
                    .let {
                        if (it.contains("<p class")) {
                            ""
                        } else {
                            it
                        }
                    },
                latitude = row[13].toDouble(),
                longitude = row[14].toDouble(),
                pricePerDay = prices.firstOrNull { it.first == "Сутки" }?.second?.toDouble()!!,
                pricePerHour = prices.firstOrNull { it.first == "Час" }?.second?.toDouble(),
                pricePerNight = prices.firstOrNull { it.first == "Ночь" }?.second?.toDouble(),
                deposit = deposit,
                prePayment = prePayment,
                conditions = conditions.filterNot {
                    it.lowercase().contains("залог") || it.lowercase().contains("предоплат")
                },
                additionalFeatures = additionalFeatures,
                square = row[6].filter { it.isDigit() }.toDoubleOrNull(),
                floor = floor,
                maxFloor = maxFloor
            )
            users.firstOrNull { it.phone == user.phone }?.also {
                it.notices.add(notice)
            } ?: kotlin.run {
                user.notices.add(notice)
                users.add(user)
            }
        }
        return users.mapIndexed {index, it ->
//            var login = generateString(10)
//            while (userService.isUserExists(login)) {
//                login = generateString(10)
//            }
//            val password = generateString(10)
//            var user = userService.registerUser(
//                RegisterUserRequest(
//                    login = login,
//                    password = password,
//                    phone = it.phone,
//                )
//            )
//            user = userService.editUser(
//                user.id,
//                EditUserRequest(
//                    name = it.name,
//                    phone = it.phone,
//                    whatsApp = it.whatsApp,
//                    telegram = it.telegram,
//                    viber = it.viber,
//                    vk = it.vk,
//                    email = null
//                )
//            )
//            it.notices.forEach {
//                val conditions = it.conditions.map {
//                    conditionService.getOrCreateCondition(it)
//                }
//                val features = it.additionalFeatures.map {
//                    additionalFeatureService.getOrCreateAdditionalFeature(it)
//                }
//                noticeService.createNotice(
//                    userId = user.id,
//                    request = CreateNoticeRequest(
//                        title = it.title,
//                        address = it.address,
//                        extraInfo = it.description,
//                        deposit = it.deposit,
//                        prePayment = it.prePayment,
//                        floor = it.floor,
//                        maxFloorInEntrance = it.maxFloor,
//                        images = it.images.mapIndexed { index, it ->
//                            NoticeImageRequest(
//                                href = it,
//                                position = index
//                            )
//                        },
//                        latitude = it.latitude,
//                        longitude = it.longitude,
//                        pricePerDay = it.pricePerDay,
//                        pricePerNight = it.pricePerNight,
//                        pricePerHour = it.pricePerHour,
//                        roomsCount = it.roomsCount,
//                        square = it.square,
//                        selectedAdditionalFeatures = features.map { it.id },
//                        selectedConditions = conditions.map { it.id }
//                    )
//                )
//            }
            it.notices.forEach {
                it.images.forEach {
                    filesUploadService.downloadFile(it)
                }
            }
            CreateUserCreditianals("login", "password")
        }
    }

    private fun generateString(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        var str = (1..length)
            .map { allowedChars.random() }
            .joinToString("")
        return str
    }

    fun normalizeImages() {
        val mediaFolder = File(fileStorageProperties.uploadDir)
        for ((index, file) in mediaFolder.listFiles()!!.withIndex()) {
            if (index % 1000 == 0) {
                Logger.getLogger("index").info(index.toString())
            }
            try {
                if (file.isFile) {
                    val image = ImageIO.read(file)
                    if (image.width != 1350 || image.height != 900) {
                        val fileType = file.name.split('.').last()
                        val image = filesUploadService.makeImage(file.inputStream(), fileType)
                        Files.copy(image, Path.of(file.toURI()), StandardCopyOption.REPLACE_EXISTING)
                    }
                }
            } catch (e: Exception) {
                Logger.getLogger("error file").info(file.path + "/" + file.name)
                Logger.getLogger("error file").info(e.message)
            }
        }
    }
}