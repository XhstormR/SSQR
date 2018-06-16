package com.xhstormr.ssqr

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 *
 * @author zhangzf
 * @create 2018/6/16 19:56
 */
object App {
    lateinit var args: Array<String>

    @JvmStatic
    fun main(args: Array<String>) {

        if (args.size < 2) return

        this.args = args

        if (args[0] == "screen")
            ScreenCapture.show() else decode(ImageIO.read(File(args[0])))
    }

    fun decode(image: BufferedImage) {
        if (args[1] == "ocr") {
            OCR.decodeOCR(image)
        } else {
            SSQR.decodeQRCode(image)
        }
    }
}
