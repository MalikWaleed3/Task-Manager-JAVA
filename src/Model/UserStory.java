package Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.DAYS;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserStory {

    private String name;
    private int points;
    private String startTime;
    private String finishTime;
    private ArrayList<String> tasks;
    private String storyID;
    private String projectID;

    public UserStory(String name, int points) {
        storyID = UUID.randomUUID().toString();
        this.name = name;
        this.points = points;
        tasks = new ArrayList<>();
    }

    public UserStory() {
        storyID = UUID.randomUUID().toString();
        name = "Untitled Story";
        points = 0;
        tasks = new ArrayList<>();
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    @JsonIgnore
    public void setFinishTime(LocalDate finishTime) throws IllegalArgumentException, DateTimeException {
        if (DAYS.between(LocalDate.parse(startTime), finishTime) > -1) {
            this.finishTime = finishTime.toString();
        } else {
            throw new IllegalArgumentException("Duration cannot be negative. ");
        }
    }

    public String getID() {
        return storyID;
    }

    public void addTask(Task task) {
        tasks.add(task.getID());
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    public boolean hasTasks() {
        return !tasks.isEmpty();
    }

    public boolean hasTask(Task task) {
        return tasks.contains(task.getID());
    }

    public double getEarnedPoints() {
        double pointsEarned = 0;
        double pointsPerTask = (double)points / tasks.size();

        Project project = Projects.getProject(projectID);
        for (String taskID : tasks) {
            if (project.getTask(taskID).isTaskStatus()) {
                pointsEarned += pointsPerTask;
            }
        }
        return pointsEarned;
    }
}
