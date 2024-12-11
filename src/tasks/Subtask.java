package tasks;

public class Subtask extends Task {
    public int epicId;

    public int getEpicId() {
        return epicId;
    }

    public Subtask(String title, String description) {
      super(title, description);
   }
}
