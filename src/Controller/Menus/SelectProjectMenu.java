package Controller.Menus;

import Model.*;

import java.util.ArrayList;

public class SelectProjectMenu extends Menu {

    private AccessLevel minimumAccessLevel;
    private Menu previousMenu;

    public SelectProjectMenu(Menu previousMenu, IObjectCallback projectCallback) {
        super();
        minimumAccessLevel = AccessLevel.Default;
        this.previousMenu = previousMenu;
        setMenuText("Select your Project: ");
        ArrayList<Project> projects = new ArrayList<>();
        for (Project project : Projects.globalProjectList.values()) {
            if (project.getUser(authenticatedUser.getID()) != null) {
                projects.add(project);
            }
        }
        for (Project project : projects) {
            addOption(project.getName(), () -> projectCallback.run(project));
        }
        addOption("Back to Main Menu", () -> previousMenu);
    }

    public AccessLevel getMinimumAccessLevel() {
        return minimumAccessLevel;
    }
}
