package de.othr.database_connector.kpi;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

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
 * One instance of this class represents one measurement of a Key Performance Indicator (=KPI).
 * That is a value for measurement of interest at a certain point in time.
 * @author Thomas Pilz
 */
public class Kpi implements Serializable {

    /**
     * Name of KPI being recorded
     */
    private String name;
    /**
     * Unit KPI is measured in.
     *
     * Will be transformed to an int value when serialized.
     * This is required as the database has
     */
    private int unitId;
    /**
     * Value of KPI
     */
    private BigDecimal value;

    /**
     * Create new KPI value
     */
    public Kpi() {
    }

    /**
     * Create new KPI value.
     * @param name name of KPI
     * @param unitId unit KPI is measured in
     * @param value value of KPI
     */
    public Kpi(String name, int unitId, BigDecimal value) {
        this.name = name;
        this.unitId = unitId;
        this.value = value;
    }

    /**
     * Get KPI name
     * @return KPI name
     */
    public String getName() {
        return name;
    }

    /**
     * Set KPI name
     * @param name KPI name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get unit KPI is measured in
     * @return KPI unit
     */
    public int getUnitId() {
        return unitId;
    }

    /**
     * Set unit KPI is measured in
     * @param unitId KPI unit
     */
    public void setUnitId(int unitId) {
        this.unitId = unitId;
    }

    /**
     * Get KPI value of measurement
     * @return KPI value
     */
    public BigDecimal getValue() {
        return value;
    }

    /**
     * Set KPI value of measurement
     * @param value KPI value
     */
    public void setValue(BigDecimal value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Kpi kpi = (Kpi) o;
        return Objects.equals(name, kpi.name) && unitId == kpi.unitId && Objects.equals(value, kpi.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, unitId, value);
    }

    @Override
    public String toString() {
        return "Kpi{" +
                "name='" + name + '\'' +
                ", unit='" + unitId + '\'' +
                ", value=" + value +
                '}';
    }
}
