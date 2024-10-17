package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    List<Task> getHistory = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (getHistory.size() < 10) {
            getHistory.add(task);
        } else {
            getHistory.removeFirst();
            getHistory.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getHistory;
    }
}
