package tasks;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
   public List<Integer> subtaskIds = new ArrayList<>();

   public Epic (String title, String description) {
      super(title,description);
   }
}
