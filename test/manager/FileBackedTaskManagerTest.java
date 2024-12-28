package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    FileBackedTaskManager fbManager;
    File file;

    @BeforeEach
    void setUp() {
        try {
            file = File.createTempFile("testTasks", ".csv");
            String stringFile = String.valueOf(file);
            fbManager = new FileBackedTaskManager(stringFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void saveAndLoadFromFile() {

        Task task1 = new Task("TestTask", "Description");
        task1.setStatus(TaskStatus.NEW);
        fbManager.addTask(task1);
        Epic epic = new Epic("TestEpic", "Description");
        fbManager.addEpic(epic);
        Subtask subtask = new Subtask("TestSubtask", "Description");
        subtask.setStatus(TaskStatus.NEW);
        subtask.epicId = epic.getId();
        fbManager.addSubtask(subtask);
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);
        Task task2 = loaded.getTaskById(task1.getId());
        Epic epic2 = loaded.getEpicById(epic.getId());
        Subtask subtask2 = loaded.getSubtaskById(subtask.getId());
        assertEquals(task2, task1);
        assertEquals(epic, epic2);
        assertEquals(subtask, subtask2);
    }

    @Test
    void saveAndLoadEmptyFile() {
        fbManager.save();
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);
        for (Task task : loaded.getAllTasks()) {
            assertNull(task);
        }
        for (Subtask subtask : loaded.getAllSubtasks()) {
            assertNull(subtask);
        }
        for (Epic epic : loaded.getAllEpics()) {
            assertNull(epic);
        }
    }
}