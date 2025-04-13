package com.example.repository;

import com.example.model.Classification;
import com.example.model.Task;

public interface AdvancedTaskRepository {
    void updateClassification(Long id, String fieldKey, Classification value);
    void updatePercentage(Long id, int value);
    void updateStatus(Long id, Task.TaskStatus value);
}
