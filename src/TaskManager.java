import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private int nextId = 1;

    public void addTask(Task task) {
        task.setId(nextId++);
        tasks.put(task.getId(), task);
        task.setTaskType(TaskType.TASK);
    }

    public void addSubtask(Subtask subtask) {
        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);
        Epic col = epics.get(subtask.epicId);
        col.subtaskIds.add(subtask.getId());
        subtask.setTaskType(TaskType.SUBTASK);

        for (Integer subtaskId : col.subtaskIds) {
            Subtask subtaskInEpic = subtasks.get(subtaskId);
            int statusNew = 0;
            int statusDone = 0;
            int total = col.subtaskIds.size();

            switch (subtaskInEpic.getStatus()) {
                case DONE:
                    statusDone++;
                    break;
                case NEW:
                    statusNew++;
                    break;
            }
            if (total == statusDone) {
                col.setStatus(TaskStatus.DONE);
                break;
            }
            if (total == statusNew) {
                col.setStatus(TaskStatus.NEW);
                break;
            } else {
                col.setStatus(TaskStatus.IN_PROGRESS);
            }
        }
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
        Epic col = epics.get(subtask.epicId);

        for (Integer subtaskId : col.subtaskIds) {
            Subtask subtaskInEpic = subtasks.get(subtaskId);
            int statusNew = 0;
            int statusDone = 0;
            int total = col.subtaskIds.size();

            switch (subtaskInEpic.getStatus()) {
                case DONE:
                    statusDone++;
                    break;
                case NEW:
                    statusNew++;
                    break;
            }
            if (total == statusDone) {
                col.setStatus(TaskStatus.DONE);
                break;
            }
            if (total == statusNew) {
                col.setStatus(TaskStatus.NEW);
                break;
            } else {
                col.setStatus(TaskStatus.IN_PROGRESS);
            }
        }
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);

        for (Integer subtaskId : epic.subtaskIds) {
            Subtask subtaskInEpic = subtasks.get(subtaskId);
            int statusNew = 0;
            int statusDone = 0;
            int total = epic.subtaskIds.size();
            if (subtaskInEpic != null) {
                switch (subtaskInEpic.getStatus()) {
                    case DONE:
                        statusDone++;
                        break;
                    case NEW:
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

    public Collection<Task> getAllTasks() {
        return tasks.values();
    }

    public Collection<Subtask> getAllSubtasks() {
        return subtasks.values();
    }

    public Collection<Epic> getAllEpics() {
        return epics.values();
    }

    public void clearTask() {
        tasks.clear();
    }

    public void clearSubtask() {
        subtasks.clear();
        epics.clear();
    }

    public void clearEpic() {
        subtasks.clear();
        epics.clear();
    }

    public Task getByIdTask(int TaskId) {
        if (tasks.containsKey(TaskId)) {
            return tasks.get(TaskId);
        }
        return null;
    }

    public Subtask getByIdSubtask(int SubtaskId) {
        if (subtasks.containsKey(SubtaskId)) {
            return subtasks.get(SubtaskId);
        }
        return null;
    }

    public Epic getByIdEpic(int EpicId) {
        if (epics.containsKey(EpicId)) {
            return epics.get(EpicId);
        }
        return null;
    }

    public void removeByIdTask(int TaskId) {
        tasks.remove(TaskId);
    }

    public void removeByIdSubtask(int SubtaskId) {
        if (subtasks.containsKey(SubtaskId)) {
            Epic epicForUpdate = epics.get((subtasks.get(SubtaskId)).epicId);
            subtasks.remove(SubtaskId);
            updateEpic(epicForUpdate);
        }
    }

    public void removeByIdEpic(int EpicId) {
        for (Integer subtaskId : epics.get(EpicId).subtaskIds) {
            subtasks.remove(subtaskId);
        }
        epics.remove(EpicId);
    }

    public ArrayList<Subtask> getAllSubtaskByEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            ArrayList<Subtask> list = new ArrayList<>();

            for (int i : epic.subtaskIds) {
                list.add(getByIdSubtask(i));
            }
            return list;
        }
        return null;
    }
}
