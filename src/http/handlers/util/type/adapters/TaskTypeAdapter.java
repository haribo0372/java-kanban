package http.handlers.util.type.adapters;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import models.Task;

import java.lang.reflect.Type;

public class TaskTypeAdapter implements JsonSerializer<Task> {
    @Override
    public JsonElement serialize(Task subtask, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("id", subtask.getId());
        jsonObject.addProperty("name", subtask.getName());
        jsonObject.addProperty("description", subtask.getDescription());
        jsonObject.addProperty("taskStatus", subtask.getTaskStatus().name());
        if (subtask.getDuration() != null)
            jsonObject.addProperty("duration", subtask.getDuration().toString());

        if (subtask.getStartTime() != null)
            jsonObject.addProperty("startTime",
                    subtask.getStartTime().format(LocalDateTimeAdapter.getTimeFormatter()));

        return jsonObject;
    }
}
