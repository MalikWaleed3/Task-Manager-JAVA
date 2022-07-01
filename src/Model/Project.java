package Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.lang.Math;

import static java.time.temporal.ChronoUnit.DAYS;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Project {
    private String projectID;
    private String name;
    private String dueDate;
    private HashMap<String, Milestone> milestones;
    @JsonProperty("MyTaskList")
    private TaskMap tasks;
    private Double budget;
    private DoubleMap expenseList = new DoubleMap();
    private ArrayList<String> users;
    private ArrayList<UserStory> userStories;
    private String startDate;

    public Project(String name, String dueDate) {
        projectID = UUID.randomUUID().toString();
        this.name = name;
        this.dueDate = dueDate;
        milestones = new HashMap<>();
        tasks = new TaskMap();
        users = new ArrayList<>();
        userStories = new ArrayList<>();
        budget = 0d;
    }

    public Project() {
        projectID = UUID.randomUUID().toString();
        name = "Untitled Project";
        dueDate = "Due date not set";
        milestones = new HashMap<>();
        tasks = new TaskMap();
        users = new ArrayList<>();
        userStories = new ArrayList<>();
        budget = 0d;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void addStory(UserStory userStory) {
        userStory.setProjectID(projectID);
        userStories.add(userStory);
    }

    public double calculateVelocity() {
        if (startDate == null) {
            throw new NullPointerException("No work has been done on your project yet. ");
        }

        int daysPast = (int) DAYS.between(LocalDate.parse(startDate), LocalDate.now());
        if (daysPast == 0) {
            throw new DateTimeException("Work on your project has only started today or not at all. ");
        }

        double earnedPoints = getEarnedPoints();
        double velocity = (earnedPoints / daysPast) * 7;
        return velocity;
    }

    public int getTotalPoints() {
        int totalPoints = 0;
        for (UserStory story : userStories) {
            totalPoints += story.getPoints();
        }
        return totalPoints;
    }

    public int getHoursPerPoint() {
        double minutesWorked = 0;
        for (String userID : users) {
            User user = Users.getUser(userID);
            if (!(user instanceof Developer)) {
                continue;
            }
            Developer developer = (Developer)user;
            minutesWorked += developer.getTime(this);
        }
        return (int) Math.round((minutesWorked / 60) / getEarnedPoints());
    }

    public int getTotalHours() {
        return getTotalPoints() * getHoursPerPoint();
    }

    public int getRemainingHours() {
        int remainingPoints = (int)(getTotalPoints() - getEarnedPoints());
        return remainingPoints * getHoursPerPoint();
    }

    public void addMilestone(Milestone milestone) {
        milestone.setProjectID(projectID);
        milestones.put(milestone.getID(), milestone);
    }

    public double getEarnedPoints() {
        double points = 0;
        for (UserStory story : userStories) {
            points += story.getEarnedPoints();
        }
        return points;
    }

    public String getDueDate() {
        return dueDate;
    }

    public Task getTask(String taskID) {
        return tasks.get(taskID);
    }

    public void addTask(Task task) {
        tasks.put(task.getID(), task);
    }

    public HashMap<String, Milestone> getMilestones() {
        return milestones;
    }

    public Milestone getMilestone(String milestoneID) {
        return milestones.get(milestoneID);
    }

    public ArrayList<UserStory> getStories() {
        return userStories;
    }

    public void removeStory(UserStory userStory) {
        userStories.remove(userStory);
    }

    public boolean hasStories() {
        return !userStories.isEmpty();
    }

    public boolean hasMilestones() {
        return !milestones.isEmpty();
    }

    public String getID() {
        return projectID;
    }

    public String getName() {
        return name;
    }

    public void addUser(User user) {
        String userID = user.getID();
        if (!users.contains(userID)) {
            users.add(userID);
        }
    }

    public void removeUser(User user) { users.remove(user.getID()); }

    public User getUser(String userID) {
        if (users.contains(userID)) {
            return Users.getUser(userID);
        }
        return null;
    }

    public ArrayList<Developer> getUsers(String userName) {
        ArrayList<Developer> developers = new ArrayList<>();
        for (String userID : users) {
            // replaceAll taken from https://stackoverflow.com/questions/5455794/removing-whitespace-from-strings-in-java
            User user = Users.getUser(userID);
            if (user.getName().replaceAll("\\s","").equalsIgnoreCase(userName)) {
                developers.add((Developer)user);
            }
        }
        return developers;
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public ArrayList<Milestone> getArrayListMilestones() {
        ArrayList<Milestone> projectMilestones = new ArrayList<>();
        for (Milestone milestone : milestones.values()) {
                projectMilestones.add(milestone);
        }
        return projectMilestones;
    }

    public String sortByTimeSpent() {
        // find all the developers and add them to a list for sorting
        ArrayList<Developer> developers = new ArrayList<>();
        for (String userID : users) {
            User user = Users.getUser(userID);
            if (user instanceof Developer) {
                developers.add((Developer)user);
            }
        }

        Developer currentDeveloper;
        Developer nextDeveloper;
        Developer placeHolder;
        // Selection sort
        for (int i = 0; i < developers.size(); ++i) {
            currentDeveloper = developers.get(i);

            for (int j = i + 1; j < developers.size(); ++j) {
                nextDeveloper = developers.get(j);

                if (nextDeveloper.getTime() > currentDeveloper.getTime()) {
                    currentDeveloper = nextDeveloper;
                }
            }
            placeHolder = currentDeveloper;
            developers.remove(currentDeveloper);
            developers.add(i, placeHolder);
        }
        // Create the String
        String sortedDevelopers = "";
        for (Developer developer : developers) {
            sortedDevelopers = sortedDevelopers + System.getProperty("line.separator") + "Name: " + developer.getName() +
                                                  System.getProperty("line.separator") + "Time spent: " + developer.getTime() +
                                                  System.getProperty("line.separator");
        }
        return sortedDevelopers;
    }

    public String sortByTaskInvolved() {
        // find all the developers and add them to a list for sorting
        ArrayList<Developer> developers = new ArrayList<>();
        for (String userID : users) {
            User user = Users.getUser(userID);
            if (user instanceof Developer) {
                developers.add((Developer)user);
            }
        }

        Developer currentDeveloper;
        Developer nextDeveloper;
        Developer placeHolder;
        // Selection sort
        for (int i = 0; i < developers.size(); ++i) {
            currentDeveloper = developers.get(i);

            for (int j = i + 1; j < developers.size(); ++j) {
                nextDeveloper = developers.get(j);

                if (nextDeveloper.getNumberOfTasks() > currentDeveloper.getNumberOfTasks()) {
                    currentDeveloper = nextDeveloper;
                }
            }
            placeHolder = currentDeveloper;
            developers.remove(currentDeveloper);
            developers.add(i, placeHolder);
        }
        // Create the String
        String sortedDevelopers = "";
        for (Developer developer : developers) {
            sortedDevelopers = sortedDevelopers + System.getProperty("line.separator") + "Name: " + developer.getName() +
                                                  System.getProperty("line.separator") + "Number of tasks involved in: " +
                                                  developer.getNumberOfTasks() + System.getProperty("line.separator");
        }
        return sortedDevelopers;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public Double getBudget() {
        return this.budget;
    }

    public void addExpense(String newPurpose, double newMoney) {
        Double money = expenseList.get(newPurpose);
        if (money != null) {
            expenseList.put(newPurpose, money + newMoney);
        } else {
            expenseList.put(newPurpose, newMoney);
        }
    }

    public double getSpendable(){
        double summedExpenses = 0;
        for (double cost : expenseList.values()) {
            summedExpenses += cost;
        }
        return (budget - summedExpenses);
    }
}