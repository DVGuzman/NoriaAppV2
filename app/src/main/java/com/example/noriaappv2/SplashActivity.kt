package com.example.noriaappv2

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SplashScreen()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 3000)
    }
}

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFB6C1),
                        Color(0xFFFF69B4)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        FerrisWheelIcon()
    }
}

@Composable
fun FerrisWheelIcon() {
    val infiniteTransition = rememberInfiniteTransition(label = "wheel_rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Canvas(modifier = Modifier.size(150.dp)) { 
        val strokeWidth = 8f
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2 - strokeWidth

        // --- Dibuja el soporte (Ahora corregido) ---
        val supportBaseOffset = radius * 0.8f
        drawLine(Color.White, start = Offset(center.x - supportBaseOffset, center.y + radius), end = center, strokeWidth = strokeWidth)
        drawLine(Color.White, start = Offset(center.x + supportBaseOffset, center.y + radius), end = center, strokeWidth = strokeWidth)

        // --- Rota el lienzo para animar la rueda ---
        rotate(rotation, pivot = center) {
            // Círculo exterior
            drawCircle(
                color = Color.White,
                radius = radius,
                center = center,
                style = Stroke(width = strokeWidth)
            )

            // Radios de la rueda
            for (i in 0 until 8) {
                val angle = i * (360f / 8f)
                val radians = Math.toRadians(angle.toDouble()).toFloat()
                val endX = center.x + kotlin.math.cos(radians) * radius
                val endY = center.y + kotlin.math.sin(radians) * radius
                drawLine(
                    color = Color.White,
                    start = center,
                    end = Offset(endX, endY),
                    strokeWidth = strokeWidth / 2
                )
            }

            // Círculo interior
            drawCircle(
                color = Color.White,
                radius = radius / 3,
                center = center
            )
        }
    }
}
