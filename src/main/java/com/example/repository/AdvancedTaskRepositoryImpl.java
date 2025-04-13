package com.example.repository;

import com.example.model.Classification;
import com.example.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.PartialUpdate;
import org.springframework.data.redis.core.RedisKeyValueTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AdvancedTaskRepositoryImpl implements AdvancedTaskRepository {

    @Autowired
    private RedisKeyValueTemplate redisKeyValueTemplate;

    @Override
    public void updateClassification(Long id, String fieldKey, Classification value) {
        redisKeyValueTemplate.update(new PartialUpdate<>(id, Task.class).set(fieldKey, value));
    }

    @Override
    public void updatePercentage(Long id, int value) {
        redisKeyValueTemplate.update(new PartialUpdate<>(id, Task.class).set(Task.Fields.percentDone, value));
    }

    @Override
    public void updateStatus(Long id, Task.TaskStatus value) {
        redisKeyValueTemplate.update(new PartialUpdate<>(id, Task.class).set(Task.Fields.status, value));
    }
}
