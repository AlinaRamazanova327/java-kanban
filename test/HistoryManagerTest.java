import manager.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    HistoryManager historyManager = new InMemoryHistoryManager();
    FileBackedTaskManager manager;
    File file;
    private Task task1, task2, task3;

    @BeforeEach
    void setUp() {
        try {
            file = File.createTempFile("testTasks", ".csv");
            String stringFile = String.valueOf(file);
            manager = new FileBackedTaskManager(stringFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        task1 = new Task("task1", "", TaskStatus.NEW,
                LocalDateTime.of(2026, Month.APRIL, 1, 10, 0), Duration.ofHours(1));
        task2 = new Task("task2", "", TaskStatus.DONE,
                LocalDateTime.of(2024, Month.APRIL, 1, 10, 0), Duration.ofHours(1));
        task3 = new Task("task3", "", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2025, Month.APRIL, 1, 10, 0), Duration.ofHours(1));
    }

    @Test
    void getEmptyHistory() {
        List<Task> emptyList = historyManager.getHistory();
        assertEquals(emptyList.size(), 0);
    }

    @Test
    void AddDuplicateTask() {
        historyManager.add(task1);
        int size = historyManager.getHistory().size();
        historyManager.add(task1);
        assertEquals(size, historyManager.getHistory().size());
    }

    @Test
    void addTasksAndGetHistory() {
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        List<Task> expectedOrder = new ArrayList<>(List.of(task1, task2, task3));
        assertEquals(expectedOrder, historyManager.getHistory());
    }

    @Test
    void removeFromBeginning() {
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task1.getId());
        assertIterableEquals(List.of(task2, task3), historyManager.getHistory());
    }

    @Test
    void removeFromMiddle() {
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task2.getId());
        assertIterableEquals(List.of(task1, task3), historyManager.getHistory());
    }

    @Test
    void removeFromEnd() {
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task3.getId());
        assertIterableEquals(List.of(task1, task2), historyManager.getHistory());
    }

    @Test
    void removeNonExistingTask() {
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(-1);
        assertIterableEquals(List.of(task1, task2, task3), historyManager.getHistory());
    }

    @Test
    void remove() {
        manager.addTask(task1);
        manager.addTask(task2);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task1.getId());
        assertEquals(1, historyManager.getHistory().size());
    }
}