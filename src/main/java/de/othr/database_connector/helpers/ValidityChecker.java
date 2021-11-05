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

import java.io.Serializable;
import java.util.Objects;

/**
 * "Return object" which only allows to have certain states.
 * e.g. it is not possible to enter have an invalid state without an error message.
 * @author Thomas Pilz
 */
public class ValidityChecker implements Serializable {
    private boolean isValid;
    private String errMsg;


    private ValidityChecker(boolean isValid) {
        this.isValid = isValid;
    }

    private ValidityChecker(boolean isValid, String errMsg){
        this.isValid = isValid;
        this.errMsg = errMsg;
    }

    /**
     * Creates instance of ValidityChecker indicating a positive validation result.
     * @return instance of ValidityChecker
     */
    public static ValidityChecker valid(){
        return new ValidityChecker(true);
    }

    /**
     * Creates instance of ValidityChecker indicating a negative validation result.
     * @param errMsg error message
     * @return instance of ValidityChecker
     */
    public static ValidityChecker invalid(String errMsg){
        return new ValidityChecker(false, errMsg);
    }

    public boolean isValid() {
        return isValid;
    }

    public String getErrMsg() {
        return errMsg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValidityChecker that = (ValidityChecker) o;
        return isValid == that.isValid && Objects.equals(errMsg, that.errMsg);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isValid, errMsg);
    }

    @Override
    public String toString() {
        return "ValidityChecker{" +
                "isValid=" + isValid +
                ", errMsg='" + errMsg + '\'' +
                '}';
    }
}
