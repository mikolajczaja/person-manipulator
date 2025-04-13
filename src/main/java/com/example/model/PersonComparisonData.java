package com.example.model;

import jakarta.validation.constraints.NotBlank;
import lombok.experimental.FieldNameConstants;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Map;

@FieldNameConstants
public record PersonComparisonData(@NotBlank String name, @NotBlank String surname, @DateTimeFormat String birthdate, String company) implements Serializable {

    /**
     * Alternatively {@link com.fasterxml.jackson.databind.ObjectMapper} could be used
     */
    public Map<String, String> getFieldValuesAsMap(){
        return Map.of(
                Fields.name, name,
                Fields.surname, surname,
                Fields.birthdate, birthdate,
                Fields.company, company);
    }
    public static PersonComparisonData empty(){
        return new PersonComparisonData("", "", "", "");
    }
}
