package manager;

public class Managers {

    static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefault(String file) {
        return new FileBackedTaskManager(file);
    }
}
