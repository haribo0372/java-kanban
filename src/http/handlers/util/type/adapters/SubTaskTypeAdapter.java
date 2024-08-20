package http.handlers.util.type.adapters;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;


import java.lang.reflect.Type;
import java.util.ArrayList;

import models.Epic;
import models.SubTask;

public class SubTaskTypeAdapter implements JsonSerializer<SubTask> {
    private final EpicTypeAdapter epicTypeAdapter = new EpicTypeAdapter();

    @Override
    public JsonElement serialize(SubTask subtask, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("id", subtask.getId());
        jsonObject.addProperty("name", subtask.getName());
        jsonObject.addProperty("description", subtask.getDescription());
        jsonObject.addProperty("taskStatus", subtask.getTaskStatus().name());
        if (subtask.getDuration() != null)
            jsonObject.addProperty("duration", subtask.getDuration().toString());

        if (subtask.getStartTime() != null)
            jsonObject.addProperty("startTime", subtask.getStartTime().format(LocalDateTimeAdapter.getTimeFormatter()));

        Epic epic = subtask.getCurrentEpic();
        if (epic != null) {
            JsonElement epicJson = epicTypeAdapter.serialize(epic, Epic.class, context);
            jsonObject.add("currentEpic", epicJson);
        }
        return jsonObject;
    }

    private static class EpicTypeAdapter implements JsonSerializer<Epic> {
        @Override
        public JsonElement serialize(Epic epic, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("id", epic.getId());
            jsonObject.add("subTasks", jsonSerializationContext.serialize(new ArrayList<>()));
            return jsonObject;
        }
    }
}