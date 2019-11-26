package com.xhstormr.ssqr

import com.google.gson.JsonParser
import java.awt.image.BufferedImage
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID
import javax.imageio.ImageIO

/**
 *
 * @author zhangzf
 * @create 2018/6/16 17:57
 */
object OCR {
    private const val URL = "https://api.ocr.space/parse/image"
    private const val NEWLINE = "\n"
    private const val FILELINE = """Content-Disposition: form-data; filename="123.png"$NEWLINE$NEWLINE"""

    fun decodeOCR(image: BufferedImage) {
        val boundary = UUID.randomUUID().toString()
        val boundaryLine = "--$boundary$NEWLINE"

        val connection = URL(URL).openConnection() as HttpURLConnection

        connection.doInput = true
        connection.doOutput = true
        connection.requestMethod = "POST"
        connection.setRequestProperty("apikey", "webocr5")
        connection.setRequestProperty("Content-Type", "multipart/form-data; charset=UTF-8; boundary=$boundary")

        connection.outputStream.use {
            it.write(boundaryLine.toByteArray())
            sendFile(image, it)
            it.write(boundaryLine.toByteArray())
        }
        val jsonElement = JsonParser.parseReader(connection.inputStream.bufferedReader())
        connection.disconnect()

        val text = jsonElement.asJsonObject
            .getAsJsonArray("ParsedResults")[0].asJsonObject
            .getAsJsonPrimitive("ParsedText").asString
        println(text)
    }

    private fun sendFile(image: BufferedImage, out: OutputStream) {
        out.write(FILELINE.toByteArray())
//      Files.copy(Path.of("123.png"), out)
        ImageIO.write(image, "png", out)
        out.write(NEWLINE.toByteArray())
    }
}
