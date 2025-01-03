package manager;

import tasks.*;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    private int nextId = 1;
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public void addTask(Task task) {
        if (task.getId() == 0) {
            task.setId(nextId++);
        }
        tasks.put(task.getId(), task);
        task.setTaskType(TaskType.TASK);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (subtask.getId() == 0) {
            subtask.setId(nextId++);
        }
        subtasks.put(subtask.getId(), subtask);
        subtask.setTaskType(TaskType.SUBTASK);
        Epic epic = epics.get(subtask.epicId);
        if (epic != null) {
            updateEpic(epic, epic.getTitle(), epic.getDescription());
            epic.subtaskIds.add(subtask.getId());
        }
    }

    @Override
    public void addEpic(Epic epic) {
        if (epic.getId() == 0) {
            epic.setId(nextId++);
        }
        epics.put(epic.getId(), epic);
        epic.setStatus(TaskStatus.NEW);
        epic.setTaskType(TaskType.EPIC);
    }

    @Override
    public void updateTask(Task task, String title,
                           String description, TaskStatus status) {
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(status);
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask, String title,
                              String description, TaskStatus status) {
        subtask.setTitle(title);
        subtask.setDescription(description);
        subtask.setStatus(status);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.epicId);
        updateEpic(epic, epic.getTitle(), epic.getDescription());
    }

    @Override
    public void updateEpic(Epic epic, String title, String description) {
        epic.setTitle(title);
        epic.setDescription(description);
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

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void clearTask() {
        tasks.clear();
    }

    @Override
    public void clearSubtask() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.subtaskIds.clear();
            epic.setStatus(TaskStatus.NEW);
        }
    }

    @Override
    public void clearEpic() {
        subtasks.clear();
        epics.clear();
    }

    @Override
    public Task getTaskById(int taskId) {
        Task task = tasks.get(taskId);
        if (task != null) {
            historyManager.add(task);
            return task;
        }
        return null;
    }

    @Override
    public Subtask getSubtaskById(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        if (subtask != null) {
            historyManager.add(subtask);
            return subtask;
        }
        return null;
    }

    @Override
    public Epic getEpicById(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            historyManager.add(epic);
            return epic;
        }
        return null;
    }

    @Override
    public void removeTaskById(int taskId) {
        historyManager.remove(taskId);
        tasks.remove(taskId);
    }

    @Override
    public void removeSubtaskById(int subtaskId) {
        if (subtasks.containsKey(subtaskId)) {
            Epic epic = epics.get((subtasks.get(subtaskId)).epicId);
            historyManager.remove(subtaskId);
            subtasks.remove(subtaskId);
            updateEpic(epic, epic.getTitle(), epic.getDescription());
        }
    }

    @Override
    public void removeEpicById(int epicId) {
        for (Integer subtaskId : epics.get(epicId).subtaskIds) {
            historyManager.remove(subtaskId);
            subtasks.remove(subtaskId);
        }
        historyManager.remove(epicId);
        epics.remove(epicId);
    }

    @Override
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

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
