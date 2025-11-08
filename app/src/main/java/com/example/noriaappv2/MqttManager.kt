package com.example.noriaappv2

import android.util.Log
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish
import java.util.UUID

class MqttManager {

    private var client: Mqtt5AsyncClient? = null
    private var isConnecting = false

    fun connect(serverHost: String, clientId: String, connectionCallback: (Boolean) -> Unit) {
        if (client?.state?.isConnectedOrReconnect == true || isConnecting) {
            Log.d("MqttManager", "Already connected or connecting.")
            return
        }
        isConnecting = true

        client = MqttClient.builder()
            .useMqttVersion5()
            .identifier(clientId + "-" + UUID.randomUUID()) // Add UUID to ensure unique client ID
            .serverHost(serverHost)
            .serverPort(1883)
            .buildAsync()

        client?.connect()
            ?.whenComplete { _, throwable ->
                isConnecting = false
                if (throwable != null) {
                    Log.e("MqttManager", "Connection failure", throwable)
                    connectionCallback(false)
                } else {
                    Log.d("MqttManager", "Connection success")
                    connectionCallback(true)
                }
            }
    }

    fun publish(topic: String, message: String) {
        if (client?.state?.isConnected != true) {
            Log.e("MqttManager", "Cannot publish, client not connected")
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