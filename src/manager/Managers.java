package manager;

public class Managers {

    static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static FileBackedTaskManager getFromFile(String file) {
        return new FileBackedTaskManager(file);
    }
}
