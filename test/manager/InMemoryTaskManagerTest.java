package manager;

import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @Test
    void isIntersectionCheck() {
        Task task = new Task("existing task", "", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.APRIL, 1, 10, 0), Duration.ofHours(2));
        taskManager.addTask(task);
        Task newTask = new Task("new task", "", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.APRIL, 1, 11, 30), Duration.ofHours(2));
        assertTrue(taskManager.isIntersectionCheck(newTask), "Задачи должны пересекаться.");
        Task newTask2 = new Task("new task", "", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.APRIL, 1, 12, 0), Duration.ofHours(2));
        assertFalse(taskManager.isIntersectionCheck(newTask2), "Задачи не должны пересекаться.");
        Task newTask3 = new Task("new task", "", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.APRIL, 1, 13, 0), Duration.ofHours(2));
        assertFalse(taskManager.isIntersectionCheck(newTask3), "Задачи не должны пересекаться.");
        Task newTask4 = new Task("new task", "", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.APRIL, 1, 10, 30), Duration.ofHours(1));
        assertTrue(taskManager.isIntersectionCheck(newTask4), "Задачи должны пересекаться.");
    }
}
