package com.example.api;

import com.example.mock.MockPersonRepository;
import com.example.mock.MockTaskRepository;
import com.example.model.Person;
import com.example.model.Task;
import com.example.repository.PersonRepository;
import com.example.repository.TaskRepository;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.convention.TestBean;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

//TODO (future idea) - (way) more tests, and more single-use-case-oriented ones
/**
 * mocked repos, disabled caches
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureRestDocs
class ApiTests {

    @LocalServerPort
    private int port;

    @TestBean
    private TaskRepository taskRepository;
    @TestBean
    private PersonRepository personRepository;

    static TaskRepository taskRepository() {
        return new MockTaskRepository();
    }

    static PersonRepository personRepository() {
        return new MockPersonRepository();
    }

    @Test
    void listUsers(@Autowired RequestSpecification documentationSpec) {
        given(documentationSpec)
                .filter(document("people"))
                .when()
                .port(port)
                .get("/api/v1/people")
                .then()
                .assertThat()
                .statusCode(is(200));
    }

    @Test
    void addAndReadPersonAndRelatedTask(@Autowired RequestSpecification documentationSpec) {
        Person expectedPerson = Person.builder()
                .name("Stefan")
                .surname("Kowalski")
                .birthdate("11.12.2020")
                .company("januszex").build();
        PersonManipulationController.UpsertRequest requestBody = new PersonManipulationController.UpsertRequest(
                expectedPerson.getName(),
                expectedPerson.getSurname(),
                expectedPerson.getBirthdate(),
                expectedPerson.getCompany());

        PersonManipulationController.UpsertResponse createPersonResponse = given(documentationSpec)
                .filter(document("people"))
                .when()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .port(port)
                .post("/api/v1/people")
                .then()
                .assertThat()
                .statusCode(is(200))
                .extract().body().as(PersonManipulationController.UpsertResponse.class);

        expectedPerson.setId(createPersonResponse.taskNumber());
        assertThat(createPersonResponse.person()).isEqualTo(expectedPerson);

        Person actual = given(documentationSpec)
                .filter(document("people"))
                .when()
                .port(port)
                .get("/api/v1/people/{id}", expectedPerson.getId())
                .then()
                .assertThat()
                .statusCode(is(200))
                .extract().body().as(Person.class);

        assertThat(actual).isEqualTo(expectedPerson);

        long actualTaskNumber = given(documentationSpec)
                .filter(document("tasks"))
                .when()
                .port(port)
                .get("/api/v1/tasks/{id}", createPersonResponse.taskNumber())
                .then()
                .assertThat()
                .statusCode(is(200))
                .and()
                .extract().response().jsonPath().getLong(Task.Fields.id);

        assertThat(actualTaskNumber).isEqualTo(createPersonResponse.taskNumber());
    }

}