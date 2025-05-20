package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    public int epicId;

    public Subtask(String title, String description) {
        super(title, description);
    }

    public Subtask(String title, String description, TaskStatus status,
                   LocalDateTime startTime, Duration duration, int epicId) {
        super(title, description, status, startTime, duration);
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%s,%s,%s,%d",
                getId(), getTaskType(), getTitle(), getStatus(), getDescription(),
                getStartTime(), getDuration(), getEndTime(), epicId);
    }
}
