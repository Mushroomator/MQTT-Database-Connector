package de.othr.database_connector.helpers;
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

/**
 * All environment variables.
 * @author Thomas Pilz
 */
public class EnvironmentVariables {
    public String postgresDbDomain;
    public String postgresUser;
    public String postgresDb;
    public String postgresPw;
    public String mqttClientId;
    public String mqttMsgBrokerUrl;

    public EnvironmentVariables(String postgresDbDomain, String postgresUser, String postgresDb, String postgresPw, String mqttClientId, String mqttMsgBrokerUrl) {
        this.postgresDbDomain = postgresDbDomain;
        this.postgresUser = postgresUser;
        this.postgresDb = postgresDb;
        this.postgresPw = postgresPw;
        this.mqttClientId = mqttClientId;
        this.mqttMsgBrokerUrl = mqttMsgBrokerUrl;
    }
}
