import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Test;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    TaskManager taskManager = Managers.getDefault();
    HistoryManager historyManager = new InMemoryHistoryManager();

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
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task1.getId());
        assertEquals(1, historyManager.getHistory().size());
    }
}