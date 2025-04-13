package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

/**
 * A person consists of several fields, such as name, surname, birthdate and
 * company
 */
@RedisHash("people")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public final class Person implements Serializable {

    @Id
    private Long id;

    private String name;
    private String surname;
    private String birthdate;
    private String company;

    public PersonComparisonData toPersonComparisonData() {
        return new PersonComparisonData(name, surname, birthdate, company);
    }
}