package managers;

import models.Epic;
import models.SubTask;
import models.Task;
import models.TaskStatus;
import util.ManagerSaveException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static void main(String[] args) {
        File exampleFile = new File("example.txt");
        FileBackedTaskManager taskManager = getTaskManager(exampleFile);

        FileBackedTaskManager newTaskManager = FileBackedTaskManager.loadFromFile(exampleFile);

        String s = "%s = %s : %s\n";
        for (Task t : taskManager.tasks.values()) {
            Task tempTask = newTaskManager.tasks.get(t.getId());
            System.out.printf(s, t.getName(), tempTask.getName(), t.equals(tempTask));
        }
        for (Epic t : taskManager.epics.values()) {
            Task tempTask = newTaskManager.epics.get(t.getId());
            System.out.printf(s, t.getName(), tempTask.getName(), t.equals(tempTask));
        }
        for (SubTask t : taskManager.subtasks.values()) {
            Task tempTask = newTaskManager.subtasks.get(t.getId());
            System.out.printf(s, t.getName(), tempTask.getName(), t.equals(tempTask));
        }
    }

    private static FileBackedTaskManager getTaskManager(File exampleFile) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(exampleFile);

        Task task = new Task("task_name_1", "task_description_1", TaskStatus.NEW);
        Epic epic1 = new Epic("epic_name_1", "epic_description_1");
        Epic epic2 = new Epic("epic_name_2", "epic_description_2");

        SubTask subTask1 = new SubTask("subtask_name_1", "subtask_description_1", TaskStatus.NEW);
        subTask1.setCurrentEpic(epic1);

        SubTask subTask2 = new SubTask("subtask_name_2", "subtask_description_2", TaskStatus.NEW);
        subTask2.setCurrentEpic(epic1);

        SubTask subTask3 = new SubTask("subtask_name_3", "subtask_description_3", TaskStatus.NEW);
        subTask3.setCurrentEpic(epic2);

        taskManager.addNewTask(task);
        taskManager.addNewEpic(epic1);
        taskManager.addNewSubtask(subTask1);
        taskManager.addNewSubtask(subTask2);
        taskManager.addNewEpic(epic2);
        taskManager.addNewSubtask(subTask3);
        return taskManager;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager backedTaskManager = new FileBackedTaskManager(file);
        List<Integer> epicsId = new ArrayList<>();
        List<SubTask> subTaskArrayList = new ArrayList<>();
        String[] strings;
        try {
            strings = Files.readString(file.toPath()).split("\n");
            for (int i = 1; i < strings.length; i++) {
                String[] currentTask = strings[i].split(",");
                if (currentTask[1].equals("TASK")) {
                    Task task = new Task(currentTask[2], currentTask[4], validateTaskStatus(currentTask[3]));
                    task.setId(Integer.parseInt(currentTask[0]));
                    backedTaskManager.tasks.put(task.getId(), task);
                } else if (currentTask[1].equals("EPIC")) {
                    Epic epic = new Epic(currentTask[2], currentTask[4]);
                    epic.setId(Integer.parseInt(currentTask[0]));
                    backedTaskManager.epics.put(epic.getId(), epic);
                } else if (currentTask[1].equals("SUBTASK")) {
                    SubTask subTask = new SubTask(currentTask[2], currentTask[4], validateTaskStatus(currentTask[3]));
                    subTask.setId(Integer.parseInt(currentTask[0]));

                    if (!currentTask[5].equals("null")) {
                        subTaskArrayList.add(subTask);
                        epicsId.add(Integer.parseInt(currentTask[5]));
                    }
                }
                backedTaskManager.serial++;
            }

            for (int i = 0; i < subTaskArrayList.size(); i++) {
                Epic epic = backedTaskManager.epics.get(epicsId.get(i));
                SubTask subTask = subTaskArrayList.get(i);
                if (epic != null) {
                    subTask.setCurrentEpic(epic);
                    epic.addNewSubTask(subTask);
                    backedTaskManager.subtasks.put(subTask.getId(), subTask);
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось загрузить данные из файла");
        }
        return backedTaskManager;
    }

    private static TaskStatus validateTaskStatus(String taskStatus) {
        if (taskStatus.equals("NEW")) return TaskStatus.NEW;
        if (taskStatus.equals("IN_PROGRESS")) return TaskStatus.IN_PROGRESS;
        else return TaskStatus.DONE;
    }

    private void save() {
        List<Task> tasks = super.getTasks();
        List<Epic> epics = super.getEpics();
        List<SubTask> subTasks = super.getSubtasks();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic\n");
            tasks.forEach(i -> {
                try {
                    writer.write(i.toStringCSV() + '\n');
                } catch (IOException e) {
                    throw new ManagerSaveException("Не удалось сохранить данные на диск");
                }
            });

            epics.forEach(i -> {
                try {
                    writer.write(i.toStringCSV() + '\n');
                } catch (IOException e) {
                    throw new ManagerSaveException("Не удалось сохранить данные на диск");
                }
            });

            subTasks.forEach(i -> {
                try {
                    writer.write(i.toStringCSV() + '\n');
                } catch (IOException e) {
                    throw new ManagerSaveException("Не удалось сохранить данные на диск");
                }
            });
        } catch (IOException e) {
            System.out.println("Не удалось сохранить данные на диск");
        }
    }

    @Override
    public int addNewTask(Task task) {
        int id = super.addNewTask(task);
        save();
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        int id = super.addNewEpic(epic);
        save();
        return id;
    }

    @Override
    public int addNewSubtask(SubTask subtask) {
        int id = super.addNewSubtask(subtask);
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(SubTask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }
}
