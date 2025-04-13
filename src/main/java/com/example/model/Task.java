package com.example.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash("tasks")
@JsonSerialize(using = TaskSerializer.class)
@FieldNameConstants
public class Task implements Serializable {

    @Id
    private Long id;
    private int percentDone = 0;
    private TaskStatus status = TaskStatus.PENDING;
    private Classification nameClassification = Classification.NOT_CALCULATED_YET;
    private Classification surnameClassification = Classification.NOT_CALCULATED_YET;
    private Classification birthdateClassification = Classification.NOT_CALCULATED_YET;
    private Classification companyClassification = Classification.NOT_CALCULATED_YET;

    //TODO (future idea) - use both fields for restarting dead tasks (currently not displayed in endpoint responses), like with a scheduled task or whatever else
    private PersonComparisonData oldData;
    private PersonComparisonData newData;

    public int incrementPercentDone(int incrementValue) {
        percentDone += incrementValue;
        return percentDone;
    }

    //could be replaced with checks against empty strings, but this can be a wee bit faster
    public static Task forCreatedPerson(PersonComparisonData newData) {
        return new TaskBuilder()
                .percentDone(100)
                .status(TaskStatus.FINISHED)
                .nameClassification(Classification.ADDED)
                .surnameClassification(Classification.ADDED)
                .birthdateClassification(Classification.ADDED)
                .companyClassification(Classification.ADDED)
                .newData(newData).build();
    }

    //could be replaced with checks against empty strings, but this can be a wee bit faster
    public static Task forDeletedPerson(PersonComparisonData oldData) {
        return new TaskBuilder()
                .percentDone(100)
                .status(TaskStatus.FINISHED)
                .nameClassification(Classification.DELETED)
                .surnameClassification(Classification.DELETED)
                .birthdateClassification(Classification.DELETED)
                .companyClassification(Classification.DELETED)
                .oldData(oldData).build();
    }

    public Task(PersonComparisonData oldData, PersonComparisonData newData) {
        this.oldData = oldData;
        this.newData = newData;
    }

    public enum TaskStatus {PENDING, RUNNING, FINISHED, ABORTED}
}