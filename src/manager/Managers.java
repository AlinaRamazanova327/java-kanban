package manager;

import adapters.Adapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.Duration;
import java.time.LocalDateTime;

public class Managers {

    static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefault(String file) {
        return new FileBackedTaskManager(file);
    }

public static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new Adapter.LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new Adapter.DurationAdapter())
                .setPrettyPrinting().create();
}
}
