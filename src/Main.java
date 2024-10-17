import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

public class Main {

    public static void main(String[] args) {

        System.out.println("Поехали!");

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

        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getAllEpics()) {
            System.out.println(epic);
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
        System.out.println(manager.getTaskById(1));
        System.out.println(manager.getTaskById(2));
        System.out.println(manager.getEpicById(3));
        manager.updateSubtask(subtask1, "subtask1", "descr1", TaskStatus.DONE);
        System.out.println("Подзадачи в эпике" + manager.getAllSubtaskByEpic(epic1));
        System.out.println(manager.getEpicById(3));
        System.out.println(manager.getSubtaskById(4));
        System.out.println(manager.getSubtaskById(5));
        System.out.println("История:" + manager.getHistory());
        System.out.println(manager.getTaskById(1));
        System.out.println(manager.getTaskById(2));
        System.out.println(manager.getEpicById(3));
        System.out.println("История:" + manager.getHistory());
    }
}
