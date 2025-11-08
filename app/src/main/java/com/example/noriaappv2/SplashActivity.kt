package com.example.noriaappv2

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

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
        }, 2000)
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
        Text(
            text = "✨ Bienvenida a NoriaApp ✨",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
    }
}