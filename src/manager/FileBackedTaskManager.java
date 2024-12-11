package manager;

import tasks.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final String file;

    public FileBackedTaskManager(String file) {
        this.file = file;
    }

    public void save() {
        try (Writer fileWriter = new FileWriter(file)) {
            fileWriter.write("id,type,name,status,description,epic\n");
            for (Integer id : tasks.keySet()) {
                fileWriter.write(toString(tasks.get(id)) + "\n");
            }
            for (Integer id : subtasks.keySet()) {
                fileWriter.write(toString(subtasks.get(id)) + "\n");
            }
            for (Integer id : epics.keySet()) {
                fileWriter.write(toString(epics.get(id)) + "\n");
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
                } else {
                    Task task = fromString(line);
                    switch (task.getTaskType()) {
                        case TASK -> tasks.put(task.getId(), task);
                        case EPIC -> epics.put(task.getId(), (Epic) task);
                        case SUBTASK -> {
                            subtasks.put(task.getId(), (Subtask) task);
                            Epic epic = epics.get(((Subtask) task).epicId);
                            epic.subtaskIds.add(task.getId());
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения данных.");
        }
        return manager;
    }

    public String toString(Task task) {
        if (task.getTaskType().equals(TaskType.SUBTASK)) {
            return task.getId() + "," + task.getTaskType() + "," + task.getTitle() + "," + task.getStatus() + ","
                    + task.getDescription() + "," + subtasks.get(task.getId()).getEpicId();
        } else {
            return task.getId() + "," + task.getTaskType() + "," + task.getTitle() + "," + task.getStatus() + ","
                    + task.getDescription();
        }
    }

    public static Task fromString(String value) {
        String[] params = value.split(",");
        if (params[1].equals("SUBTASK")) {
            Subtask subtask = new Subtask(params[2], params[4]);
            subtask.setId(Integer.parseInt(params[0]));
            subtask.epicId = Integer.parseInt(params[5]);
            subtask.setStatus(TaskStatus.valueOf(params[3]));
            subtask.setTaskType(TaskType.SUBTASK);
            return subtask;
        } else if (params[1].equals("TASK")) {
            Task task = new Task(params[2], params[4]);
            task.setId(Integer.parseInt(params[0]));
            task.setStatus(TaskStatus.valueOf(params[3]));
            task.setTaskType(TaskType.TASK);
            return task;
        } else {
            Epic epic = new Epic(params[2], params[4]);
            epic.setId(Integer.parseInt(params[0]));
            epic.setStatus(TaskStatus.valueOf(params[3]));
            epic.setTaskType(TaskType.EPIC);
            return epic;
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
    public List<Task> getAllTasks() {
        super.getAllTasks();
        save();
        return List.of();
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        super.getAllSubtasks();
        save();
        return List.of();
    }

    @Override
    public List<Epic> getAllEpics() {
        super.getAllEpics();
        save();
        return List.of();
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

    @Override
    public List<Subtask> getAllSubtaskByEpic(Epic epic) {
        return super.getAllSubtaskByEpic(epic);
    }

    @Override
    public List<Task> getHistory() {
        super.getHistory();
        save();
        return super.getHistory();
    }

    public static void main(String[] args) {
        String fileName = "tasks.csv";
        File file = new File(fileName);
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(fileName);

        Task task1 = new Task("task1", "description1");
        task1.setStatus(TaskStatus.NEW);
        fileBackedTaskManager.addTask(task1);

        Task task2 = new Task("task2", "description2");
        task2.setStatus(TaskStatus.NEW);
        fileBackedTaskManager.addTask(task2);

        Epic epic1 = new Epic("epic1", "description1");
        fileBackedTaskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("subtask1", "description1");
        subtask1.epicId = epic1.getId();
        subtask1.setStatus(TaskStatus.NEW);
        fileBackedTaskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("subtask2", "description2");
        subtask2.epicId = epic1.getId();
        subtask2.setStatus(TaskStatus.NEW);
        fileBackedTaskManager.addSubtask(subtask2);

        Subtask subtask3 = new Subtask("subtask3", "description3");
        subtask3.epicId = epic1.getId();
        subtask3.setStatus(TaskStatus.NEW);
        fileBackedTaskManager.addSubtask(subtask3);

        Epic epic2 = new Epic("epic2", "description2");
        fileBackedTaskManager.addEpic(epic2);
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);
        System.out.println(loaded.getTaskById(2));
        System.out.println(loaded.getTaskById(1));
        System.out.println(loaded.getTaskById(2));
        System.out.println("История:" + loaded.getHistory());
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
