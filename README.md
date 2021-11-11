![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)

# MQTT Database Connector
Program which subscribes to all messages sent to a MQTT message broker and then puts relevant messages containing KPIs in a PostgreSQL database.

> This repository is part of the [MQTT KPI Collection Project](https://github.com/Mushroomator/MQTT-KPI-Collection-Project).

## Table of Contents
- [MQTT Database Connector](#mqtt-database-connector)
  - [Table of Contents](#table-of-contents)
  - [Usage](#usage)
  - [Environment variables](#environment-variables)
  - [License](#license)

## Usage
You can run the image with a simple docker command which must contain all the mandatory environment variables. See [section "Environment variables"](#environment-variables) for more information on those. The container will connect on port 5432 (= PostgreSQL default port) to the database.
```bash
docker run -d thomaspilz/mqtt-database-connector:$COMMIT_SHA \
  -e MQTT_MSG_BROKER_URL=tcp://mosquitto:1883 \
  -e MQTT_CLIENT_ID=mqtt-database-connector \
  -e POSTGRES_DB_DOMAIN=database-connector \
  -e POSTGRES_DB=yourDatabase \
  -e POSTGRES_USER=yourUsername \
  -e POSTGRES_PW=yourPassword
```
`$COMMIT_SHA` must be replaced with the first seven characters of the corresponding Github commit hash.

## Environment variables
There are a few environment variables available to set mandatory parameters/ options.

| Name                | Description                                                                                                                  | Mandatory? | Default value |
| ------------------- | ---------------------------------------------------------------------------------------------------------------------------- | ---------- | ------------- |
| MQTT_MSG_BROKER_URL | URL to the MQTT message broker. Must be in format `[protocol]://[hostname]:[port]` for example `tcp://iot.eclipse.org:1883`. | Yes        | -             |
| MQTT_CLIENT_ID      | Unique ID for the MQTT client.                                                                                               | Yes        | -             |
| POSTGRES_DB_DOMAIN  | Domainname of the PostgreSQL database.                                                                                       | Yes        | -             |
| POSTGRES_DB         | Name of the PostgreSQL database that contains the KPI table.                                                                 | Yes        | -             |
| POSTGRES_USER       | PostgreSQL username. Must have appropriate write permissions to insert KPIs into table.                                      | Yes        | -             |
| POSTGRES_PW         | Password for the given PostgreSQL username.                                                                                  | Yes        | -             |


## License
Copyright 2021 Thomas Pilz

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

[https://www.apache.org/licenses/LICENSE-2.0](https://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.