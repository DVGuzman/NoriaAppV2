package com.example.noriaappv2

import android.util.Log
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.MqttGlobalPublishFilter
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish
import com.hivemq.client.mqtt.mqtt5.message.subscribe.Mqtt5Subscribe
import java.nio.charset.StandardCharsets
import java.util.UUID
import java.util.function.Consumer

class MqttManager {

    private var client: Mqtt5AsyncClient? = null
    private var isConnecting = false

    fun connect(
        serverHost: String,
        clientId: String,
        connectionCallback: (Boolean) -> Unit,
        messageCallback: (String, String) -> Unit // topic, payload
    ) {
        if (client?.state?.isConnectedOrReconnect == true || isConnecting) {
            Log.d("MqttManager", "Already connected or connecting.")
            return
        }
        isConnecting = true

        client = MqttClient.builder()
            .useMqttVersion5()
            .identifier(clientId + "-" + UUID.randomUUID())
            .serverHost(serverHost)
            .serverPort(1883)
            .buildAsync()

        // --- INICIO DE LA CORRECCIÓN ---
        // El listener se asigna al cliente DESPUÉS de crearlo
        client?.publishes(MqttGlobalPublishFilter.ALL, Consumer { publish: Mqtt5Publish ->
            val topic = publish.topic.toString()
            val payload = publish.payload.map { StandardCharsets.UTF_8.decode(it).toString() }.orElse("")
            Log.d("MqttManager", "Received message on topic '$topic': $payload")
            messageCallback(topic, payload)
        })
        // --- FIN DE LA CORRECCIÓN ---

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

    fun subscribe(topic: String) {
        if (client?.state?.isConnected != true) {
            Log.e("MqttManager", "Cannot subscribe, client not connected")
            return
        }
        client?.subscribe(
            Mqtt5Subscribe.builder()
                .topicFilter(topic)
                .build()
        )?.whenComplete { _, throwable ->
            if (throwable != null) {
                Log.e("MqttManager", "Subscription to '$topic' failed", throwable)
            } else {
                Log.d("MqttManager", "Subscription to '$topic' successful")
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
