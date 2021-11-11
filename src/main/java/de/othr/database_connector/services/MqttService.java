package de.othr.database_connector.services;
/*
Copyright 2021 Thomas Pilz

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.othr.database_connector.kpi.KpiMsg;
import org.eclipse.paho.mqttv5.client.*;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * MQTT Service to connect to a MQTT message broker and subscribe to all messages.
 * @author Thomas Pilz
 */
public class MqttService implements MqttCallback {

    private static final Logger logger = LoggerFactory.getLogger(MqttService.class);
    private Consumer<KpiMsg> onValidMessageListener;
    private final MqttClientPersistence persistence;
    private final MqttConnectionOptions connectionOptions;
    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private final String msgBrokerUrl;
    private final String clientId;


    public MqttService(String msgBrokerUrl, String clientId, Consumer<KpiMsg> onValidMessageListener) {
        this.msgBrokerUrl = msgBrokerUrl;
        this.clientId = clientId;
        // Set memory persistence to prevent paho to create lots of lock files
        this.persistence = new MemoryPersistence();
        // Set MQTT connection options
        this.connectionOptions = new MqttConnectionOptionsBuilder()
                .automaticReconnect(true)
                .cleanStart(true)
                .build();
        this.onValidMessageListener = onValidMessageListener;
    }

    public MqttService(String msgBrokerUrl, String clientId){
        this(msgBrokerUrl, clientId, msg -> {});
    }

    public void connectAndSubscribeAll(){
        try {
            // Use async MQTT client for better performance/ non-blocking operations
            logger.info("Attempting to connect to {} as {}", msgBrokerUrl, clientId);
            var client = new MqttAsyncClient(msgBrokerUrl, clientId, persistence);
            client.setCallback(this);
            var conToken = client.connect(connectionOptions, new MqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    logger.warn("Failed to connect to {}", msgBrokerUrl, exception);
                }
            });
            conToken.waitForCompletion();
            client.subscribe("#", 0);
        } catch (MqttException e) {
            logger.warn("Failed to connect due to exception.", e);
        }
    }

    public Consumer<KpiMsg> getOnValidMessageListener() {
        return onValidMessageListener;
    }

    public void setOnValidMessageListener(Consumer<KpiMsg> onValidMessageListener) {
        this.onValidMessageListener = onValidMessageListener;
    }

    public void onValidMessageReceived(Consumer<KpiMsg> listener, KpiMsg kpiMsg){
        listener.accept(kpiMsg);
    }

    @Override
    public void disconnected(MqttDisconnectResponse disconnectResponse) {
        logger.warn("Disconnect from MQTT message broker: Reason Code: {}; Reason: {}", disconnectResponse.getReturnCode(), disconnectResponse.getReasonString());
    }

    @Override
    public void mqttErrorOccurred(MqttException exception) {
        logger.warn("An MQTT error occurred!", exception);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        logger.debug("Received message of topic {}", topic);
/*        JsonNode msg = null;

        try {
            msg = new ObjectMapper().readTree(message.getPayload());
        } catch (IOException e){
            logger.warn("Received message could not be parsed as JSON. Message is ignored.");
            return;
        }
        var validation = Validator.isMsgValid(msg);
        if(!validation.isValid()) {
            logger.warn(validation.getErrMsg());
            return;
        }
        System.out.println("parsed");*/
        // now we know the type of the message; No need to catch DatabindException as validator ensures message has the right format
        KpiMsg kpiMsg;
        try {
            kpiMsg = jsonMapper.readValue(message.getPayload(), KpiMsg.class);
        } catch (DatabindException e){
            logger.warn("Received message does not conform to required structure.", e);
            return;
        } catch (IOException e){
            logger.warn("Could not parse message for topic {}", topic, e);
            return;
        }
        logger.debug("Parsed message: {}", kpiMsg.toString());
        onValidMessageListener.accept(kpiMsg);
    }

    @Override
    public void deliveryComplete(IMqttToken token) {

    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        if(reconnect) logger.info("Successfully reconnected to {}", serverURI);
        else logger.info("Successfully connected to {}", serverURI);
    }

    @Override
    public void authPacketArrived(int reasonCode, MqttProperties properties) {

    }
}
