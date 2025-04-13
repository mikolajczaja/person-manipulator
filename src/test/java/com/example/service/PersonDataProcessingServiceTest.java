package com.example.service;

import com.example.model.PersonComparisonData;
import com.example.model.Task;
import com.example.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonDataProcessingServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private DifferenceClassificationTool differenceClassificationTool;

    @Test
    void testCreatedPersonDataComparison() {
        PersonComparisonData data = new PersonComparisonData("Adam", "Kowalski", "13-04-2025", "company A");

        Task expected = Task.forCreatedPerson(data);
        when(taskRepository.save(any())).thenReturn(expected);
        PersonDataProcessingService service = new PersonDataProcessingService(differenceClassificationTool, taskRepository);
        service.processComparisonForCreatedPerson(data);
        verify(taskRepository).save(expected);
    }

    @Test
    void testDeletedPersonDataComparison() {
        PersonComparisonData data = new PersonComparisonData("Adam", "Kowalski", "13-04-2025", "company A");

        Task expected = Task.forDeletedPerson(data);
        when(taskRepository.save(any())).thenReturn(expected);
        PersonDataProcessingService service = new PersonDataProcessingService(differenceClassificationTool, taskRepository);
        service.processComparisonForDeletedPerson(data);
        verify(taskRepository).save(expected);
    }

    @Test
    void testPersonDataComparison() {
        PersonComparisonData oldData = new PersonComparisonData("Adam", "Kowalski", "13-04-2025", "company A");
        PersonComparisonData newData = new PersonComparisonData("Adam", "Kowalski-Nowak", "01-04-2020", "company called B");

        Task savedTask = new Task(oldData, newData);
        long id = 15L;
        savedTask.setId(id);
        when(taskRepository.save(any())).thenReturn(savedTask);
        PersonDataProcessingService service = new PersonDataProcessingService(differenceClassificationTool, taskRepository);

        Task task = service.createTask(oldData, newData);
        service.runTaskAsync(task); //obv won't be run asynchronously here

        ArgumentCaptor<String> oldValueCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> newValueCaptor = ArgumentCaptor.forClass(String.class);

        //TODO (future idea) - remove these 2 lines and use partial updates without actually setting values on Task instance
        verify(taskRepository, times(4)).save(task);
        assertThat(task.getStatus()).isEqualTo(Task.TaskStatus.FINISHED);

        verify(differenceClassificationTool, times(4)).classifyDifference(oldValueCaptor.capture(), newValueCaptor.capture());
        assertThat(oldValueCaptor.getAllValues()).containsExactly(oldData.name(), oldData.surname(), oldData.birthdate(), oldData.company());
        assertThat(newValueCaptor.getAllValues()).containsExactly(newData.name(), newData.surname(), newData.birthdate(), newData.company());

    }
}