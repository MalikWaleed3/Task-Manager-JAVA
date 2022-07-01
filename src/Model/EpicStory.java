package Model;

import java.util.ArrayList;

public class EpicStory extends UserStory {

    private ArrayList<String> stories;

    public EpicStory(String name, int points) {
        super(name, points);
        stories = new ArrayList<>();
    }

    public EpicStory() {
        super("Untitled Story", 0);
        stories = new ArrayList<>();
    }

    public void addStory(UserStory story) {
        stories.add(story.getID());
    }

    public boolean hasStory(UserStory story) {
        return stories.contains(story.getID());
    }
}
