package de.othr.database_connector.kpi;

import java.io.Serializable;
import java.time.Instant;
import java.util.Collection;

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

public class KpiMsg implements Serializable {
    /**
     * Unique equipment number of the object (e.g. machine) the KPIs belong to
     */
    private String clientId;
    /**
     * Unix timestamp which gives you the milliseconds since "The Epoch"
     */
    private long unixTimestamp;
    /**
     * ISO8601 compliant String representation of current time.
     * MUST be the same time as unixTimestamp!
     */
    private String timestamp;
    private Collection<Kpi> kpis;

    public KpiMsg() {
    }

    /**
     * Create a message containing KPI values. A timestamp will be assigned upon creation.
     * @param clientId equipment number
     * @param kpis collection of KPIs for this equipment
     */
    public KpiMsg(String clientId, Collection<Kpi> kpis) {
        var now = Instant.now();
        this.unixTimestamp = now.toEpochMilli();
        this.timestamp = now.toString();
        this.clientId = clientId;
        this.kpis = kpis;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public long getUnixTimestamp() {
        return unixTimestamp;
    }

    public void setUnixTimestamp(long unixTimestamp) {
        this.unixTimestamp = unixTimestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Collection<Kpi> getKpis() {
        return kpis;
    }

    public void setKpis(Collection<Kpi> kpis) {
        this.kpis = kpis;
    }
}
