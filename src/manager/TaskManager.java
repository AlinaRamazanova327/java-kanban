package manager;

import tasks.*;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private int nextId = 1;

    public void addTask(Task task) {
        task.setId(nextId++);
        tasks.put(task.getId(), task);
        task.setTaskType(TaskType.TASK);
    }

    public void addSubtask(Subtask subtask) {
        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.epicId);
        epic.subtaskIds.add(subtask.getId());
        subtask.setTaskType(TaskType.SUBTASK);
        updateEpic(epic);
    }

    public void addEpic(Epic epic) {
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
        epic.setTaskType(TaskType.EPIC);
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.epicId);
        updateEpic(epic);
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);

        int statusNew = 0;
        int statusDone = 0;
        int total = epic.subtaskIds.size();
        for (Integer subtaskId : epic.subtaskIds) {
            Subtask subtaskInEpic = subtasks.get(subtaskId);

            if (subtaskInEpic != null) {
                switch (subtaskInEpic.getStatus()) {
                    case TaskStatus.DONE:
                        statusDone++;
                        break;
                    case TaskStatus.NEW:
                        statusNew++;
                        break;
                }
                if (total == statusDone) {
                    epic.setStatus(TaskStatus.DONE);
                    break;
                }
                if (total == statusNew) {
                    epic.setStatus(TaskStatus.NEW);
                    break;
                } else {
                    epic.setStatus(TaskStatus.IN_PROGRESS);
                }
            }
        }
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public void clearTask() {
        tasks.clear();
    }

    public void clearSubtask() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.subtaskIds.clear();
            epic.setStatus(TaskStatus.NEW);
        }
    }

    public void clearEpic() {
        subtasks.clear();
        epics.clear();
    }

    public Task getTaskById(int taskId) {
        if (tasks.containsKey(taskId)) {
            return tasks.get(taskId);
        }
        return null;
    }

    public Subtask getSubtaskById(int subtaskId) {
        if (subtasks.containsKey(subtaskId)) {
            return subtasks.get(subtaskId);
        }
        return null;
    }

    public Epic getEpicById(int epicId) {
        if (epics.containsKey(epicId)) {
            return epics.get(epicId);
        }
        return null;
    }

    public void removeTaskById(int taskId) {
        tasks.remove(taskId);
    }

    public void removeSubtaskById(int subtaskId) {
        if (subtasks.containsKey(subtaskId)) {
            Epic epic = epics.get((subtasks.get(subtaskId)).epicId);
            subtasks.remove(subtaskId);
            updateEpic(epic);
        }
    }

    public void removeEpicById(int epicId) {
        for (Integer subtaskId : epics.get(epicId).subtaskIds) {
            subtasks.remove(subtaskId);
        }
        epics.remove(epicId);
    }

    public List<Subtask> getAllSubtaskByEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            ArrayList<Subtask> list = new ArrayList<>();

            for (int i : epic.subtaskIds) {
                list.add(getSubtaskById(i));
            }
            return list;
        }
        return null;
    }
}
