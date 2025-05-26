package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import tasks.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class SubtasksHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public SubtasksHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        Endpoints endpoint = getEndpoints(path, exchange.getRequestMethod());
        switch (endpoint) {
            case GET -> getSubtasks(exchange);
            case GET_BY_ID -> getSubtask(exchange, getId(path));
            case POST -> postSubtask(exchange);
            case DELETE -> deleteSubtask(exchange, getId(path));
            default -> sendBadRequest(exchange);
        }
    }

    private void getSubtasks(HttpExchange exchange) throws IOException {
        List<Subtask> subtasks = taskManager.getAllSubtasks();
        sendSuccessWithData(exchange, gson.toJson(subtasks));
    }

    private void getSubtask(HttpExchange exchange, int id) throws IOException {
        Optional<Subtask> subtask = Optional.ofNullable(taskManager.getSubtaskById(id));
        if (subtask.isPresent()) {
            sendSuccessWithData(exchange, gson.toJson(subtask.get()));
        } else {
            sendNotFound(exchange);
        }
    }

    private void postSubtask(HttpExchange exchange) throws IOException {
        String taskJson = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        if (taskJson.isEmpty()) {
            sendBadRequest(exchange);
            return;
        }
        try {
            Subtask subtask = gson.fromJson(taskJson, Subtask.class);
            if (subtask.getId() != 0 && taskManager.getSubtaskById(subtask.getId()) != null) {
                taskManager.updateSubtask(subtask);
            } else {
                taskManager.addSubtask(subtask);
            }
            sendSuccessWithoutData(exchange);
        } catch (IOException e) {
            sendBadRequest(exchange);
        }
    }

    private void deleteSubtask(HttpExchange exchange, int id) throws IOException {
        Optional<Subtask> subtask = Optional.ofNullable(taskManager.getSubtaskById(id));
        if (subtask.isPresent()) {
            taskManager.removeSubtaskById(id);
            sendNoContent(exchange);
        } else {
            sendNotFound(exchange);
        }
    }
}