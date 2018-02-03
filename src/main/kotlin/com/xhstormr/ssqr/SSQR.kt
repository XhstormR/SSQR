package com.xhstormr.ssqr

import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.common.GlobalHistogramBinarizer
import com.google.zxing.qrcode.QRCodeReader
import java.io.File
import java.util.*
import javax.imageio.ImageIO

private object SSQR {
    private val HINTS: Map<DecodeHintType, Any> = mapOf(
//          DecodeHintType.PURE_BARCODE.to(true),
            DecodeHintType.CHARACTER_SET.to("UTF-8"),
            DecodeHintType.POSSIBLE_FORMATS.to(listOf(BarcodeFormat.QR_CODE))
    )

    @JvmStatic
    fun main(args: Array<String>) {

        if (args.isEmpty()) return

        val list = decodeQRCode(args[0])
                .substring(5)
                .decodeBase64()
                .trim()
                .split(':')

        val split = list[1].split('@')

        val method = list[0]
        val port = list[2]
        val password = split[0]
        val address = split[1]

        println("""
                address:  $address
                port:     $port
                method:   $method
                password: $password
                """.trimIndent()
        )
    }

    private fun decodeQRCode(path: String): String {
        val file = File(path)
        val image = ImageIO.read(file)

        val bitmap = BinaryBitmap(GlobalHistogramBinarizer(BufferedImageLuminanceSource(image)))

        val reader = QRCodeReader()
        val result = reader.decode(bitmap, HINTS)

        image.flush()
        return result.text
    }

    private fun String.decodeBase64() = Base64.getDecoder().decode(this).toString(Charsets.UTF_8)
}
