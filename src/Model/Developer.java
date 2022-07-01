package Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Developer extends User {
    private HashMap<Task, Double> timeSpent;
    private ArrayList<String> tasks;

    public Developer(String name, String password) {
        super(name, password, AccessLevel.Developer);
        tasks = new ArrayList<>();
        timeSpent = new HashMap<>();
    }

    public Developer() {
        super("Unnamed Developer", "default", AccessLevel.Developer);
        tasks = new ArrayList<>();
        timeSpent = new HashMap<>();
    }

    public boolean hasTasks() {
        return !tasks.isEmpty();
    }

    public void addTask(Task task) {
        tasks.add(task.getID());
    }

    public void removeTask(Task task) {
    	tasks.remove(task.getID());
    }

    public void addTime(Task task, double time) {
        if (timeSpent.get(task) != null) {
            timeSpent.merge(task, time, Double::sum);
        } else {
            timeSpent.put(task, time);
        }
    }

    public int getNumberOfTasks(Project project) {
        int numberOfProjectTasks = 0;
        for (String taskID : tasks) {
            Task task = project.getTask(taskID);
            if (task.getProjectID().equals(project.getID())) {
                ++numberOfProjectTasks;
            }
        }
        return numberOfProjectTasks;
    }

    public int getNumberOfTasks() {
        return tasks.size();
    }

    public ArrayList<Task> getTasks(Project project) {
        ArrayList<Task> projectTasks = new ArrayList<>();
        for (String taskID : tasks) {
            Task task = project.getTask(taskID);
            if (task != null) {
                projectTasks.add(task);
            }
        }
        return projectTasks;
    }

    public ArrayList <String> getTasks() {
        return tasks;
    }

    public double getTime(Project project) {
        double sumTime = 0;
        for (Map.Entry<Task, Double> entry : timeSpent.entrySet()) {
            if (entry.getKey().getProjectID().equals(project.getID())) {
                sumTime += entry.getValue();
            }
        }
        return sumTime;
    }

    public double getTime() {
        double sumTime = 0;
        for (Double time : timeSpent.values()) {
            sumTime += time;
        }
        return sumTime;
    }

    public double getTime(Task task) {
        return timeSpent.get(task);
    }

    public String toString() {
        return ("Name: " + getName() + System.getProperty("line.separator") +
                "Number of tasks: " + getNumberOfTasks() + System.getProperty("line.separator") +
                (getTime() > 0 ? "Time spent: " + timeSpent  + System.getProperty("line.separator") : ""));
    }
}
