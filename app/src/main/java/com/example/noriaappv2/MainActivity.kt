package com.example.noriaappv2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalAnimationApi::class)
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "main") {
                composable("main") {
                    MainScreen(navController, viewModel)
                }
                composable(
                    "basic_functions",
                    enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) },
                    exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) },
                    popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) },
                    popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) }
                ) {
                    BasicFunctionsScreen(viewModel)
                }
                composable(
                    "secondary_functions",
                    enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) },
                    exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) },
                    popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) },
                    popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) }
                ) {
                    SecondaryFunctionsScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun MainScreen(navController: NavController, viewModel: MainViewModel) {
    LaunchedEffect(Unit) {
        viewModel.connect()
    }

    val statusMessage by viewModel.statusMessage.collectAsState()
    val showPersonCountDialog = remember { mutableStateOf(false) }

    if (showPersonCountDialog.value) {
        PersonCountDialog(viewModel) { showPersonCountDialog.value = false }
    }

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
            Text(
                text = "NoriaApp",
                color = Color.White,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(Color.White.copy(alpha = 0.5f), shape = RoundedCornerShape(20.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = statusMessage,
                    color = Color.Black,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            ControlButton(text = "Funciones básicas Noria") { navController.navigate("basic_functions") }
            Spacer(modifier = Modifier.height(16.dp))
            ControlButton(text = "Funciones secundarias") { navController.navigate("secondary_functions") }
            Spacer(modifier = Modifier.height(16.dp))
            ControlButton(text = "Personas dentro de la noria") { showPersonCountDialog.value = true }
        }
    }
}

@Composable
fun BasicFunctionsScreen(viewModel: MainViewModel) {
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
        Image(
            painter = painterResource(id = R.drawable.florosa2),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.2f
        )
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ControlButton(text = "Iniciar noria") { viewModel.startNoria() }
            Spacer(modifier = Modifier.height(16.dp))
            ControlButton(text = "Pausar noria") { viewModel.pauseNoria() }
            Spacer(modifier = Modifier.height(16.dp))
            ControlButton(text = "Detener noria") { viewModel.stopNoria() }
        }
    }
}

@Composable
fun SecondaryFunctionsScreen(viewModel: MainViewModel) {
    val showColorDialog = remember { mutableStateOf(false) }
    val showMotorSpeedDialog = remember { mutableStateOf(false) }
    val showServoDialog = remember { mutableStateOf(false) } // New state for servo dialog

    if (showColorDialog.value) {
        ColorPickerDialog(viewModel) { showColorDialog.value = false }
    }

    if (showMotorSpeedDialog.value) {
        MotorSpeedDialog(viewModel) { showMotorSpeedDialog.value = false }
    }

    if (showServoDialog.value) {
        ServoDialog(viewModel) { showServoDialog.value = false }
    }

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
        Image(
            painter = painterResource(id = R.drawable.florosa2),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.2f
        )
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ControlButton(text = "Cambiar colores NeoPixel") { showColorDialog.value = true }
            Spacer(modifier = Modifier.height(16.dp))
            ControlButton(text = "Controlar velocidad del motor") { showMotorSpeedDialog.value = true }
            Spacer(modifier = Modifier.height(16.dp))
            ControlButton(text = "Controlar Servomotor") { showServoDialog.value = true } // New button
            Spacer(modifier = Modifier.height(16.dp))
            ControlButton(text = "Iniciar / Detener OLED") { viewModel.toggleOLED() }
            Spacer(modifier = Modifier.height(16.dp))
            ControlButton(text = "Iniciar / Detener proyecto") { viewModel.stopProject() }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ServoDialog(viewModel: MainViewModel, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Control del Servomotor") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        viewModel.openServo()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63))
                ) {
                    Text("Abrir (90°)")
                }
                Button(
                    onClick = {
                        viewModel.closeServo()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63))
                ) {
                    Text("Cerrar (0°)")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63))
            ) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
fun MotorSpeedDialog(viewModel: MainViewModel, onDismiss: () -> Unit) {
    var speed by remember { mutableStateOf(0f) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Control de Velocidad del Motor") },
        text = {
            Column {
                Slider(
                    value = speed,
                    onValueChange = { speed = it },
                    valueRange = 0f..255f,
                    onValueChangeFinished = {
                        viewModel.changeMotorSpeed(speed.toInt())
                    }
                )
                Text(text = "Velocidad: ${speed.toInt()}", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63))
            ) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
fun PersonCountDialog(viewModel: MainViewModel, onDismiss: () -> Unit) {
    val count by viewModel.personCount.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Personas en la Noria") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("Personas que han entrado:", fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "$count", fontSize = 48.sp, fontWeight = FontWeight.Bold)
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63)) // Color rosa
            ) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
fun ColorPickerDialog(viewModel: MainViewModel, onDismiss: () -> Unit) {
    val colors = listOf(
        "Rosa" to "#FFC0CB",
        "Rojo" to "#FF0000",
        "Azul" to "#0000FF",
        "Amarillo" to "#FFFF00",
        "Naranja" to "#FFA500"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Elige un color") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                colors.forEach { (name, hex) ->
                    Button(
                        onClick = {
                            viewModel.changeNeoPixelColor(hex)
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63)) // Color rosa
                    ) {
                        Text(name)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63)) // Color rosa
            ) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
fun ControlButton(text: String, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "pop"
    )

    Button(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .scale(scale.value),
        interactionSource = interactionSource
    ) {
        // Este es un comentario de prueba para verificar el estado de Git
        Text(
            text = text,
            color = Color(0xFFFF80AB),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
