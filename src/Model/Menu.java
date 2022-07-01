package Model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

class MenuOption {
    String optionText;
    String activator;
    IMenuAction callback;
    AccessLevel accessLevel;

    public MenuOption() {
        optionText = "Undefined Menu Text";
        activator = null;
        callback = null;
        accessLevel = AccessLevel.Default;
    }

    // create a menu option with activator
    public MenuOption(String optionText, String activator, IMenuAction callback, AccessLevel accessLevel) {
        this.optionText = optionText;
        // the activator is used to select the option
        this.activator = activator;
        this.callback = callback;
        this.accessLevel = accessLevel;
    }

    // without activator generic number index is used
    public MenuOption(String optionText, IMenuAction callback, AccessLevel accessLevel) {
        this.optionText = optionText;
        this.activator = null;
        this.callback = callback;
        this.accessLevel = accessLevel;
    }

    public MenuOption(String optionText, String activator, IMenuAction callback) {
        this.optionText = optionText;
        this.activator = activator;
        this.callback = callback;
        accessLevel = AccessLevel.Default;
    }

    public MenuOption(String optionText, IMenuAction callback) {
        this.optionText = optionText;
        this.callback = callback;
        accessLevel = AccessLevel.Default;
    }
}

public abstract class Menu {
    private static ObjectMapper objectMapper = new ObjectMapper();
    protected static File projectFile = new File("Projects.json");
    protected static File userFile = new File("Users.json");
    protected static Scanner input = new Scanner(System.in);

    private String menuText;
    private List<MenuOption> menuOptions;
    protected boolean authenticated;
    protected static User authenticatedUser;

    public Menu() {
        menuText = "";
        menuOptions = new ArrayList<>();
        authenticated = false;
    }

    public Menu run() throws IOException {
        displayText();
        return handleInput(displayOptions());
    }

    public void save() throws IOException {
        objectMapper.writeValue(projectFile, Projects.globalProjectList);
        objectMapper.writeValue(userFile, Users.globalUserList);
    }

    public static void load() throws IOException {
        Projects.globalProjectList = objectMapper.readValue(projectFile, new TypeReference<ProjectMap>(){});
        Users.globalUserList = objectMapper.readValue(userFile, new TypeReference<UserMap>(){});
    }

    public boolean authenticate() {
        if (authenticated) {
            return true;
        }

        AccessLevel accessLevel = getMinimumAccessLevel();

        User user = Users.getUser(readString("Please enter your id to proceed"));

        if (user == null) {
            System.out.println("Invalid ID. ");
            input.nextLine();
            return false;
        }

        if(!user.getAccessLevel().isSufficientFor(accessLevel)) {
            System.out.println("You do not have access to this menu! ");
            input.nextLine();
            return false;
        }

        String password = readString("Please enter your password");
        if (!password.equals(user.getPassword())) {
            System.out.println("Invalid password. ");
            input.nextLine();
            return false;
        }

        // to prevent having to log in again, will reset if you leave the menu
        authenticated = true;
        authenticatedUser = user;
        return true;
    }

    public abstract AccessLevel getMinimumAccessLevel();

    protected void promptConfirmation() {
        System.out.println("[Press Enter to continue]");
        input.nextLine();
    }

    protected String readString(String label) {
        System.out.println(label + ": ");

        String userInput = "";
        while (userInput.isBlank()) {
            userInput = input.nextLine();

            if (userInput.isBlank()) {
                System.out.println("Please enter a non-empty String. ");
            }
        }
        return userInput;
    }

    protected int readInt(String label) {
        System.out.println(label + ": ");

        Integer out = null;
        while (out == null) {
            try {
                out = Integer.parseInt(input.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Try again! ");
            }
        }
        return out;
    }

    protected float readFloat(String label) {
        System.out.println(label + ": ");

        Float out = null;
        while (out == null) {
            try {
                out = Float.parseFloat(input.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Try again! ");
            }
        }
        return out;
    }

    protected LocalDate readDate(String label) {
        System.out.println("Enter the " + label + " in the following format: YYYY-MM-DD ");

        LocalDate out = null;
        while (out == null) {
            try {
                out = LocalDate.parse(input.nextLine());
            } catch (DateTimeParseException e) {
                System.out.println("Invalid input! Try again! ");
            }
        }
        return out;
    }

    public void addOption(String optionText, IMenuAction callback, AccessLevel accessLevel) {
        menuOptions.add(new MenuOption(optionText, callback, accessLevel));
    }

    public void addOption(String optionText, String activator, IMenuAction callback, AccessLevel accessLevel) {
        menuOptions.add(new MenuOption(optionText, activator, callback, accessLevel));
    }

    public void addOption(String optionText, String activator, IMenuAction callback) {
        menuOptions.add(new MenuOption(optionText, activator, callback));
    }

    public void addOption(String optionText, IMenuAction callback) {
        menuOptions.add(new MenuOption(optionText, callback));
    }

    public void setMenuText(String newText) {
        menuText = newText;
    }

    public void displayText() {
        System.out.println(menuText);
    }

    public Map<String, MenuOption> displayOptions() {
        // print options with activator index if it is given, otherwise generic number index
        Map<String, MenuOption> activators = new HashMap<>();
        int i = 1;
        for (MenuOption menuOption : menuOptions) {

            // is the user authenticated for this option?
            if (authenticatedUser.getAccessLevel().isSufficientFor(menuOption.accessLevel)) {

                if (menuOption.activator != null) {
                    // save the activator and return it later to recognize it as valid input
                    activators.put(menuOption.activator, menuOption);
                    System.out.println(menuOption.activator + ". " + menuOption.optionText);

                } else {
                    // save generic number index as activator
                    activators.put(Integer.toString(i), menuOption);
                    System.out.println(i + ". " + menuOption.optionText);
                }
                ++i;
            }
        }
        return activators;
    }

    public Menu handleInput(Map<String, MenuOption> activators) throws IOException {
        MenuOption selectedOption = null;
        // counting attempts to refresh the menu so it doesn't scroll off screen
        int attempts = 3;

        while (selectedOption == null) {
            String userInput = input.nextLine().toLowerCase();

            if (activators.get(userInput) == null) {
                System.out.println("Invalid input! Try again! ");
                --attempts;
                if (attempts == 0) {
                    return this;
                }

            } else {
                selectedOption = activators.get(userInput);
            }
        }

        Menu nextMenu = selectedOption.callback.run();

        if (nextMenu == null) {
            input.close();
        // if password check fails, get thrown back to current menu (usually main menu)
        }

        return nextMenu;
    }
}
