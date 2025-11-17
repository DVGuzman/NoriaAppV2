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

    fun connect() {
        _statusMessage.value = "Conectando..."
        mqttManager.connect("broker.hivemq.com", "NoriaAppClient") { success ->
            _isConnected.value = success
            if (success) {
                _statusMessage.value = "Conectado y esperando datos"
                mqttManager.subscribe("sensor_Noria") { payload ->
                    val count = payload.toIntOrNull() ?: 0
                    _personCount.value = count
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
        sendJsonCommand("color", colorHex)
    }

    fun toggleOLED() {
        sendJsonCommand("oled")
    }

    fun stopProject() {
        sendJsonCommand("parada_emergencia")
    }

    fun changeMotorSpeed(speed: Int) {
        sendJsonCommand("velocidad_motor", speed.toString())
    }
}
