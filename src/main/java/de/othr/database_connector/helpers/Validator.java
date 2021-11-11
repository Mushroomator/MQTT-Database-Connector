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

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

import java.util.Arrays;
import java.util.Set;

/**
 * Validator with utility methods for input validation.
 * @author Thomas Pilz
 */
public class Validator {


    private static final JsonSchemaFactory jsFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);

    public static ValidityChecker isMsgValid(JsonNode msg){
        var inputStream = Validator.class.getClassLoader().getResourceAsStream("kpi_msg_schema.json");
        var schema = jsFactory.getSchema(inputStream);
        Set<ValidationMessage> errors = schema.validate(msg);
        if(errors.size() == 0) return ValidityChecker.valid();
        return ValidityChecker.invalid("Message must conform to JSON Schema! Errors found: %s".formatted(errors.toString()));
    }
}
