package com.example.service;

import com.example.model.Classification;
import com.example.model.PersonComparisonData;
import com.example.model.Task;
import com.example.repository.TaskRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service used for creating and running {@link PersonComparisonData} comparison tasks
 */
@Slf4j
@Service
public class PersonDataProcessingService {

    public static final String TASK_FIELD_NAME_SUFFIX = "Classification";

    private final DifferenceClassificationTool differenceClassificationTool;
    private final TaskRepository taskRepository;

    @Autowired
    public PersonDataProcessingService(DifferenceClassificationTool differenceClassificationTool, TaskRepository taskRepository) {
        this.differenceClassificationTool = differenceClassificationTool;
        this.taskRepository = taskRepository;
    }

    public Long processComparisonForCreatedPerson(PersonComparisonData personComparisonData) {
        return taskRepository.save(Task.forCreatedPerson(personComparisonData)).getId();
    }

    public Long processComparisonForDeletedPerson(PersonComparisonData personComparisonData) {
        return taskRepository.save(Task.forDeletedPerson(personComparisonData)).getId();
    }

    /**
     * {@link Cacheable} at this level, to cache actual comparisons,
     * and not responses to specific request params - because comparison processing might
     * still be necessary for repeated requests
     * <br><br>
     * e.g. company of a particular person changes like "aaa" -> "bbb" -> "ccc" -> "bbb",
     * with "bbb" requiring 2 different comparisons (against "aaa" and "ccc")
     */
    @Cacheable("tasks")
    public Task createTask(PersonComparisonData oldData, PersonComparisonData newData) {
        return taskRepository.save(new Task(oldData, newData));
    }

    @Async
    public void runTaskAsync(Task task) {
        Long taskNumber = task.getId();
        PersonComparisonData oldData = task.getOldData();
        PersonComparisonData newData = task.getNewData();

        task.setStatus(Task.TaskStatus.RUNNING);
        taskRepository.updateStatus(taskNumber, Task.TaskStatus.RUNNING);
        log.info("task {} started", taskNumber);

        task.setNameClassification(differenceClassificationTool.classifyDifference(oldData.name(), newData.name()));
        saveProgress(task, 25);

        task.setSurnameClassification(differenceClassificationTool.classifyDifference(oldData.surname(), newData.surname()));
        saveProgress(task, 50);

        task.setBirthdateClassification(differenceClassificationTool.classifyDifference(oldData.birthdate(), newData.birthdate()));
        saveProgress(task, 75);

        task.setCompanyClassification(differenceClassificationTool.classifyDifference(oldData.company(), newData.company()));
        saveProgress(task, 100);
    }

    private void saveProgress(Task task, int percentDone) {
        if (percentDone == 100) {
            task.setStatus(Task.TaskStatus.FINISHED);
        }
        task.setPercentDone(percentDone);
        taskRepository.save(task);
        log.info("task {} progress: {}%", task.getId(), percentDone);
    }

    @Deprecated(forRemoval = true)
    @Async
    public void runTaskAsyncAlternativeStrat(Task task) {
        Long taskNumber = task.getId();
        Map<String, String> oldDataFieldValues = task.getOldData().getFieldValuesAsMap();
        int percentageIncrement = 100 / oldDataFieldValues.size();
        AtomicInteger percentDone = new AtomicInteger();

        taskRepository.updateStatus(taskNumber, Task.TaskStatus.RUNNING);
        log.info("task {} started", taskNumber);

        task.getNewData().getFieldValuesAsMap().forEach((key, value) -> {
            Classification classification = differenceClassificationTool.classifyDifference(oldDataFieldValues.get(key), value);
            taskRepository.updateClassification(taskNumber, key + TASK_FIELD_NAME_SUFFIX, classification);
            taskRepository.updatePercentage(taskNumber, percentDone.addAndGet(percentageIncrement));
            log.info("task {} progress: {}%", percentDone, taskNumber);
        });

        taskRepository.updateStatus(taskNumber, Task.TaskStatus.FINISHED);
        log.info("task {} done", taskNumber);
    }

    @Deprecated(forRemoval = true)
    @Async
    @SneakyThrows
    public void runTaskAsyncReflectiveStrat(Task task) {
        Long taskNumber = task.getId();
        PersonComparisonData oldData = task.getOldData();
        PersonComparisonData newData = task.getNewData();

        Field[] fields = PersonComparisonData.class.getDeclaredFields();
        int percentageIncrement = 100 / fields.length;
        AtomicInteger percentDone = new AtomicInteger();

        taskRepository.updateStatus(taskNumber, Task.TaskStatus.RUNNING);
        log.info("task {} started", taskNumber);

        for (Field field : fields) {
            Method getter = PersonComparisonData.class.getMethod(field.getName());
            String oldValue = (String) getter.invoke(oldData);
            String newValue = (String) getter.invoke(newData);
            Classification result = differenceClassificationTool.classifyDifference(oldValue, newValue);

            taskRepository.updateClassification(taskNumber, field.getName() + TASK_FIELD_NAME_SUFFIX, result);
            taskRepository.updatePercentage(taskNumber, percentDone.addAndGet(percentageIncrement));
            log.info("task {} progress: {}%", percentDone, taskNumber);
        }

        taskRepository.updateStatus(taskNumber, Task.TaskStatus.FINISHED);
        log.info("task {} done", taskNumber);
    }

}