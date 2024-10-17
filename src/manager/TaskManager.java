package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

public interface TaskManager {

    void addTask(Task task);

    void addSubtask(Subtask subtask);

    void addEpic(Epic epic);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    List<Task> getAllTasks();

    List<Subtask> getAllSubtasks();

    List<Epic> getAllEpics();

    void clearTask();

    void clearSubtask();

    void clearEpic();

    Task getTaskById(int taskId);

    Subtask getSubtaskById(int subtaskId);

    Epic getEpicById(int epicId);

    void removeTaskById(int taskId);

    void removeSubtaskById(int subtaskId);

    void removeEpicById(int epicId);

    List<Subtask> getAllSubtaskByEpic(Epic epic);

    List<Task> getHistory();
}
