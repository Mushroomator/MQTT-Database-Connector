package de.othr.database_connector;
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
import de.othr.database_connector.helpers.EnvironmentVariables;
import de.othr.database_connector.services.DatabaseService;
import de.othr.database_connector.services.MqttService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Program which subscribes to all messages sent to a MQTT message broker and then puts relevant messages containing KPIs in a PostgreSQL database.
 * @author Thomas Pilz
 */
public class DatabaseConnector {

    /**
     * SLF4J logger using Log4j 2
     */
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnector.class);
    private static DatabaseService db = DatabaseService.getInstance();

    public static void main(String[] args) {

        var jdbcPostgresPrefix = "jdbc:postgresql://";
        var envs = readEnvs();
        // Connect do PostgreSQL database
        db.connect2db(String.format("%s%s/%s", jdbcPostgresPrefix, envs.postgresDbDomain, envs.postgresDb), envs.postgresUser, envs.postgresPw);

        // Create MQTT service
        var mqttService = new MqttService(envs.mqttMsgBrokerUrl, envs.mqttClientId);
        // Set listener for when a valid message is received
        mqttService.setOnValidMessageListener(msg -> {
            if(db.isConnected()){
                db.insertKpis(msg);
            }
        });
        // connect to MQTT message broker and subsribe to all messages
        mqttService.connectAndSubscribeAll();
    }

    /**
     * Read environment variables and leave program if one of the mandatory options is missing.
     * @return environment variable values
     */
    private static EnvironmentVariables readEnvs(){
        var postgresDbDomain = System.getenv("POSTGRES_DB_DOMAIN");
        if(postgresDbDomain == null){
            logger.error("Environment variable POSTGRES_DB_DOMAIN must be specified!");
            System.exit(1);
        }

        var postgresUser = System.getenv("POSTGRES_USER");
        if(postgresUser == null){
            logger.error("Environment variable POSTGRES_USER must be specified!");
            System.exit(1);
        }

        var postgresDb = System.getenv("POSTGRES_DB");
        if(postgresDb == null){
            logger.error("Environment variable POSTGRES_DB must be specified!");
            System.exit(1);
        }

        var postgresPw = System.getenv("POSTGRES_PW");
        if(postgresPw == null){
            logger.error("Environment variable POSTGRES_PW must be specified!");
            System.exit(1);
        }

        var mqttClientId = System.getenv("MQTT_CLIENT_ID");
        if(mqttClientId == null){
            logger.error("Environment variable MQTT_CLIENT_ID must be specified!");
            System.exit(1);
        }

        var mqttMsgBrokerUrl = System.getenv("MQTT_MSG_BROKER_URL");
        if(mqttMsgBrokerUrl == null){
            logger.error("Environment variable MQTT_MSG_BROKER_URL must be specified!");
            System.exit(1);
        }
        var envs = new EnvironmentVariables(postgresDbDomain, postgresUser, postgresDb, postgresPw, mqttClientId, mqttMsgBrokerUrl);
        logger.info("Read configuration from environment variables: \n{}", envs);
        return envs;
    }
}
