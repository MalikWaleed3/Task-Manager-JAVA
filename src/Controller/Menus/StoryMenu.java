package Controller.Menus;

import Model.*;

import java.io.IOException;

public class StoryMenu extends Menu {

    private AccessLevel minimumAccessLevel;
    private Project project;
    private UserStory userStory;
    private ProjectMenu projectMenu;

    public StoryMenu(Project project, UserStory userStory, ProjectMenu projectMenu) {
        minimumAccessLevel = AccessLevel.ProjectManager;
        this.project = project;
        this.userStory = userStory;
        this.projectMenu = projectMenu;
        setMenuText("What would you like to do: ");
        if (userStory instanceof EpicStory) {
            addOption("Add a user story to this epic", () ->
                    new SelectStoryMenu(project, this, this::addStory, userStory));
        } else {
            addOption("Make this user story an epic", this::convertToEpic);
            addOption("Assign a task to this user story", () -> new SelectTaskMenu(project, this,
                    this::assignTask));
        }
        addOption("Select a different User story", () -> new SelectStoryMenu(project, this, this::selectStory, null));
        addOption("Return to Project Menu", () -> projectMenu);
    }

    public AccessLevel getMinimumAccessLevel() {
        return minimumAccessLevel;
    }

    private Menu assignTask(Object task) throws IOException {
        if (userStory.hasTask((Task)task)) {
            System.out.println("That task is already assigned to this user story! ");
        } else {
            userStory.addTask((Task) task);
            System.out.println(((Task) task).getName() + " has been assigned to " + userStory.getName() + "! ");
        }
        save();
        promptConfirmation();
        return this;
    }

    private Menu selectStory(Object userStory) {
        this.userStory = (UserStory)userStory;
        return this;
    }

    private Menu convertToEpic() throws IOException {
        if (userStory.hasTasks() || userStory instanceof EpicStory) {
            System.out.println("Can't convert because Story is either epic or has tasks assigned to it already. ");
            promptConfirmation();
            return this;
        }

        UserStory epic = new EpicStory(userStory.getName(), userStory.getPoints());
        project.removeStory(userStory);
        userStory = epic;
        project.addStory(userStory);
        System.out.println("\"" + userStory.getName() + "\" has been converted to an epic! ");
        save();
        promptConfirmation();
        return new StoryMenu(project, userStory, projectMenu);
    }

    private Menu addStory(Object userStory) throws IOException {
        if (((EpicStory)this.userStory).hasStory((UserStory)userStory)) {
            System.out.println("That user story is already part of this epic! ");
        } else {
            ((EpicStory) this.userStory).addStory((UserStory) userStory);
            System.out.println("The user story \"" + ((UserStory) userStory).getName() + "\" has been assigned to the epic \"" +
                    (this.userStory.getName() + "\"! "));
        }
        save();
        promptConfirmation();
        return this;
    }
}
