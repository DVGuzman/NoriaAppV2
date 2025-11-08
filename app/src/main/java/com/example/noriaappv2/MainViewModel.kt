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
        val turns = "5"
        mqttManager.publish("javiermorenoelmejor", turns)
        _statusMessage.value = "Comando 'Iniciar' enviado"
    }

    fun pauseNoria() {
        if (!mqttManager.isConnected()) {
            _statusMessage.value = "No conectado. Imposible pausar."
            return
        }
        mqttManager.publish("noria/pause", "pause")
        _statusMessage.value = "Comando 'Pausar' enviado"
    }

    fun stopNoria() {
        if (!mqttManager.isConnected()) {
            _statusMessage.value = "No conectado. Imposible detener."
            return
        }
        mqttManager.publish("noria/stop", "stop")
        _statusMessage.value = "Comando 'Detener' enviado"
    }
}
