import manager.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    HistoryManager historyManager = new InMemoryHistoryManager();
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
    void getHistory() {
        Task task1 = new Task("task1", "d1");
        historyManager.add(task1);
        assertEquals(1, (historyManager.getHistory()).size());
        historyManager.add(task1);
        assertEquals(1, (historyManager.getHistory()).size());
    }

    @Test
    void remove() {
        Task task1 = new Task("task1", "d1");
        Task task2 = new Task("task2", "t2");
        manager.addTask(task1);
        manager.addTask(task2);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task1.getId());
        assertEquals(1, historyManager.getHistory().size());
    }
}