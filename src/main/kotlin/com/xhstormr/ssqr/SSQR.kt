package com.xhstormr.ssqr

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.common.GlobalHistogramBinarizer
import com.google.zxing.qrcode.QRCodeReader
import com.xhstormr.ssqr.App.args
import java.awt.image.BufferedImage
import java.util.*

object SSQR {
    private val HINTS: Map<DecodeHintType, Any> = mapOf(
            DecodeHintType.PURE_BARCODE to true,
            DecodeHintType.CHARACTER_SET to "UTF-8",
            DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE)
    )
    private val GSON: Gson = GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create()

    fun decodeQRCode(image: BufferedImage) {

        val bitmap = BinaryBitmap(GlobalHistogramBinarizer(BufferedImageLuminanceSource(image)))

        val reader = QRCodeReader()
        val result = reader.decode(bitmap, HINTS).text

        image.flush()

        when {
            args.contains("-ss") -> parseShadowsocks(result)
            args.contains("-v2ray") -> parseV2Ray(result)
            else -> println(result)
        }
    }

    private fun parseV2Ray(text: String) {
        val json = text
                .substring(8)
                .decodeBase64()
                .trim()

        val server = GSON.fromJson(json, Any::class.java)

        println(GSON.toJson(server))
    }

    private fun parseShadowsocks(text: String) {
        val list = text
                .substring(5)
                .decodeBase64()
                .trim()
                .split(':')

        val split = list[1].split('@')

        val method = list[0]
        val port = list[2]
        val password = split[0]
        val address = split[1]

        if (args.contains("-json")) {
            val server = ShadowsocksServer(
                    address,
                    port.toInt(),
                    method,
                    password
            )
            println(GSON.toJson(server))
        } else {
            println("""
                address:  $address
                port:     $port
                method:   $method
                password: $password
                """.trimIndent()
            )
        }
    }

    private fun String.decodeBase64() = Base64.getDecoder().decode(this).toString(Charsets.UTF_8)
}
