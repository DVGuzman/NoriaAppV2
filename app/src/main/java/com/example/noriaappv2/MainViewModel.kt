package com.example.noriaappv2

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel() {

    private val mqttManager = MqttManager()

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    private val _statusMessage = MutableStateFlow("Desconectado")
    val statusMessage: StateFlow<String> = _statusMessage

    private val _personCount = MutableStateFlow(0)
    val personCount: StateFlow<Int> = _personCount

    private var isOledOn = false // Estado para controlar el OLED

    fun connect() {
        _statusMessage.value = "Conectando..."
        mqttManager.connect("broker.hivemq.com", "NoriaAppClient") { success ->
            _isConnected.value = success
            if (success) {
                _statusMessage.value = "Conectado y esperando datos"
                mqttManager.subscribe("sensor_Noria") { payload ->
                    // Se espera un payload en formato JSON como {"personas": 5}
                    val countString = payload.substringAfter(":").substringBefore("}").trim()
                    val count = countString.toIntOrNull()
                    count?.let {
                        _personCount.value = it
                    }
                }
            } else {
                _statusMessage.value = "Error en la conexión con el broker"
            }
        }
    }

    // --- CAMBIOS A JSON ---

    private fun sendJsonCommand(command: String, value: String? = null) {
        val json = if (value != null) {
            "{\"comando\": \"$command\", \"valor\": \"$value\"}"
        } else {
            "{\"comando\": \"$command\"}"
        }
        mqttManager.publish("noria/comandos", json)
    }

    fun startNoria() {
        sendJsonCommand("iniciar")
    }

    fun pauseNoria() {
        sendJsonCommand("pausar")
    }

    fun stopNoria() {
        sendJsonCommand("detener")
    }

    fun changeNeoPixelColor(colorHex: String) {
        val cleanHex = colorHex.removePrefix("#")
        sendJsonCommand("color", cleanHex)
    }

    fun toggleOLED() {
        isOledOn = !isOledOn // Cambia el estado
        val oledCommand = if (isOledOn) "iniciar" else "detener"
        sendJsonCommand("oled", oledCommand)
    }

    fun stopProject() {
        sendJsonCommand("parada_emergencia")
    }

    fun changeMotorSpeed(speed: Int) {
        sendJsonCommand("velocidad_motor", speed.toString())
    }

    fun openServo() {
        sendJsonCommand("servo", "abrir")
    }

    fun closeServo() {
        sendJsonCommand("servo", "cerrar")
    }
}
