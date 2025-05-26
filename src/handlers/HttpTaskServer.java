package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final TaskManager taskManager;
    private final Gson gson;
    private HttpServer server;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = Managers.getGson();
    }

    public void start() throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TasksHandler(taskManager, gson));
        server.createContext("/subtasks", new SubtasksHandler(taskManager, gson));
        server.createContext("/epics", new EpicsHandler(taskManager, gson));
        server.createContext("/history", new TaskCommandsHandler(taskManager, gson));
        server.createContext("/prioritized", new TaskCommandsHandler(taskManager, gson));
        server.start();
        System.out.println("Сервер запущен на порту " + PORT);
    }

    public void stop() {
        this.server.stop(0);
    }

    public static void main(String[] args) {
    }
}
