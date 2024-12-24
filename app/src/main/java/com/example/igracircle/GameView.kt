package com.example.igracircle

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.random.Random

class GameView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private val circles = mutableListOf<Circle>()
    private var selectedCircle: Circle? = null
    private var offsetX = 0f
    private var offsetY = 0f

    private val hole = Circle(
        x = 400f,
        y = 800f,
        radius = 100f,
        color = Color.RED
    )

    private val paint = Paint().apply { isAntiAlias = true }

    private var gameOver = false

    init {
        generateCircles()
    }

    private fun generateCircles() {
        val colors = listOf(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.MAGENTA)
        post {
            val padding = 60f
            repeat(5) {
                circles.add(
                    Circle(
                        x = Random.nextFloat() * (width - 2 * padding) + padding,
                        y = Random.nextFloat() * (height - 2 * padding) + padding,
                        radius = 60f,
                        color = colors[it % colors.size]
                    )
                )
            }
            hole.color = circles.firstOrNull()?.color ?: Color.BLACK
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (gameOver) {
            paint.color = Color.BLACK
            paint.textSize = 60f
            canvas.drawText("Игра окончена!", width / 2f - 200f, height / 2f, paint)
            return
        }

        paint.color = hole.color
        canvas.drawRect(
            hole.x - hole.radius,
            hole.y - hole.radius,
            hole.x + hole.radius,
            hole.y + hole.radius,
            paint
        )

        circles.forEach {
            paint.color = it.color
            canvas.drawCircle(it.x, it.y, it.radius, paint)
        }
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (gameOver) return false

        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                selectedCircle = circles.find {
                    Math.hypot((it.x - x).toDouble(), (it.y - y).toDouble()) <= it.radius
                }
                selectedCircle?.let {
                    offsetX = x - it.x
                    offsetY = y - it.y
                }
            }
            MotionEvent.ACTION_MOVE -> {
                selectedCircle?.let {
                    it.x = x - offsetX
                    it.y = y - offsetY
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                selectedCircle?.let {
                    if (isInHole(it)) {
                        circles.remove(it)
                        if (circles.isEmpty()) {
                            gameOver = true
                        } else {
                            hole.color = circles.first().color
                        }
                    }
                }
                selectedCircle = null
                invalidate()
            }
        }
        return true
    }

    private fun isInHole(circle: Circle): Boolean {
        return Math.abs(circle.x - hole.x) <= hole.radius &&
                Math.abs(circle.y - hole.y) <= hole.radius &&
                circle.color == hole.color
    }
}