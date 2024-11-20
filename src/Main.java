import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

public class Main {

    public static void main(String[] args) {

        TaskManager manager = Managers.getDefault();

        Task task1 = new Task("task1", "description1");
        task1.setStatus(TaskStatus.NEW);
        manager.addTask(task1);

        Task task2 = new Task("task2", "description2");
        task2.setStatus(TaskStatus.NEW);
        manager.addTask(task2);

        Epic epic1 = new Epic("epic1", "description1");
        manager.addEpic(epic1);

        Subtask subtask1 = new Subtask("subtask1", "description1");
        subtask1.epicId = epic1.getId();
        subtask1.setStatus(TaskStatus.NEW);
        manager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("subtask2", "description2");
        subtask2.epicId = epic1.getId();
        subtask2.setStatus(TaskStatus.NEW);
        manager.addSubtask(subtask2);

        Subtask subtask3 = new Subtask("subtask3", "description3");
        subtask3.epicId = epic1.getId();
        subtask3.setStatus(TaskStatus.NEW);
        manager.addSubtask(subtask3);

        Epic epic2 = new Epic("epic2", "description2");
        manager.addEpic(epic2);

        System.out.println(manager.getTaskById(2));
        System.out.println(manager.getTaskById(1));
        System.out.println(manager.getTaskById(2));
        System.out.println("История:" + manager.getHistory());
        System.out.println(manager.getTaskById(2));
        System.out.println(manager.getEpicById(3));
        System.out.println(manager.getTaskById(1));
        System.out.println("История:" + manager.getHistory());
        System.out.println(manager.getSubtaskById(4));
        System.out.println(manager.getTaskById(1));
        System.out.println(manager.getEpicById(3));
        System.out.println(manager.getSubtaskById(6));
        System.out.println(manager.getEpicById(7));
        System.out.println(manager.getSubtaskById(5));
        System.out.println("История:" + manager.getHistory());
        manager.removeTaskById(2);
        System.out.println("История:" + manager.getHistory());
        System.out.println(manager.getAllSubtaskByEpic(epic1));
        manager.removeEpicById(3);
        System.out.println("История:" + manager.getHistory());
    }
}
