package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {

    FileBackedTaskManager manager;
    File file;

    @BeforeEach
    void setUp() {
        try {
            file = File.createTempFile("testTasks", ".csv");
            String stringFile = String.valueOf(file);
            manager = new FileBackedTaskManager(stringFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldReturnTask1EqualsTask2() {
        Task task1 = new Task("task1", "d1");
        Task task2 = new Task("task1", "d1");
        manager.addTask(task1);
        manager.addTask(task2);
        task2.setId(task1.getId());
        assertEquals(task1, task2);
    }

    @Test
    void shouldReturnSubtask1EqualsSubtask2() {
        Epic epic1 = new Epic("epic1", "epicD1");
        manager.addEpic(epic1);
        Subtask subtask1 = new Subtask("subtask1", "d1");
        Subtask subtask2 = new Subtask("subtask1", "d1");
        subtask2.setStatus(TaskStatus.NEW);
        subtask1.setStatus(TaskStatus.NEW);
        subtask1.epicId = epic1.getId();
        subtask2.epicId = epic1.getId();
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        subtask2.setId(subtask1.getId());
        assertEquals(subtask2, subtask1);
    }

    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        manager.addTask(task);

        final Task savedTask = manager.getTaskById(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = manager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void updateTask() {
        Task task1 = new Task("task1", "d1");
        manager.updateTask(task1, "new task1", "description2", TaskStatus.DONE);
        assertEquals("new task1", manager.getTaskById(task1.getId()).getTitle());
        assertEquals("description2", manager.getTaskById(task1.getId()).getDescription());
        assertEquals(TaskStatus.DONE, manager.getTaskById(task1.getId()).getStatus());
    }

    @Test
    void removeEpicById() {
        Epic epic = new Epic("epic", "epicD");
        manager.addEpic(epic);
        manager.removeEpicById(epic.getId());
        assertNull(manager.getEpicById(epic.getId()));
    }
}