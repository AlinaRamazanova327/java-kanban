package tasks;

import com.google.gson.annotations.SerializedName;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    public List<Integer> subtaskIds = new ArrayList<>();
    @SerializedName("alternativeDuration")
    private Duration alternativeDuration;
    @SerializedName("alternativeStartTime")
    private LocalDateTime alternativeStartTime;
    @SerializedName("alternativeEndTime")
    private LocalDateTime alternativeEndTime;

    public Epic(String title, String description) {
        super(title, description);
    }

    public LocalDateTime getStartTime() {
        return alternativeStartTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.alternativeStartTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return alternativeEndTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.alternativeEndTime = endTime;
    }

    public Duration getDuration() {
        return alternativeDuration;
    }

    public void setDuration(Duration duration) {
        this.alternativeDuration = duration;
    }
}