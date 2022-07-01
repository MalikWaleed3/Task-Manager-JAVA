package Controller.Menus;

import Model.*;

import java.io.IOException;

public class LoginMenu extends Menu {
    private AccessLevel minimumAccessLevel;

    public LoginMenu() {
        super();
        minimumAccessLevel = AccessLevel.Default;
        authenticatedUser = new Developer("default", "default");
        setMenuText("Welcome to GroupSixProject, your project management tool. ");
        addOption("Log in", this::login);
        addOption("Register", this::register);
        addOption("Exit Program", () -> null);
    }

    public AccessLevel getMinimumAccessLevel() {
        return minimumAccessLevel;
    }

    public Menu login() {
        if (!authenticate()) {
            return this;
        }
        return new MainMenu();
    }

    public Menu register() throws IOException {
        String name = readString("Enter your full name");
        String password = readString("Enter your password");
        User developer = new Developer(name, password);
        Users.addUser(developer);
        System.out.println("You have been registered and can now log in! Your id is " + developer.getID() + "! ");
        save();
        return this;
    }
}
