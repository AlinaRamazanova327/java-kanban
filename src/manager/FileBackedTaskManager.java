package manager;
import tasks.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final String file;

    public FileBackedTaskManager(String file) {
        this.file = file;
    }

    public void save() {
        try (Writer fileWriter = new FileWriter(file)) {
            fileWriter.write("id,type,name,status,description,epic\n");
            for (Task task : tasks.values()) {
                fileWriter.write(task.toString() + "\n");
            }
            for (Subtask subtask : subtasks.values()) {
                fileWriter.write(subtask.toString() + "\n");
            }
            for (Epic epic : epics.values()) {
                fileWriter.write(epic.toString() + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи данных.");
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file.getPath());
        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            br.readLine();
            while (br.ready()) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                Task task = fromString(line);
                switch (task.getTaskType()) {
                    case TASK -> manager.addTask(task);
                    case EPIC -> manager.addEpic((Epic) task);
                    case SUBTASK -> manager.addSubtask((Subtask) task);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения данных.");
        }
        return manager;
    }

    public static Task fromString(String value) {
        String[] params = value.split(",");
        if (params[1].equals("EPIC")) {
            Epic epic = new Epic(params[2], params[4]);
            epic.setId(Integer.parseInt(params[0]));
            epic.setStatus(TaskStatus.valueOf(params[3]));
            epic.setTaskType(TaskType.EPIC);
            return epic;
        } else if (params[1].equals("TASK")) {
            Task task = new Task(params[2], params[4]);
            task.setId(Integer.parseInt(params[0]));
            task.setStatus(TaskStatus.valueOf(params[3]));
            task.setTaskType(TaskType.TASK);
            return task;
        } else {
            Subtask subtask = new Subtask(params[2], params[4]);
            subtask.setId(Integer.parseInt(params[0]));
            subtask.epicId = Integer.parseInt(params[5]);
            subtask.setStatus(TaskStatus.valueOf(params[3]));
            subtask.setTaskType(TaskType.SUBTASK);
            return subtask;
        }
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void updateTask(Task task, String title, String description, TaskStatus status) {
        super.updateTask(task, title, description, status);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask, String title, String description, TaskStatus status) {
        super.updateSubtask(subtask, title, description, status);
        save();
    }

    @Override
    public void updateEpic(Epic epic, String title, String description) {
        super.updateEpic(epic, title, description);
        save();
    }

    @Override
    public void clearTask() {
        super.clearTask();
        save();
    }

    @Override
    public void clearSubtask() {
        super.clearSubtask();
        save();
    }

    @Override
    public void clearEpic() {
        super.clearEpic();
        save();
    }

    @Override
    public Task getTaskById(int taskId) {
        Task task = super.getTaskById(taskId);
        save();
        return task;
    }

    @Override
    public Subtask getSubtaskById(int subtaskId) {
        Subtask subtask = super.getSubtaskById(subtaskId);
        save();
        return subtask;
    }

    @Override
    public Epic getEpicById(int epicId) {
        Epic epic = super.getEpicById(epicId);
        save();
        return epic;
    }

    @Override
    public void removeTaskById(int taskId) {
        super.removeTaskById(taskId);
        save();
    }

    @Override
    public void removeSubtaskById(int subtaskId) {
        super.removeSubtaskById(subtaskId);
        save();
    }

    @Override
    public void removeEpicById(int epicId) {
        super.removeEpicById(epicId);
        save();
    }
}
