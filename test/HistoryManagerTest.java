import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import org.junit.jupiter.api.Test;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    HistoryManager historyManager = new InMemoryHistoryManager();
    Task task1 = new Task("task1", "d1");

    @Test
    void getHistory() {
        historyManager.add(task1);
        assertEquals(1, (historyManager.getHistory()).size());
        historyManager.add(task1);
        assertEquals(1, (historyManager.getHistory()).size());
    }

    @Test
    void remove() {
        Task task2 = new Task("task2", "t2");
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task1.getId());
        assertEquals(1, historyManager.getHistory().size());
    }
}