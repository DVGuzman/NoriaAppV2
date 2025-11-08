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

    fun connect() {
        _statusMessage.value = "Conectando..."
        mqttManager.connect("broker.hivemq.com", "NoriaAppClient") { success ->
            _isConnected.value = success
            _statusMessage.value = if (success) "Conectado al broker" else "Error de conexión"
        }
    }

    fun startNoria() {
        if (!mqttManager.isConnected()) {
            _statusMessage.value = "No conectado. Imposible iniciar."
            return
        }
        mqttManager.publish("javiermorenoelmejor", "1")
        _statusMessage.value = "Comando 'Iniciar' enviado"
    }

    fun pauseNoria() {
        if (!mqttManager.isConnected()) {
            _statusMessage.value = "No conectado. Imposible pausar."
            return
        }
        mqttManager.publish("TOGGLE_PAUSE", "pause")
        _statusMessage.value = "Comando 'Pausar' enviado"
    }

    fun stopNoria() {
        if (!mqttManager.isConnected()) {
            _statusMessage.value = "No conectado. Imposible detener."
            return
        }
        mqttManager.publish("javiermorenoelmejor", "0")
        _statusMessage.value = "Comando 'Detener' enviado"
    }

    fun changeNeoPixelColor(color: String) {
        if (!mqttManager.isConnected()) {
            _statusMessage.value = "No conectado. Imposible cambiar color."
            return
        }
        mqttManager.publish("TOGGLE_COLOR", color)
        _statusMessage.value = "Comando 'Cambiar color' enviado"
    }

    fun toggleOLED() {
        if (!mqttManager.isConnected()) {
            _statusMessage.value = "No conectado. Imposible accionar OLED."
            return
        }
        mqttManager.publish("OLED_START", "toggle")
        _statusMessage.value = "Comando 'OLED' enviado"
    }

    fun stopProject() {
        if (!mqttManager.isConnected()) {
            _statusMessage.value = "No conectado. Imposible detener proyecto."
            return
        }
        mqttManager.publish("PROYECT_OFF", "stop")
        _statusMessage.value = "Comando 'Detener proyecto' enviado"
    }
}
