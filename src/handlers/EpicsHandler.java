package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import tasks.Epic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class EpicsHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public EpicsHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        Endpoints endpoint = getEndpoints(path, exchange.getRequestMethod());
        switch (endpoint) {
            case GET -> getEpics(exchange);
            case GET_BY_ID -> getEpic(exchange, getId(path));
            case POST -> postEpic(exchange);
            case DELETE -> deleteEpic(exchange, getId(path));
            default -> sendBadRequest(exchange);
        }
    }

    private void getEpics(HttpExchange exchange) throws IOException {
        List<Epic> epics = taskManager.getAllEpics();
        sendSuccessWithData(exchange, gson.toJson(epics));
    }

    private void getEpic(HttpExchange exchange, int id) throws IOException {
        Optional<Epic> epic = Optional.ofNullable(taskManager.getEpicById(id));
        if (epic.isPresent()) {
            sendSuccessWithData(exchange, gson.toJson(epic.get()));
        } else {
            sendNotFound(exchange);
        }
    }

    private void postEpic(HttpExchange exchange) throws IOException {
        String taskJson = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        if (taskJson.isEmpty()) {
            sendBadRequest(exchange);
            return;
        }
        try {
            Epic epic = gson.fromJson(taskJson, Epic.class);
            if (epic.getId() != 0 && taskManager.getEpicById(epic.getId()) != null) {
                taskManager.updateEpic(epic);
            } else {
                taskManager.addEpic(epic);
            }
            sendSuccessWithoutData(exchange);
        } catch (IOException e) {
            sendBadRequest(exchange);
        }
    }

    private void deleteEpic(HttpExchange exchange, int id) throws IOException {
        Epic epic = taskManager.getEpicById(id);
        if (epic != null) {
            taskManager.removeEpicById(id);
            sendSuccessWithoutData(exchange);
        } else {
            sendNotFound(exchange);
        }
    }
}