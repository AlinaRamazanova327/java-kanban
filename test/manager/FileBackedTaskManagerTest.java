package manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File file;

    @BeforeEach
    void initFile() {
        try {
            file = File.createTempFile("testTasks", ".csv");
            taskManager = new FileBackedTaskManager(file.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void cleanUp() {
        if (!file.delete()) {
            System.err.println("Файл не удалось удалить: " + file.getAbsolutePath());
        }
    }

    @Override
    protected FileBackedTaskManager createTaskManager() {
        return taskManager;
    }

    @Test
    void saveAndLoadFromFile() {
        Task task1 = new Task("TestTask", "Description");
        task1.setStatus(TaskStatus.NEW);
        taskManager.addTask(task1);
        Epic epic = new Epic("TestEpic", "Description");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("TestSubtask", "Description");
        subtask.setStatus(TaskStatus.NEW);
        subtask.epicId = epic.getId();
        taskManager.addSubtask(subtask);
        taskManager.save();
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
        taskManager.save();
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);
        assertTrue(loaded.getAllTasks().isEmpty());
        assertTrue(loaded.getAllSubtasks().isEmpty());
        assertTrue(loaded.getAllEpics().isEmpty());
    }
}