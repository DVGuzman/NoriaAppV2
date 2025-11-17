package com.example.noriaappv2

import android.util.Log
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish
import java.nio.charset.StandardCharsets
import java.util.UUID




class MqttManager {

    private var client: Mqtt5AsyncClient? = null
    private var isConnecting = false

    fun connect(serverHost: String, clientId: String, connectionCallback: (Boolean) -> Unit) {
        if (client?.state?.isConnectedOrReconnect == true || isConnecting) {
            Log.d("MqttManager", "Ya conectado o conectando.")
            return
        }
        isConnecting = true

        client = MqttClient.builder()
            .useMqttVersion5()
            .identifier(clientId + "-" + UUID.randomUUID())
            .serverHost(serverHost)
            .serverPort(1883)
            .buildAsync()

        client?.connect()
            ?.whenComplete { _, throwable ->
                isConnecting = false
                if (throwable != null) {
                    Log.e("MqttManager", "Fallo en la conexión", throwable)
                    connectionCallback(false)
                } else {
                    Log.d("MqttManager", "Conexión exitosa")
                    connectionCallback(true)
                }
            }
    }

    fun subscribe(topic: String, onMessage: (String) -> Unit) {
        if (client?.state?.isConnected != true) {
            Log.e("MqttManager", "No se puede suscribir, cliente no conectado")
            return
        }

        // Se usa el callback por suscripción, que es más robusto
        client?.subscribeWith()
            ?.topicFilter(topic)
            ?.callback { publish ->
                // Este código se ejecuta cuando llega un mensaje en este topic específico
                val payload = String(publish.payloadAsBytes, StandardCharsets.UTF_8)
                Log.d("MqttManager", "Mensaje recibido en '$topic': $payload")
                onMessage(payload) // Se llama directamente al callback proporcionado
            }
            ?.send()
            ?.whenComplete { _, throwable ->
                if (throwable != null) {
                    Log.e("MqttManager", "Fallo en la suscripción a '$topic'", throwable)
                } else {
                    Log.d("MqttManager", "Suscripción a '$topic' exitosa")
                }
            }
    }

    fun publish(topic: String, message: String) {
        if (client?.state?.isConnected != true) {
            Log.e("MqttManager", "No se puede publicar, cliente no conectado")
            return
        }

        val publishMessage = Mqtt5Publish.builder()
            .topic(topic)
            .payload(message.toByteArray())
            .build()

        client?.publish(publishMessage)
    }

    fun isConnected(): Boolean {
        return client?.state?.isConnected == true
    }
}
