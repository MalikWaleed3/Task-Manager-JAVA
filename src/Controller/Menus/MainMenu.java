package Controller.Menus;

import Controller.Main;
import Model.AccessLevel;
import Model.Menu;
import Model.Project;
import Model.Projects;

import java.io.IOException;
import java.time.LocalDate;

public class MainMenu extends Menu {

    private AccessLevel minimumAccessLevel;

    public MainMenu() {
        super();
        minimumAccessLevel = AccessLevel.Default;
        setMenuText("Main Menu: ");
        addOption("Log out", LoginMenu::new);
        addOption("Create a Project", this::createProject);
        addOption("Select a Project to work on", this::selectProject);
        addOption("Exit Program", () -> null);
    }

    @Override
    public AccessLevel getMinimumAccessLevel() {
        return minimumAccessLevel;
    }

    private Menu createProject() throws IOException {
        String projectName = readString("Enter your project name");
        LocalDate dueDate = readDate("project's due date");
        Project newProject = new Project(projectName, dueDate.toString());
        newProject.addUser(authenticatedUser);
        authenticatedUser.setAccessLevel(AccessLevel.ProjectManager);
        Projects.addProject(newProject);
        Main.createAdmin();
        save();
        return this;
    }

    private Menu selectProject() {
        return new SelectProjectMenu(this, (Object object) -> new ProjectMenu((Project)object, this));
    }
}
