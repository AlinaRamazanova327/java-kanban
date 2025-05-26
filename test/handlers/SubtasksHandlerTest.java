package handlers;

import com.google.gson.Gson;
import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Subtask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubtasksHandlerTest {
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
    void testGetAllSubtasks() throws IOException, InterruptedException {
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1");
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2");
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .header("Content-Type", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();
        assertEquals(response.statusCode(), 200);
        assertTrue(json.contains("Subtask 1"));
        assertTrue(json.contains("Subtask 2"));
    }

    @Test
    void testGetSubtaskById() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("Subtask", "Description");
        taskManager.addSubtask(subtask);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + subtask.getId()))
                .header("Content-Type", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        assertEquals(response.statusCode(), 200);
        assertTrue(body.contains("Subtask"));
    }

    @Test
    void testPostNewSubtask() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("Subtask", "Description");
        String subtaskJson = gson.toJson(subtask);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> subtasks = taskManager.getAllSubtasks();
        assertEquals(response.statusCode(), 201);
        assertEquals(1, subtasks.size());
        assertEquals("Subtask", subtasks.getFirst().getTitle());
    }

    @Test
    void testDeleteSubtask() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("Subtask", "Description");
        taskManager.addSubtask(subtask);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + subtask.getId()))
                .header("Content-Type", "application/json")
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), 204);
        assertEquals(0, taskManager.getAllSubtasks().size());
    }
}