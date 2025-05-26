package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class TaskCommandsHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public TaskCommandsHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        List<Task> tasks;
        String command = getCommand(exchange.getRequestURI().getPath());
        switch (command) {
            case "history" -> {
                try {
                    tasks = taskManager.getHistory();
                    sendSuccessWithData(exchange, gson.toJson(tasks));
                } catch (Exception e) {
                    sendInternalError(exchange);
                }
            }
            case "prioritized" -> {
                try {
                    tasks = taskManager.getPrioritizedTasks();
                    sendSuccessWithData(exchange, gson.toJson(tasks));
                } catch (Exception e) {
                    sendInternalError(exchange);
                }
            }
            case null, default -> sendBadRequest(exchange);
        }
    }

    protected String getCommand(String path) {
        String[] parts = path.split("/");
        return parts.length > 1 && !parts[1].isEmpty() ? parts[1] : null;
    }
}
