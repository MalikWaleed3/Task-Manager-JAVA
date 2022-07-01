package Controller.Menus;

import Model.*;

import java.util.ArrayList;

public class StatsMenu extends Menu {
    private Project project;
    private Menu previousMenu;
    private AccessLevel minimumAccessLevel;

    public StatsMenu(Project project, Menu previousMenu) {
        super();
        this.project = project;
        this.previousMenu = previousMenu;
        minimumAccessLevel = AccessLevel.ProjectManager;
        setMenuText("What information would you like to view? ");
        addOption("Display a Project Member", this::displayUser);
        addOption("Sort Project Members by time spent", () -> displaySorted("time"));
        addOption("Sort Project Members by tasks involved in", () -> displaySorted("tasks"));
        addOption("Back to Project Menu", () -> previousMenu);
    }

    public AccessLevel getMinimumAccessLevel() {
        return minimumAccessLevel;
    }

    public Menu displayUser() {
        // replaceAll taken from https://stackoverflow.com/questions/5455794/removing-whitespace-from-strings-in-java
        String userName = readString("Enter the name of the user you wish to view").replaceAll("\\s","");
        ArrayList<Developer> developers = project.getUsers(userName);

        if (developers.size() == 0) {
            System.out.println("No such user has been added to this project. ");
            return this;
        }

        for (Developer developer : developers) {
            ArrayList<Task> tasks = developer.getTasks(project);

            System.out.println(developer.getName() + System.getProperty("line.separator") +
                    "Time spent working: " + developer.getTime(project) + "h " + System.getProperty("line.separator") +
                    "Number of tasks involved in: " + developer.getNumberOfTasks() + System.getProperty("line.separator") +
                    System.getProperty("line.separator") + "Tasks: ");

            for (Task task : tasks) {
                System.out.println(task.getName());
            }
            System.out.println();
        }
        return this;
    }

    public Menu displaySorted(String sortCriteria) throws RuntimeException {
        System.out.println(switch (sortCriteria) {
            // vulnerable to misspelling!
            case "time" -> project.sortByTimeSpent();
            case "tasks" -> project.sortByTaskInvolved();
            default -> throw new RuntimeException("Not a valid sortCriteria");
        });
        return this;
    }
}
