package Controller;


import Controller.Menus.LoginMenu;
import Model.*;
import java.io.IOException;

public class Main {

    public static void createAdmin() {
        // create master user
        Developer admin;
        if (Users.getNumberOfUsers() == 0) {
            admin = new Developer("admin", "admin");
            admin.createMaster("admin", AccessLevel.Admin);
            Users.addUser(admin);

        } else {
            admin = (Developer) Users.getUser("admin");
        }

        // give admin full access to all projects
        for (Project project : Projects.globalProjectList.values()) {
            project.addUser(admin);
        }
    }

    public static void main(String[] args) throws IOException {
        // load data
        try {
            Menu.load();
        } catch (IOException e) {
            System.out.println("No save data has been found. ");
        }

        // create admin user
        createAdmin();

        // start at login menu
        Menu nextMenu = new LoginMenu();
        nextMenu.save();

        // run the program
        while (nextMenu != null) {
            try {
                nextMenu = nextMenu.run();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
                // unhandled exception, shutting down program
                return;
            }
        }
    }

}


