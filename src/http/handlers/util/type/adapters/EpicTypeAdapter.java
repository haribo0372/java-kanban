package http.handlers.util.type.adapters;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import models.Epic;

import java.lang.reflect.Type;

public class EpicTypeAdapter implements JsonSerializer<Epic> {
    @Override
    public JsonElement serialize(Epic epic, Type type, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("id", epic.getId());
        jsonObject.addProperty("name", epic.getName());
        jsonObject.addProperty("description", epic.getDescription());
        jsonObject.addProperty("taskStatus", epic.getTaskStatus().name());
        if (epic.getDuration() != null)
            jsonObject.addProperty("duration", epic.getDuration().toString());

        if (epic.getStartTime() != null)
            jsonObject.addProperty("startTime", epic.getStartTime().format(LocalDateTimeAdapter.getTimeFormatter()));

        jsonObject.add("subTasks", context.serialize(epic.getSubTasks()));
        return jsonObject;

    }
}
