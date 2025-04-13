package com.example.api;

import com.example.exception.PersonNotFoundException;
import com.example.model.Person;
import com.example.model.PersonComparisonData;
import com.example.model.Task;
import com.example.repository.PersonRepository;
import com.example.service.PersonDataProcessingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.Optional;

//TODO (future idea) - using a sql db 😿, with correct and hassle-free locking
/**
 * Design API, which allows the manipulation of a person, lists all tasks and reads
 * their status and results
 */
@RestController
@RequestMapping("/api/v1/people")
public class PersonManipulationController {

    private final PersonRepository personRepository;

    private final PersonDataProcessingService personDataProcessingService;

    @Autowired
    public PersonManipulationController(PersonRepository personRepository, PersonDataProcessingService personDataProcessingService) {
        this.personRepository = personRepository;
        this.personDataProcessingService = personDataProcessingService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> getPersonById(@PathVariable Long id) {
        return ResponseEntity.of(personRepository.findById(id));
    }

    @PostMapping
    public UpsertResponse createPerson(@Valid @RequestBody UpsertRequest request) {
        Person saved = personRepository.save(request.toPerson());
        Long taskNumber = personDataProcessingService.processComparisonForCreatedPerson(saved.toPersonComparisonData());
        return new UpsertResponse(saved, taskNumber);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpsertResponse> updatePerson(@PathVariable @NotNull Long id, @Valid @RequestBody UpsertRequest request) {
        PersonComparisonData oldData;
        PersonComparisonData newData;
        Person newPerson;

        synchronized (this) { //to (hopefully) avoid situations where the Person was modified after findById (without locking fun 😅)
            Optional<Person> personOptional = personRepository.findById(id);
            if (personOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Person oldPerson = personOptional.get();
            oldData = oldPerson.toPersonComparisonData();

            oldPerson.setName(request.name);
            oldPerson.setSurname(request.surname);
            oldPerson.setBirthdate(request.birthdate);
            oldPerson.setCompany(request.company);

            newPerson = personRepository.save(oldPerson);
            newData = newPerson.toPersonComparisonData();
        }
        Task task = personDataProcessingService.createTask(oldData, newData);
        personDataProcessingService.runTaskAsync(task);
        return ResponseEntity.ok(new UpsertResponse(newPerson, task.getId()));
    }

    @DeleteMapping("/{id}")
    public DeleteResponse deletePerson(@PathVariable Long id) throws PersonNotFoundException {
        Person oldPerson;

        synchronized (this) { //to avoid situations where the Person was modified after findById (without locking fun 😅)
            Optional<Person> personOptional = personRepository.findById(id);
            if (personOptional.isEmpty()) {
                throw new PersonNotFoundException();
            }
            oldPerson = personOptional.get();
            personRepository.delete(oldPerson);
        }
        Long taskNumber = personDataProcessingService.processComparisonForDeletedPerson(oldPerson.toPersonComparisonData());
        return new DeleteResponse(id, taskNumber);
    }

    @GetMapping
    public ResponseEntity<Iterable<Person>> getAllPeople() {
        return ResponseEntity.ok(personRepository.findAll());
    }

    public record UpsertRequest(@NotBlank String name, @NotBlank String surname,
                         @DateTimeFormat String birthdate, String company) {
        public Person toPerson() {
            Person person = new Person();
            person.setName(name);
            person.setSurname(surname);
            person.setBirthdate(birthdate);
            person.setCompany(company);
            return person;
        }
    }

    public record UpsertResponse(Person person, Long taskNumber) implements Serializable {
    }

    public record DeleteResponse(Long id, Long taskNumber) implements Serializable {
    }

}
