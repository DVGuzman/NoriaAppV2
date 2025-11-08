package com.example.noriaappv2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen(viewModel)
        }
    }
}

@Composable
fun MainScreen(viewModel: MainViewModel) {
    LaunchedEffect(Unit) {
        viewModel.connect()
    }

    val isConnected by viewModel.isConnected.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFB6C1), Color(0xFFFF69B4))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "NoriaApp",
                    color = Color.White,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(if (isConnected) Color.Green else Color.Red)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = statusMessage,
                color = Color.White,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(0.9f)
            )
            Spacer(modifier = Modifier.height(24.dp))
            ControlButton(text = "Iniciar noria") { viewModel.startNoria() }
            Spacer(modifier = Modifier.height(16.dp))
            ControlButton(text = "Pausar noria") { viewModel.pauseNoria() }
            Spacer(modifier = Modifier.height(16.dp))
            ControlButton(text = "Detener noria") { viewModel.stopNoria() }
        }
    }
}

@Composable
fun ControlButton(text: String, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = animateFloatAsState(if (isPressed) 0.95f else 1f)

    Button(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .scale(scale.value),
        interactionSource = interactionSource
    ) {
        Text(
            text = text,
            color = Color(0xFFFF80AB),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
