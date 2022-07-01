package Controller.Menus;

import Model.*;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.ArrayList;

public class ProjectMenu extends Menu {

    private AccessLevel minimumAccessLevel;
    private MainMenu mainMenu;
    private Project project;

    public ProjectMenu(Project project, MainMenu mainMenu) {
        super();
        this.mainMenu = mainMenu;
        this.project = project;
        minimumAccessLevel = AccessLevel.Default;
        setMenuText("This Project is due on: " + project.getDueDate() + ". What would you like to do? ");
        addOption("View Milestone and Task Status", () -> new StatusMenu(project, this), AccessLevel.Developer);
        addOption("Add a Milestone", this::createMilestone, AccessLevel.ProjectManager);
        addOption("Change Status of Milestone", this::changeMilestoneStatus, AccessLevel.ProjectManager);
        addOption("Add a User Story", this::createStory, AccessLevel.ProjectManager);
        addOption("Remove a User Story", () -> new SelectStoryMenu(project, this, this::removeStory, null), AccessLevel.ProjectManager);
        addOption("Add a Task", this::createTask, AccessLevel.ProjectManager);
        addOption("Change Task Status", this::changeTaskStatus, AccessLevel.ProjectManager);
        addOption("Add a comment to Milestone or Task",  () -> new AddCommentMenu(project, this), AccessLevel.ProjectManager);
        addOption("Assign a Task", this::changeAssigne, AccessLevel.ProjectManager);
        addOption("Work on a Task", this::workOnTask, AccessLevel.Developer);
        addOption("Work on user stories", this::workOnStories, AccessLevel.ProjectManager);
        addOption("Create budget", this::createBudget, AccessLevel.ProjectManager);
        addOption("Add a Project Member", this::addMember, AccessLevel.ProjectManager);
        addOption("Remove a Project Member", this::removeMember, AccessLevel.ProjectManager);
        addOption("Add budget", this::addBudget, AccessLevel.ProjectManager);
        addOption("Add expense", this::addExpense, AccessLevel.ProjectManager);
        addOption("Get spendable budget", this::getSpendable, AccessLevel.ProjectManager);
        addOption("View Project Member statistics", () -> new StatsMenu(project, this), AccessLevel.ProjectManager);
        addOption("Show Velocity", this::displayVelocity);
        addOption("Show estimated completion date", this::calculateCompletion);
        addOption("Show estimated project cost in work hours", this::displayCost, AccessLevel.ProjectManager);
        addOption("Select a different Project", () -> new SelectProjectMenu(this, this::selectProject));
        addOption("Return to Main Menu", () -> mainMenu);
        addOption("Log out", MainMenu::new);
        addOption("Exit Program", () -> null);
    }

    public AccessLevel getMinimumAccessLevel() {
        return minimumAccessLevel;
    }

    private Menu selectProject(Object project) {
        this.project = (Project)project;
        return this;
    }

    private Menu displayVelocity() {
        try {
            System.out.println("Your average weekly velocity is " + project.calculateVelocity() + ". ");
        } catch (NullPointerException | DateTimeException e) {
            System.out.println(e.getMessage());
        }
        promptConfirmation();
        return this;
    }

    private Menu displayCost() {
        int totalHours = project.getTotalHours();
        if (totalHours == 0) {
            System.out.println("Cannot calculate as no work has been done yet. ");
            promptConfirmation();
            return this;
        }

        System.out.println("Based on planned story points and logged work time..." +
                System.getProperty("line.separator") + "The Projects total cost is " + totalHours +
                " work hours. " + System.getProperty("line.separator") + "The Projects remaining cost is " +
                project.getRemainingHours() + " work hours. ");
        promptConfirmation();
        return this;
    }

    private Menu calculateCompletion() {
        double dailyVelocity = 0;
        try {
            dailyVelocity = project.calculateVelocity() / 7;
        } catch (NullPointerException | DateTimeException e) {
            System.out.println(e.getMessage());
            promptConfirmation();
            return this;
        }
        int totalPoints = project.getTotalPoints();
        int duration = (int)(totalPoints / dailyVelocity);
        LocalDate completionTime = LocalDate.parse(project.getStartDate()).plusDays(duration);
        System.out.println("Estimated completion date based on planned story points and average velocity is " +
                completionTime + ". ");
        promptConfirmation();
        return this;
    }

    public Menu addMember() throws IOException {
        Users.globalUserList.forEach((key, value) -> {
            if (project.getUser(key) == null) {
                System.out.println("ID:" + key);
                System.out.println("Name:" + value.getName());

            }});

        String userID = readString("Enter the ID of the user you want to add to your project");
        User user = Users.getUser(userID);

        if (user == null) {
            System.out.println("That user does not exist! ");
        } else {
            project.addUser(user);
            System.out.println(user.getName() + " has been added to your project " + project.getName() + "! ");
        }
        save();
        return this;
    }

    public Menu removeMember() throws IOException {
        Users.globalUserList.forEach((key, value) -> {
            if (project.getUser(key) == Users.getUser(key)) {
                System.out.println("ID:" + key);
                System.out.println("Name:" + value.getName());

            }});
        String userID = readString("Enter the ID of the user you want to remove from the project");
        User user = Users.getUser(userID);
        if (user == null) {
            System.out.println("That user does not exist! ");
        } else {
            project.removeUser(user);
            System.out.println(user.getName() + " has been remove from your project " + project.getName() + "! ");
        }
        save();
        return this;

    }


    private Menu createStory() throws IOException {
        String userType = readString("As a...");
        String desiredFunctionality = readString(", I want...");
        String reason = readString(", so that...");
        int storyPoints = 0;
        while (storyPoints <= 0) {
            storyPoints = readInt("Number of story points for this user story");
            if (storyPoints <= 0) {
                System.out.println("Please enter a positive Integer. ");
            }
        }
        project.addStory(new UserStory("As a " + userType + ", I want " + desiredFunctionality + ", so that " +
                reason + ". ", storyPoints));
        save();
        return this;
    }

    private Menu removeStory(Object object) throws IOException {
        project.removeStory((UserStory)object);
        System.out.println("\"" + ((UserStory)object).getName() + "\" has been removed. ");
        promptConfirmation();
        save();
        return this;
    }

    public Menu changeMilestoneStatus() throws IOException {
        if (!project.hasMilestones()) {
            System.out.println("This project does not have any Milestone. Please create one first! \n");
            return this;
        }
    	//Show all Milestones and their status for the project
    	String arrayInfoMilestone = "-------------------------------------------\n";
    	ArrayList<Milestone> milestones = project.getArrayListMilestones();
    	
    	for (int i = 0; i < milestones.size(); i++) { 
    		arrayInfoMilestone += milestones.get(i).getName() + " - " + milestones.get(i).giveEnumMilestoneStatus() + " \n";
    		 }  
    	System.out.println("These are all milestones and their status in Project " + project.getName() + ":");
    	System.out.println(arrayInfoMilestone);
    	
    	// print the milestones with numeric indicators
        int i = 1;
        HashMap<String, Milestone> keyMap = new HashMap<>();
        for (Milestone milestone : project.getMilestones().values()) {
            System.out.println(i + ". " + milestone.getName());
        // save indicator to milestone in a map to retrieve the correct milestone upon input
            keyMap.put(Integer.toString(i), milestone);
            ++i;
        }
      
        // get valid input
        String userInput = null;
        while(keyMap.get(userInput) == null) {
            userInput = readString("Select the Milestone where you want to update the status");           
            }
        
    	Milestone changeStatusMilestone = keyMap.get(userInput);
    	changeStatusMilestone.changeMilestoneStatus();
    	System.out.println("Status of Milestone " + changeStatusMilestone.getName() + " was set to " + changeStatusMilestone.giveEnumMilestoneStatus() + ".\n");

        //optional to add a comment
    	String userInput2 = null;
        while(userInput2 == null) {
            userInput2 = readString("Do you want to add a comment to this milestone? Press Y or N: ");
        }
        if(userInput2.equalsIgnoreCase("Y")) {
        	// add a comment to the milestone          
	        String commentContent = readString("Please enter the comment below");
	        changeStatusMilestone.addComment(new Comment(commentContent));
	        
	        System.out.println("Comment added to Milestone " +  changeStatusMilestone.getName() + ". \n");
    	save();
		}
    	return this;
    }

    public Menu changeTaskStatus() throws IOException {
        if (!project.hasMilestones()) {
            System.out.println("This project does not have any Milestone. Please create one first! \n");
            return this;
        }
        //Select milestone
        String arrayInfoTasks = "-------------------------------------------\n";
        //Selecting the milestone
        System.out.println("List of milestones: ");
        // print the milestones with numeric indicators
        int i = 1;
        HashMap<String, Milestone> keyMap = new HashMap<>();
        for (Milestone milestone : project.getMilestones().values()) {
            System.out.println(i + ". " + milestone.getName());
             // save indicator to milestone in a map to retrieve the correct milestone upon input
            keyMap.put(Integer.toString(i), milestone);
            ++i;
        }
        // get valid input
        String userInput = null;
        while(keyMap.get(userInput) == null) {
            userInput = readString("First select the Milestone in which you want to view the Task Status: ");
        }
        Milestone selectedMilestone = keyMap.get(userInput);

        if (!selectedMilestone.hasTasks()) {
            System.out.println("This milestone does not have any Task. Please create one first! \n");
            return this;
        }	
        	
        //show all tasks in this milestone with status
        ArrayList<String> milestoneTasks = selectedMilestone.getTasks();
        for (int j = 0; j < milestoneTasks.size(); j++) {
            Task task = project.getTask(milestoneTasks.get(j));
        	arrayInfoTasks += task.getName() + " - " + task.giveEnumTaskStatus() + " \n";
        }  
        System.out.println("These are all tasks and their status in Milestone " + selectedMilestone.getName() + ":");
        System.out.println(arrayInfoTasks + " \n");
        	
        // print the all tasks in this milestone with numeric indicators
        int j = 1;
        HashMap<String, Task> tasks = new HashMap<>();
        for (String taskID : selectedMilestone.getTasks()) {
            Task task = project.getTask(taskID);
            System.out.println(j + ". " + task.getName());
            // save indicator to milestone in a map to retrieve the correct milestone upon input
            tasks.put(Integer.toString(j), task);
            ++j;
        }
        	
        // get valid input
        String userInput2 = null;
        while(tasks.get(userInput2) == null) {
            userInput2 = readString("Select the Task where you want to update the status");           
        }
            
        Task changeTaskStatusProjectManager = tasks.get(userInput2);
        changeTaskStatusProjectManager.changeTaskStatus();
        System.out.println("Status of Task " + changeTaskStatusProjectManager.getName() + " was set to " + changeTaskStatusProjectManager.giveEnumTaskStatus() + ".\n");  

        String userInput3 = null;
        while(userInput3 == null) {
            userInput3 = readString("Do you want to add a comment to this task? Press Y or N: ");
        }
        if(userInput3.equalsIgnoreCase("Y")) {
        	// add a comment to the task           
	        String commentContent = readString("Please enter the comment below");
	        changeTaskStatusProjectManager.addComment(new Comment(commentContent));
	        
	        System.out.println("Comment added to Task " +  changeTaskStatusProjectManager.getName() + ". \n");
        }
        save();
        return this;
    }

    public Menu createMilestone() throws IOException {
        String milestoneName = readString("Enter the title of your milestone");
        project.addMilestone(new Milestone(milestoneName));
        save();
        return this;
    }

    public Menu createTask() throws IOException {
        if (!project.hasMilestones()) {
            System.out.println("Create a Milestone first! ");
            return this;
        }
        String taskName = readString("Enter the title of your task");
        LocalDate dueDate = readDate("task's due date");

        // print the milestones with numeric indicators
        System.out.println("List of milestones: ");
        int i = 1;
        HashMap<String, Milestone> keyMap = new HashMap<>();
        for (Milestone milestone : project.getMilestones().values()) {
            System.out.println(i + ". " + milestone.getName());
            // save indicator to milestone in a map to retrieve the correct milestone upon input
            keyMap.put(Integer.toString(i), milestone);
            ++i;
        }
        // get valid input
        String userInput = null;
        Milestone milestone = keyMap.get(userInput);
        while (milestone == null) {
            userInput = readString("Enter the Milestone you want to assign your task to");
            milestone = keyMap.get(userInput);
        }
        // add the task to the selected milestone
        Task newTask = new Task(taskName, dueDate.toString());
        milestone.addTask(newTask);
        project.addTask(newTask);
        System.out.println("The task " + taskName + " has been assigned to milestone " + milestone.getName() + ". ");
        promptConfirmation();

        // assign to user story if wanted
        if (project.hasStories() && readString("Would you like to assign this task to a user story? " +
                "This is recommended to enable velocity and completion date calculations. Y/N").equalsIgnoreCase("y")) {
            // print the user stories with numeric indicators
            i = 1;
            HashMap<String, UserStory> storiesKeyMap = new HashMap<>();
            for (UserStory userStory : project.getStories()) {
                System.out.println(i + ". " + userStory.getName());
                // save indicator to milestone in a map to retrieve the correct user story upon input
                storiesKeyMap.put(Integer.toString(i), userStory);
                ++i;
            }
            // get valid input
            userInput = null;
            UserStory story = storiesKeyMap.get(userInput);
            while (story == null) {
                userInput = readString("Enter the User Story you want to assign your task to");
                story = storiesKeyMap.get(userInput);
            }
            // add the task to the selected user story
            story.addTask(newTask);
            System.out.println("The task " + taskName + " has been assigned to user story " + story.getName() + ". ");
            promptConfirmation();
            Task assigneTask = new Task(taskName, dueDate.toString());
            keyMap.get(userInput).addTask(assigneTask);
            Milestone assigneMilestone = keyMap.get(userInput);

            String userInput2 = null;
            userInput2 = readString("Do you want to assign the task to someone? Press Y or N: ");

            if (userInput2.equalsIgnoreCase("Y")) {
                boolean isDeveloper = false;
                while (!isDeveloper) {
                    //Select the user to assign
                    // print the user list with numeric indicators
                    System.out.println("List of Users: ");
                    int j = 1;
                    HashMap<String, User> keyMap2 = new HashMap<>();
                    for (String userID : project.getUsers()) {
                        User user = Users.getUser(userID);
                        System.out.println(j + ". " + user.getName());
                        // save indicator to user in a map to retrieve the correct user upon input
                        keyMap2.put(Integer.toString(j), user);
                        ++j;
                    }

                    // get valid input and assign task to user
                    String userInput3 = null;
                    while (keyMap2.get(userInput3) == null || !(keyMap2.get(userInput3) instanceof Developer)) {
                        userInput3 = readString("Enter the User you want to assign to the task. Make sure you select a Developer!");
                    }
                    User assigneUser = keyMap2.get(userInput3);

                    if (assigneUser instanceof Developer) {
                        isDeveloper = true;
                        assigneTask.setAssigne(assigneUser.getID());
                        Developer assigneDeveloper = (Developer) assigneUser;
                        assigneDeveloper.addTask(assigneTask);
                        System.out.println("Milestone: " + assigneMilestone.getName());
                        System.out.println(assigneUser.getName() + " is now Assigne of task " + assigneTask.getName() + ".\n");
                    } else {
                        System.out.println("This is not a developer.\n");
                    }
                }
            }
        }
        save();
        return this;
    }

public Menu changeAssigne() throws IOException {
    	if (!project.hasMilestones()) {
             System.out.println("Create a Milestone first! ");
             return this;
         }
    	//Selecting the milestone
    	System.out.println("List of milestones: ");
    	// print the milestones with numeric indicators
        int i = 1;
        HashMap<String, Milestone> keyMap = new HashMap<>();
        for (Milestone milestone : project.getMilestones().values()) {
            System.out.println(i + ". " + milestone.getName());
         // save indicator to milestone in a map to retrieve the correct milestone upon input
            keyMap.put(Integer.toString(i), milestone);
            ++i;
        }
        // get valid input
        String userInput = null;
        while(keyMap.get(userInput) == null) {
            userInput = readString("Enter the Milestone in which you want to assign a task to someone");
        }
        Milestone assigneMilestone = keyMap.get(userInput);

        if (!assigneMilestone.hasTasks()) {
            System.out.println("This Milestone does not have any Task. Please create one first! \n");
            return this;
        }	
        //show all tasks in this milestone with Assignee
        String arrayInfoTasks = "-------------------------------------------\n";
        ArrayList<String> milestoneTasks = assigneMilestone.getTasks();
        for (int j = 0; j < milestoneTasks.size(); j++) {
            Task task = project.getTask(milestoneTasks.get(j));
    		arrayInfoTasks += task.getName() + " - " + task.getAssigneName() + " \n";
    		 }  
    	System.out.println("These are all tasks and their Assignees in Milestone " + assigneMilestone.getName() + ":");
    	System.out.println(arrayInfoTasks);
        
        //Selecting the task
        System.out.println("List of tasks in this milestone: ");
         // print the tasks with numeric indicators
        int j = 1;
        HashMap<String, Task> keyMap2 = new HashMap<>();
        for (String taskID : assigneMilestone.getTasks()) {
            Task task = project.getTask(taskID);
            System.out.println(j + ". " + task.getName());
         // save indicator to tasks in a map to retrieve the correct task upon input
            keyMap2.put(Integer.toString(j), task);
            ++j;
        }
        // get valid input
        String userInput2 = null;
        while(keyMap2.get(userInput2) == null) {
            userInput2 = readString("Enter the Task you want to assign to someone");
        }
        Task assigneTask = keyMap2.get(userInput2);
        
        //get previous assignee and remove it on his/her task list
        Developer previousAssigne = (Developer) Users.getUser(assigneTask.getAssigne());
        if(previousAssigne != null) {
            previousAssigne.removeTask(assigneTask);
            System.out.println("Previous Assignee is: " + previousAssigne.getName());
        } else {System.out.println("The task is not assigned to someone.");
        }
        
        boolean isDeveloper = false;
        while(!isDeveloper) {
            //Select the user to assign
            System.out.println("List of Users: ");
            // print the user list with numeric indicators  
            int k = 1;
            HashMap<String, User> keyMap3 = new HashMap<>();
            for (String userID : project.getUsers()) {
                if (!assigneTask.hasDeveloper(userID)) {
                    User user = Users.getUser(userID);
                    System.out.println(k + ". " + user.getName());
                    // save indicator to user in a map to retrieve the correct user upon input
                    keyMap3.put(Integer.toString(k), user);
                    ++k;
                }
            }
        
            // get valid input and assign task to user
            String userInput3 = null;
            while(keyMap3.get(userInput3) == null || !(keyMap3.get(userInput3) instanceof Developer)) {
                userInput3 = readString("Enter the User you want to assign to the task. Make sure you select a Developer");
            }
            User assigneUser = keyMap3.get(userInput3);
        
            if(assigneUser instanceof Developer) {
                String devName = assigneUser.getName();
                String taskName = assigneTask.getName();
                System.out.println("1. Responsible " + System.getProperty("line.separator") +
                        "2. Team Member ");
                String input = "";
                while (!input.equals("1") && !input.equals("2")) {
                    input = readString("Is " + devName + " responsible for this task or just a " +
                            "team member? ");
                }

                String userID = assigneUser.getID();
                if (input.equals("1")) {
                    assigneTask.setAssigne(userID);
                    System.out.println(devName + " is now Assignee of task " + taskName + ". ");
                } else {
                    assigneTask.addDeveloper(userID);
                    System.out.println(devName + " has been added to the development team for " + taskName + "! ");
                }
        	    isDeveloper = true;
     	        Developer assigneDeveloper = (Developer) assigneUser;
     	        assigneDeveloper.addTask(assigneTask);
     	        System.out.println("Milestone: " + assigneMilestone.getName());
                } else {System.out.println("This is not a Developer");
            }
        }
        save();
        return this;
    }

    public Menu workOnTask() {
        if (authenticatedUser instanceof Developer && !((Developer)authenticatedUser).hasTasks()) {
            System.out.println("You have not been assigned to any Task! ");
            promptConfirmation();
            return this;
        }
        return new SelectTaskMenu(project, this, (Object object) -> new TaskMenu(project, (Task)object, this));
    }

    private Menu workOnStories() {
        if (!project.hasStories()) {
            System.out.println("No User stories have been created yet! ");
            promptConfirmation();
            return this;
        }
        return new SelectStoryMenu(project, this, (Object object) ->
                new StoryMenu(project, (UserStory)object, this), null);
    }

    public Menu createBudget() throws IOException {
        double money = readFloat("Enter the amount you want in your budget: ");
        if(money >= 1){
            project.setBudget(money);
        }
        else {
            System.out.println("Please add a number bigger than 0. ");
        }
        save();
        return this;
    }

    public Menu addBudget() throws IOException {
        Double oldBudget = project.getBudget();
        double money = readFloat("Enter the amount you want to add in to your budget: ");
        if (money >= 1) {
            if (oldBudget != null) {
                project.setBudget(money + oldBudget);
            } else {
                project.setBudget(money);
            }
        }
        else if(money < 1) {
            System.out.println("Please add a number bigger than 0. ");
        }
        save();
        return this;
    }

    public Menu addExpense() throws IOException {
        String purpose = readString("Enter what kind of expense: ");
        double money = readFloat("Enter expense: ");
        if(money >= 1) {
            project.addExpense(purpose, money);
        }
        else {
            System.out.println("Please add a number bigger than 0. ");
        }
        save();
        return this;
    }

    public Menu getSpendable(){
        System.out.println("Your spandable budget is: " + project.getSpendable());
        return this;
    }
}