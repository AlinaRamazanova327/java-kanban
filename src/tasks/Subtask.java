package tasks;

public class Subtask extends Task {
    public int epicId;

    public Subtask(String title, String description) {
        super(title, description);
    }

    @Override
    public String toString() {
        return getId() + "," + getTaskType() + "," + getTitle() + "," + getStatus() + ","
                + getDescription() + "," + epicId;
    }
}
