package com.example.mock;

import com.example.model.Classification;
import com.example.model.Task;
import com.example.repository.TaskRepository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MockTaskRepository implements TaskRepository {

    ConcurrentHashMap<Long, Task> mockedStorage = new ConcurrentHashMap<>();
    AtomicLong mockedIdGenerator = new AtomicLong();

    @Override
    public <S extends Task> S save(S entity) {
        if (entity.getId() == null) {
            entity.setId(mockedIdGenerator.incrementAndGet());
        }
        mockedStorage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public <S extends Task> Iterable<S> saveAll(Iterable<S> entities) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Optional<Task> findById(Long id) {
        return Optional.ofNullable(mockedStorage.get(id));
    }

    @Override
    public boolean existsById(Long aLong) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Iterable<Task> findAll() {
        return mockedStorage.values();
    }

    @Override
    public Iterable<Task> findAllById(Iterable<Long> longs) {
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
    public void delete(Task entity) {
        mockedStorage.remove(entity.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void deleteAll(Iterable<? extends Task> entities) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void deleteAll() {
        throw new RuntimeException("Not implemented");
    }

    //TODO (future idea) - consider removing partial update functionality - too much hassle with handling the implementation
    //TODO (future idea) - alternatively store tasks in progress in an intermediate cache (i.e. save step-by-step to said cache, then when done - save to db/datastore), when user fetches task - return from said cache if present, if not then from db
    @Override
    public void updateClassification(Long id, String fieldKey, Classification value) {
        Task task = mockedStorage.get(id);
        switch (fieldKey) {
            case Task.Fields.nameClassification -> task.setNameClassification(value);
            case Task.Fields.surnameClassification -> task.setSurnameClassification(value);
            case Task.Fields.birthdateClassification -> task.setBirthdateClassification(value);
            case Task.Fields.companyClassification -> task.setCompanyClassification(value);
            default -> throw new IllegalStateException("Unexpected value: " + fieldKey);
        }
    }

    @Override
    public void updatePercentage(Long id, int value) {
        Task task = mockedStorage.get(id);
        task.setPercentDone(value);
    }

    @Override
    public void updateStatus(Long id, Task.TaskStatus value) {
        Task task = mockedStorage.get(id);
        task.setStatus(value);
    }
}
