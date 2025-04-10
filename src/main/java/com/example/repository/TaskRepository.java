package com.example.repository;

import com.example.model.Task;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository
@RepositoryRestResource(collectionResourceRel = "tasks", path = "tasks")
public interface TaskRepository extends CrudRepository<Task, Long>, AdvancedTaskRepository {
}
