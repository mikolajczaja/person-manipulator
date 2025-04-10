package com.example.mock;

import com.example.model.Person;
import com.example.repository.PersonRepository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MockPersonRepository implements PersonRepository {

    ConcurrentHashMap<Long, Person> mockedStorage = new ConcurrentHashMap<>();
    AtomicLong mockedIdGenerator = new AtomicLong();

    @Override
    public <S extends Person> S save(S entity) {
        if (entity.getId() == null) {
            entity.setId(mockedIdGenerator.incrementAndGet());
        }
        mockedStorage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public <S extends Person> Iterable<S> saveAll(Iterable<S> entities) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Optional<Person> findById(Long id) {
        return Optional.ofNullable(mockedStorage.get(id));
    }

    @Override
    public boolean existsById(Long aLong) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Iterable<Person> findAll() {
        return mockedStorage.values();
    }

    @Override
    public Iterable<Person> findAllById(Iterable<Long> longs) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public long count() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void deleteById(Long id) {
        mockedStorage.remove(id);
    }

    @Override
    public void delete(Person entity) {
        mockedStorage.remove(entity.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void deleteAll(Iterable<? extends Person> entities) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void deleteAll() {
        throw new RuntimeException("Not implemented");
    }
}
