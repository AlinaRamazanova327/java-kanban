import manager.FileBackedTaskManager;
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

public class Main {

    public static void main(String[] args) {

        String fileName = "tasks.csv";
        File file = new File(fileName);
        TaskManager manager = Managers.getDefault(fileName);

        manager.addTask(new Task("task1", "", TaskStatus.NEW,
                LocalDateTime.of(2022, Month.APRIL, 1, 12, 0), Duration.ofHours(1)));

        manager.addTask(new Task("task2", "", TaskStatus.NEW,
                LocalDateTime.of(2023, Month.APRIL, 1, 12, 0), Duration.ofHours(1)));

        Epic epic1 = new Epic("epic1", "description1");
        manager.addEpic(epic1);

        manager.addSubtask(new Subtask("subtask1", "description1", TaskStatus.NEW,
                LocalDateTime.of(2023, Month.APRIL, 1, 12, 0), Duration.ofHours(1),
                epic1.getId()));

        manager.addSubtask(new Subtask("subtask2", "description2", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.APRIL, 1, 12, 0), Duration.ofHours(1),
                epic1.getId()));

        manager.addSubtask(new Subtask("subtask3", "description3", TaskStatus.NEW,
                LocalDateTime.of(2023, Month.AUGUST, 1, 12, 0), Duration.ofHours(1),
                epic1.getId()));

        Epic epic2 = new Epic("epic2", "description2");
        manager.addEpic(epic2);
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);
        System.out.println(loaded.getTaskById(2));
        System.out.println(loaded.getTaskById(1));
        System.out.println(loaded.getTaskById(2));
        System.out.println(loaded.getAllSubtaskByEpic(epic1));
        System.out.println(loaded.getEpicById(3));
        System.out.println("История:" + loaded.getHistory());
        System.out.println(manager.getPrioritizedTasks());
        System.out.println(loaded.getTaskById(2));
        System.out.println(loaded.getEpicById(3));
        System.out.println(loaded.getTaskById(1));
        System.out.println("История:" + loaded.getHistory());
        System.out.println(loaded.getSubtaskById(4));
        System.out.println(loaded.getTaskById(1));
        System.out.println(loaded.getEpicById(3));
        System.out.println(loaded.getSubtaskById(6));
        System.out.println(loaded.getEpicById(7));
        System.out.println(loaded.getSubtaskById(5));
        System.out.println("История:" + loaded.getHistory());
    }
}
