package handlers;

import com.google.gson.Gson;
import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TasksHandlerTest {
    TaskManager taskManager;
    HttpTaskServer httpTaskServer;
    Gson gson;
    HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();

    @BeforeEach
    void setUp() throws IOException {
        taskManager = new InMemoryTaskManager();
        httpTaskServer = new HttpTaskServer(taskManager);
        gson = Managers.getGson();
        httpTaskServer.start();
    }

    @AfterEach
    void tearDown() {
        httpTaskServer.stop();
    }

    @Test
    void testGetAllTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();
        assertEquals(response.statusCode(), 200);
        assertTrue(json.contains("Task 1"));
        assertTrue(json.contains("Task 2"));
    }

    @Test
    void testGetTaskById() throws IOException, InterruptedException {
        Task task = new Task("Task", "Description");
        taskManager.addTask(task);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + task.getId()))
                .header("Content-Type", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        assertEquals(response.statusCode(), 200);
        assertTrue(body.contains("Task"));
    }

    @Test
    void testPostNewTask() throws IOException, InterruptedException {
        Task task = new Task("Task", "Description");
        String taskJson = gson.toJson(task);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> tasks = taskManager.getAllTasks();
        assertEquals(response.statusCode(), 201);
        assertEquals(1, tasks.size());
        assertEquals("Task", tasks.getFirst().getTitle());
    }

    @Test
    void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Task", "Description");
        taskManager.addTask(task);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + task.getId()))
                .header("Content-Type", "application/json")
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), 201);
        assertEquals(0, taskManager.getAllTasks().size());
    }
}