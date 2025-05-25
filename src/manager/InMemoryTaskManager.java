package manager;

import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    private int nextId = 1;
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    @Override
    public void addTask(Task task) {
        if (task.getId() == 0) {
            task.setId(nextId++);
        }
        tasks.put(task.getId(), task);
        task.setTaskType(TaskType.TASK);
        addToPrioritizedTasks(task);
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
            epic.subtaskIds.add(subtask.getId());
            updateEpic(epic);
            addToPrioritizedTasks(subtask);
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
    public void updateTask(Task task) {
        prioritizedTasks.remove(tasks.get(task.getId()));
        addToPrioritizedTasks(task);
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        prioritizedTasks.remove(subtasks.get(subtask.getId()));
        addToPrioritizedTasks(subtask);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.epicId);
        updateEpic(epic);
    }

    @Override
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
        List<Subtask> subtaskList = getAllSubtaskByEpic(epic);
        LocalDateTime startTime = subtaskList.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
        epic.setStartTime(startTime);

        Duration duration = subtaskList.stream()
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);
        epic.setDuration(duration);

        LocalDateTime endTime = subtaskList.stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);
        epic.setEndTime(endTime);
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
    public List<Subtask> getAllSubtaskByEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            return epic.subtaskIds.stream()
                    .map(subtasks::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return List.copyOf(prioritizedTasks);
    }

    private void addToPrioritizedTasks(Task task) {
        if (!isIntersectionCheck(task) && task.getStartTime() != null && task.getDuration() != null) {
            prioritizedTasks.add(task);
        }
    }

    protected boolean isIntersectionCheck(Task newTask) {
        if (newTask.getStartTime() != null && newTask.getDuration() != null) {
            return getPrioritizedTasks().stream()
                    .filter(task -> task.getId() != newTask.getId())
                    .filter(task -> task.getStartTime() != null && task.getEndTime() != null)
                    .anyMatch(task ->
                            newTask.getStartTime().isBefore(task.getEndTime()) &&
                                    newTask.getEndTime().isAfter(task.getStartTime())
                    );
        }
        return false;
    }

    @Override
    public void clearTask() {
        for (Task task : tasks.values()) {
            prioritizedTasks.remove(task);
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void clearSubtask() {
        prioritizedTasks.removeIf(task -> task instanceof Subtask);
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.subtaskIds.clear();
            epic.setStatus(TaskStatus.NEW);
            historyManager.remove(epic.getId());
        }
    }

    @Override
    public void clearEpic() {
        clearSubtask();
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
        prioritizedTasks.remove(tasks.get(taskId));
        historyManager.remove(taskId);
        tasks.remove(taskId);
    }

    @Override
    public void removeSubtaskById(int subtaskId) {
        if (subtasks.containsKey(subtaskId)) {
            Epic epic = epics.get((subtasks.get(subtaskId)).epicId);
            prioritizedTasks.remove(subtasks.get(subtaskId));
            historyManager.remove(subtaskId);
            subtasks.remove(subtaskId);
            if (epic != null) {
                epic.subtaskIds.remove(Integer.valueOf(subtaskId));
                updateEpic(epic);
            }
        }
    }

    @Override
    public void removeEpicById(int epicId) {
        for (Integer subtaskId : epics.get(epicId).subtaskIds) {
            prioritizedTasks.remove(subtasks.get(subtaskId));
            historyManager.remove(subtaskId);
            subtasks.remove(subtaskId);
        }
        historyManager.remove(epicId);
        epics.remove(epicId);
    }
}
