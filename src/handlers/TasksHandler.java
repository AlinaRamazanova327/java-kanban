package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class TasksHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public TasksHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        Endpoints endpoint = getEndpoints(path, exchange.getRequestMethod());
        switch (endpoint) {
            case GET -> getTasks(exchange);
            case GET_BY_ID -> getTask(exchange, getId(path));
            case POST -> postTask(exchange);
            case DELETE -> deleteTask(exchange, getId(path));
            case null, default -> sendNotFound(exchange);
        }
    }

    private void getTasks(HttpExchange exchange) throws IOException {
        List<Task> tasks = taskManager.getAllTasks();
        sendSuccessWithData(exchange, gson.toJson(tasks));
    }

    private void getTask(HttpExchange exchange, int id) throws IOException {
        Optional<Task> task = Optional.ofNullable(taskManager.getTaskById(id));
        if (task.isPresent()) {
            sendSuccessWithData(exchange, gson.toJson(task.get()));
        } else {
            sendNotFound(exchange);
        }
    }

    private void postTask(HttpExchange exchange) throws IOException {
        String taskJson = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        if (taskJson.isEmpty()) {
            sendBadRequest(exchange);
            return;
        }
        try {
            Task task = gson.fromJson(taskJson, Task.class);
            if (task.getId() == 0 || taskManager.getTaskById(task.getId()) == null) {
                taskManager.addTask(task);
                sendSuccessWithoutData(exchange);
            } else {
                Optional<Task> currentTask = Optional.ofNullable(taskManager.getTaskById(task.getId()));
                if (currentTask.isPresent()) {
                    taskManager.updateTask(currentTask.get());
                    sendSuccessWithoutData(exchange);
                } else {
                    sendNotFound(exchange);
                }
            }
        } catch (IOException e) {
            sendBadRequest(exchange);
        } catch (IllegalArgumentException e) {
            sendNotAcceptable(exchange);
        }
    }

    private void deleteTask(HttpExchange exchange, int id) throws IOException {
        Optional<Task> task = Optional.ofNullable(taskManager.getTaskById(id));
        if (task.isPresent()) {
            taskManager.removeTaskById(id);
            sendSuccessWithoutData(exchange);
        } else {
            sendNotFound(exchange);
        }
    }
}