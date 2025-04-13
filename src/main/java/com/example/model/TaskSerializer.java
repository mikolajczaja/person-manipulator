package com.example.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Serializer that hides {@link Classification} fields until {@link Task} is finished.
 */
public class TaskSerializer extends JsonSerializer<Task> {

    @Override
    public void serialize(Task task, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField(Task.Fields.id, task.getId());

        Task.TaskStatus status = task.getStatus();
        jsonGenerator.writeObjectField(Task.Fields.status, status);

        if (status.equals(Task.TaskStatus.FINISHED)) {
            jsonGenerator.writeObjectField(Task.Fields.nameClassification, task.getNameClassification());
            jsonGenerator.writeObjectField(Task.Fields.surnameClassification, task.getSurnameClassification());
            jsonGenerator.writeObjectField(Task.Fields.birthdateClassification, task.getBirthdateClassification());
            jsonGenerator.writeObjectField(Task.Fields.companyClassification, task.getCompanyClassification());
        } else {
            jsonGenerator.writeNumberField(Task.Fields.percentDone, task.getPercentDone());
        }

        jsonGenerator.writeEndObject();
    }
}
