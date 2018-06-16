package com.xhstormr.ssqr

import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.WindowEvent
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.WindowConstants

object ScreenCapture {
    private var first = true

    private val p1 = Point()
    private val p2 = Point()
    private val screenImage = Robot().createScreenCapture(Rectangle(Toolkit.getDefaultToolkit().screenSize))

    private val frame = JFrame().apply {
        this.isAlwaysOnTop = true
        this.isUndecorated = true
        this.extendedState = JFrame.MAXIMIZED_BOTH
        this.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        this.cursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR)
    }

    private val label = object : JLabel(ImageIcon(screenImage)) {
        override fun paintComponent(g: Graphics) {
            super.paintComponent(g)
            g.color = Color.RED
            g.drawLine(p1.x, 0, p1.x, height)
            g.drawLine(0, p1.y, width, p1.y)
            if (!first) {
                g.drawLine(p2.x, 0, p2.x, height)
                g.drawLine(0, p2.y, width, p2.y)
            }
            g.dispose()
        }
    }

    private val mouseAdapter = object : MouseAdapter() {
        override fun mouseMoved(e: MouseEvent) {
            updatePoint(p1, e)
        }

        override fun mouseDragged(e: MouseEvent) {
            if (first) {
                first = !first
            } else {
                updatePoint(p2, e)
            }
        }

        override fun mouseReleased(e: MouseEvent) {
            if (!first) {
                first = !first
                scan()
                updatePoint(p1, e)
            }
        }

        override fun mouseClicked(e: MouseEvent) {
            // 鼠标右键退出程序
            if (e.button == MouseEvent.BUTTON3) {
                frame.dispatchEvent(WindowEvent(frame, WindowEvent.WINDOW_CLOSING))
            }
        }
    }

    init {
        label.addMouseListener(mouseAdapter)
        label.addMouseMotionListener(mouseAdapter)
        frame.add(label)
    }

    fun show() {
        frame.isVisible = true
    }

    private fun scan() {
        val minX = Math.min(p1.x, p2.x)
        val minY = Math.min(p1.y, p2.y)
        val maxX = Math.max(p1.x, p2.x)
        val maxY = Math.max(p1.y, p2.y)
        val w = maxX - minX
        val h = maxY - minY

        if (w < 1 || h < 1) {
            return
        }

        try {
            App.decode(screenImage.getSubimage(minX, minY, w, h))
        } catch (e: Exception) {
            println(e.toString().substringAfterLast('.'))
            return
        }

        frame.dispatchEvent(WindowEvent(frame, WindowEvent.WINDOW_CLOSING))
    }

    private fun updatePoint(p: Point, e: MouseEvent) {
        p.move(e.x, e.y)
        label.repaint()
    }
}
