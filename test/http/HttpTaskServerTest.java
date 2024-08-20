package http;

import com.google.gson.Gson;
import http.handlers.util.type.tokens.EpicListToken;
import http.handlers.util.type.tokens.SubTaskListToken;
import http.handlers.util.type.tokens.TaskListToken;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import models.Epic;
import models.SubTask;
import models.Task;
import models.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = taskServer.getGson();
    HttpClient client = HttpClient.newHttpClient();

    public HttpTaskServerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.deleteTasks();
        manager.deleteSubtasks();
        manager.deleteEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gson.toJson(task);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 1", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");

        Task task2 = new Task("", "",
                TaskStatus.NEW, Duration.ofDays(3), task.getStartTime());
        String task2Json = gson.toJson(task2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(task2Json)).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response2.statusCode(), "Неверный ответ сервера");
        assertEquals(1, manager.getTasks().size(), "Некорректное количество задач");
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2", TaskStatus.NEW);
        manager.addNewTask(task);

        URI url = URI.create(String.format("http://localhost:8080/tasks/%s", task.getId()));
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task taskFromHttp = gson.fromJson(response.body(), Task.class);
        assertEquals(task.toStringCSV(), taskFromHttp.toStringCSV(), "Задачи не равны");
        assertTrue(manager.getTasks().contains(taskFromHttp));

        manager.deleteTask(task.getId());
        HttpResponse<Void> response2 = client.send(request, HttpResponse.BodyHandlers.discarding());
        assertEquals(404, response2.statusCode());

        URI url2 = URI.create("http://localhost:8080/tasks/L");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).GET().build();
        HttpResponse<String> response3 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response3.statusCode());
    }

    @Test
    void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Test", "Testing task",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.addNewTask(task);

        URI url = URI.create(String.format("http://localhost:8080/tasks/%s", task.getId()));
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(manager.getTasks().isEmpty());

        URI url2 = URI.create("http://localhost:8080/tasks/L");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).DELETE().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response2.statusCode());
    }

    @Test
    void testGetTasks() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1", TaskStatus.NEW);
        manager.addNewTask(task);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> taskList = gson.fromJson(response.body(), new TaskListToken().getType());

        assertEquals(1, taskList.size());
        assertEquals(task, taskList.getFirst());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Test epic 1");

        SubTask subTask1 = new SubTask("Subtask 1", "Test subtask 1",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());

        SubTask subTask2 = new SubTask("", "",
                TaskStatus.NEW, Duration.ofDays(3), subTask1.getStartTime());

        epic.addNewSubTask(subTask1);
        manager.addNewEpic(epic);

        String subtaskJson = gson.toJson(subTask1);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<SubTask> subtasksFromManager = manager.getSubtasks();

        assertFalse(subtasksFromManager.isEmpty(), "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Subtask 1", subtasksFromManager.getFirst().getName(), "Некорректное имя подзадачи");

        epic.addNewSubTask(subTask2);
        String subtask2Json = gson.toJson(subTask2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtask2Json)).build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response2.statusCode(), "Неверный ответ сервера");
        assertEquals(1, manager.getSubtasks().size(), "Некорректное количество подзадач");
    }

    @Test
    public void testGetSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Test epic 1");

        SubTask subTask1 = new SubTask("Subtask 1", "Test subtask 1",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());

        epic.addNewSubTask(subTask1);
        manager.addNewEpic(epic);
        manager.addNewSubtask(subTask1);

        URI url = URI.create(String.format("http://localhost:8080/subtasks/%s", subTask1.getId()));
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        SubTask subtaskFromHttp = gson.fromJson(response.body(), SubTask.class);
        assertEquals(subTask1.toStringCSV(), subtaskFromHttp.toStringCSV(), "Подадачи не равны");
        assertTrue(manager.getSubtasks().contains(subtaskFromHttp));

        manager.deleteSubtask(subTask1.getId());
        HttpResponse<Void> response2 = client.send(request, HttpResponse.BodyHandlers.discarding());
        assertEquals(404, response2.statusCode());

        URI url2 = URI.create("http://localhost:8080/subtasks/L");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).GET().build();
        HttpResponse<String> response3 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response3.statusCode());
    }

    @Test
    void testDeleteSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Test epic 1");

        SubTask subTask1 = new SubTask("Subtask 1", "Test subtask 1",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());

        epic.addNewSubTask(subTask1);
        manager.addNewEpic(epic);
        manager.addNewSubtask(subTask1);

        URI url = URI.create(String.format("http://localhost:8080/subtasks/%s", subTask1.getId()));
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(manager.getSubtasks().isEmpty());

        URI url2 = URI.create("http://localhost:8080/subtasks/L");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).DELETE().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response2.statusCode());
    }

    @Test
    void testGetSubTasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Test epic 1");

        SubTask subTask1 = new SubTask("Subtask 1", "Test subtask 1",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());

        epic.addNewSubTask(subTask1);
        manager.addNewEpic(epic);
        manager.addNewSubtask(subTask1);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<SubTask> subTaskList = gson.fromJson(response.body(), new SubTaskListToken().getType());

        assertEquals(1, subTaskList.size());
        assertEquals(subTask1, subTaskList.getFirst());
        assertEquals(200, response.statusCode());
    }

    @Test
    void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Test epic 1");

        String epicJson = gson.toJson(epic);
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getEpics().size(), "Эпик не сохранился в менеджере");
        assertEquals(epic.getName(), manager.getEpics().getFirst().getName(), "Некорректное имя эпик");

        Epic epicWithSubTask = new Epic("Epic 2", "Test epic 2");
        epicWithSubTask.addNewSubTask(
                new SubTask("Subtask 1", "Test subtask 1", TaskStatus.NEW),
                new SubTask("Subtask 2", "Test subtask 2", TaskStatus.NEW),
                new SubTask("Subtask 3", "Test subtask 3", TaskStatus.NEW));

        String epicJson2 = gson.toJson(epicWithSubTask);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson2)).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response2.statusCode());
        assertEquals(2, manager.getEpics().size(), "Эпик с подзадачами не сохранился в менеджере");
    }

    @Test
    void testGetEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Test epic 1");
        manager.addNewEpic(epic);

        URI url = URI.create(String.format("http://localhost:8080/epics/%s", epic.getId()));
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Epic epicFromHttp = gson.fromJson(response.body(), Epic.class);

        assertEquals(epic.toStringCSV(), epicFromHttp.toStringCSV(), "Эпики не равны");
        assertTrue(manager.getEpics().contains(epicFromHttp));

        manager.deleteEpic(epic.getId());
        HttpResponse<Void> response2 = client.send(request, HttpResponse.BodyHandlers.discarding());
        assertEquals(404, response2.statusCode());

        URI url2 = URI.create("http://localhost:8080/epics/L");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).GET().build();
        HttpResponse<String> response3 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response3.statusCode());
    }

    @Test
    void testDeleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Test epic 1");
        manager.addNewEpic(epic);

        URI url = URI.create(String.format("http://localhost:8080/epics/%s", epic.getId()));
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(manager.getEpics().isEmpty());

        URI url2 = URI.create("http://localhost:8080/epics/L");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).DELETE().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response2.statusCode());
    }

    @Test
    void testGetEpicSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 2", "Test epic 2");
        epic.addNewSubTask(
                new SubTask("Subtask 1", "Test subtask 1", TaskStatus.NEW),
                new SubTask("Subtask 2", "Test subtask 2", TaskStatus.NEW),
                new SubTask("Subtask 3", "Test subtask 3", TaskStatus.NEW)
        );
        manager.addNewEpic(epic);
        epic.getSubTasks().forEach(manager::addNewSubtask);

        URI url = URI.create(String.format("http://localhost:8080/epics/%s/subtasks", epic.getId()));
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<SubTask> subTasksFromHttp = gson.fromJson(response.body(), new SubTaskListToken().getType());
        List<SubTask> subTasksFromEpic = new ArrayList<>(epic.getSubTasks());
        assertEquals(subTasksFromEpic.size(), subTasksFromHttp.size(), "Подзадачи неверно передаются");

        Comparator<Task> comparator = Comparator.comparingInt(Task::getId);
        subTasksFromHttp.sort(comparator);
        subTasksFromEpic.sort(comparator);
        for (int i = 0; i < subTasksFromEpic.size(); i++)
            assertEquals(subTasksFromHttp.get(i), subTasksFromEpic.get(i),
                    "Полученные подзадачи не соответсвуют существующим");

        manager.deleteEpic(epic.getId());
        HttpResponse<String> response2 = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response2.statusCode());

        URI url2 = URI.create("http://localhost:8080/epics/L/subtasks");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).GET().build();
        HttpResponse<String> response3 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response3.statusCode());
    }

    @Test
    void testGetEpics() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 2", "Test epic 2");
        epic.addNewSubTask(
                new SubTask("Subtask 1", "Test subtask 1", TaskStatus.NEW),
                new SubTask("Subtask 2", "Test subtask 2", TaskStatus.NEW),
                new SubTask("Subtask 3", "Test subtask 3", TaskStatus.NEW)
        );
        manager.addNewEpic(epic);

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Epic> epicList = gson.fromJson(response.body(), new EpicListToken().getType());
        assertEquals(1, epicList.size());
        assertEquals(epic, epicList.getFirst());
        assertEquals(epic.getSubTasks().size(), epicList.getFirst().getSubTasks().size());
    }

    @Test
    void testGetHistory() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        Task task1 = new Task("name_1", "description_1", TaskStatus.NEW);
        Task task2 = new Task("name_2", "description_2", TaskStatus.NEW);

        manager.addNewTask(task1);
        manager.addNewTask(task2);

        HttpResponse<String> response1 = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response1.statusCode());
        List<Task> history1 = gson.fromJson(response1.body(), new TaskListToken().getType());
        assertTrue(history1.isEmpty());

        manager.getTask(task1.getId());
        HttpResponse<String> response2 = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response2.statusCode());
        List<Task> history2 = gson.fromJson(response2.body(), new TaskListToken().getType());
        assertEquals(1, history2.size());
        assertEquals(task1, history2.getLast());

        manager.getTask(task2.getId());
        HttpResponse<String> response3 = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response3.statusCode());
        List<Task> history3 = gson.fromJson(response3.body(), new TaskListToken().getType());
        assertEquals(2, history3.size());
        assertEquals(task2, history3.getLast(), history3.toString());
    }

    @Test
    void testGetPrioritized() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        Task task1 = new Task("Test 1", "Testing task 1",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        Task task2 = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, Duration.ofHours(3), LocalDateTime.now());
        Task task3 = new Task("Test 3", "Testing task 3",
                TaskStatus.NEW, Duration.ofMinutes(5), task2.getEndTime());

        manager.addNewTask(task1);
        manager.addNewTask(task2);
        manager.addNewTask(task3);

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> prioritizedFromHttp = gson.fromJson(response.body(), new TaskListToken().getType());
        assertEquals(2, prioritizedFromHttp.size());
        assertFalse(prioritizedFromHttp.contains(task2));
        assertTrue(prioritizedFromHttp.contains(task1));
        assertTrue(prioritizedFromHttp.contains(task3));
    }
}

