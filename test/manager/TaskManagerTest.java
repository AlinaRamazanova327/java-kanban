package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    @BeforeEach
    void setUp() {
        taskManager = createTaskManager();
    }

    protected abstract T createTaskManager();

    @Test
    void shouldReturnTask1EqualsTask2() {
        Task task1 = new Task("task1", "d1");
        Task task2 = new Task("task1", "d1");
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        task2.setId(task1.getId());
        assertEquals(task1, task2);
    }

    @Test
    void shouldReturnSubtask1EqualsSubtask2() {
        Epic epic1 = new Epic("epic1", "epicD1");
        taskManager.addEpic(epic1);
        Subtask subtask1 = new Subtask("subtask1", "d1");
        Subtask subtask2 = new Subtask("subtask1", "d1");
        subtask2.setStatus(TaskStatus.NEW);
        subtask1.setStatus(TaskStatus.NEW);
        subtask1.epicId = epic1.getId();
        subtask2.epicId = epic1.getId();
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        subtask2.setId(subtask1.getId());
        assertEquals(subtask2, subtask1);
    }

    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        taskManager.addTask(task);
        Task savedTask = taskManager.getTaskById(task.getId());
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
        List<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void updateTask() {
        Task task1 = new Task("task1", "d1");
        taskManager.addTask(task1);
        Task task2 = new Task(task1.getId(), "new task1", "description2", TaskStatus.DONE,
                LocalDateTime.of(2025, Month.APRIL, 1, 12, 0), Duration.ofHours(2));
        taskManager.updateTask(task2);
        Task resultTask = taskManager.getTaskById(task1.getId());
        assertEquals("new task1", resultTask.getTitle());
        assertEquals("description2", resultTask.getDescription());
        assertEquals(TaskStatus.DONE, resultTask.getStatus());
        assertEquals(LocalDateTime.of(2025, Month.APRIL, 1, 12, 0), resultTask.getStartTime());
        assertEquals(Duration.ofHours(2), resultTask.getDuration());
    }

    @Test
    void removeEpicById() {
        Epic epic = new Epic("epic", "epicD");
        taskManager.addEpic(epic);
        taskManager.removeEpicById(epic.getId());
        assertNull(taskManager.getEpicById(epic.getId()));
        assertNull(taskManager.getAllSubtaskByEpic(epic));
    }

    private Epic createEpicWithSubtasks(String title, int numSubtasks, TaskStatus... statuses) {
        Epic epic = new Epic(title, "description");
        taskManager.addEpic(epic);
        for (int i = 0; i < numSubtasks; i++) {
            Subtask subtask = new Subtask("Subtask " + (i + 1), "description");
            subtask.setStatus(statuses[i % statuses.length]);
            subtask.epicId = epic.getId();
            taskManager.addSubtask(subtask);
        }
        return epic;
    }

    @Test
    void epicShouldBeNewWhenAllSubtasksAreNew() {
        Epic epic = createEpicWithSubtasks("epic", 2, TaskStatus.NEW);
        taskManager.updateEpic(epic);
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    void epicShouldBeDoneWhenAllSubtasksAreDone() {
        Epic epic = createEpicWithSubtasks("epic", 2, TaskStatus.DONE);
        taskManager.updateEpic(epic);
        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    void epicShouldBeInProgressWhenSubtasksAreNewAndDone() {
        Epic epic = createEpicWithSubtasks("epic", 2, TaskStatus.DONE, TaskStatus.NEW);
        taskManager.updateEpic(epic);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void epicShouldBeInProgressWhenOneOrMoreSubtasksInProgress() {
        Epic epic = createEpicWithSubtasks("epic", 2, TaskStatus.DONE, TaskStatus.IN_PROGRESS);
        taskManager.updateEpic(epic);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void subtaskMustHaveAssociatedEpic() {
        Epic epic = createEpicWithSubtasks("epic", 1, TaskStatus.IN_PROGRESS);
        taskManager.updateEpic(epic);
        Subtask subtask = taskManager.getSubtaskById(epic.subtaskIds.getFirst());
        assertEquals(epic.getId(), subtask.epicId);
        taskManager.removeSubtaskById(epic.subtaskIds.getFirst());
        assertFalse(epic.subtaskIds.contains(subtask.getId()));
    }
}