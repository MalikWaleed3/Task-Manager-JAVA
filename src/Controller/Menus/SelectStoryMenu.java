package Controller.Menus;

import Model.*;

import java.util.ArrayList;

public class SelectStoryMenu extends Menu {

    private Project project;
    private Menu previousMenu;
    private AccessLevel minimumAccessLevel;

    public SelectStoryMenu(Project project, Menu previousMenu, IObjectCallback storyCallback, UserStory previousStory) {
        minimumAccessLevel = AccessLevel.ProjectManager;
        this.project = project;
        this.previousMenu = previousMenu;
        ArrayList<UserStory> stories = project.getStories();
        setMenuText("Select your User story: ");
        for (UserStory userStory : stories) {
            if (userStory.equals(previousStory)) {
                continue;
            }
            if (userStory instanceof EpicStory) {
                addOption("[EPIC] " + userStory.getName(), () -> storyCallback.run(userStory));
            } else {
                addOption(userStory.getName(), () -> storyCallback.run(userStory));
            }
        }
        addOption("Return to previous Menu", () -> previousMenu);
    }

    public AccessLevel getMinimumAccessLevel() {
        return minimumAccessLevel;
    }
}
