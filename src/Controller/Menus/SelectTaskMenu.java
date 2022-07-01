package Controller.Menus;

import Model.*;

import java.util.ArrayList;
import java.util.HashMap;

public class SelectTaskMenu extends Menu {

    private AccessLevel minimumAccessLevel;
    private Project project;
    private Menu previousMenu;

    public SelectTaskMenu(Project project, Menu previousMenu, IObjectCallback taskCallback) {
        super();
        minimumAccessLevel = AccessLevel.Developer;
        this.project = project;
        this.previousMenu = previousMenu;
        setMenuText("Select your task: ");
        ArrayList<Task> tasks = ((Developer)authenticatedUser).getTasks(project);
        for (Task task : tasks) {
            addOption(task.getName(), () -> taskCallback.run(task));
        }
        addOption("Return to previous Menu", () -> previousMenu);

    }

    public Menu changeTaskStatus(Project project) {
	    if (authenticatedUser instanceof Developer && !((Developer)authenticatedUser).hasTasks()) {
            System.out.println("You have not been assigned to any Task! ");
            return this;
        } else {
    	    //show all assigned tasks in this project with status
            String arrayInfoTask = "-------------------------------------------\n";
            ArrayList<Task> developerTasks = ((Developer)authenticatedUser).getTasks(project);
            for (int j = 0; j < developerTasks.size(); j++) { 
    		    arrayInfoTask += developerTasks.get(j).getName() + " - " + developerTasks.get(j).giveEnumTaskStatus() + " \n";
    		 }  
    	    System.out.println("These are all your tasks in the project " + project.getName() + ":");
    	    System.out.println(arrayInfoTask + " \n");	
    	
    	    Developer changeStatus = (Developer) authenticatedUser;
    	
    	    // print the assigned tasks with numeric indicators
            int i = 1;
            HashMap<String, Task> tasks = new HashMap<>();
            for (String taskID : changeStatus.getTasks()) {
            	Task task = project.getTask(taskID);
            	if (task == null) {
            		continue;
				}
        	    System.out.println(i + ". " + task.getName());
                // save indicator to milestone in a map to retrieve the correct milestone upon input
                tasks.put(Integer.toString(i), task);
                ++i;
            }
        
            // get valid input
            String userInput = null;
            while(tasks.get(userInput) == null) {
                userInput = readString("Select the Task where you want to update the status");           
            }
        
    	    Task changeTaskStatusDeveloper = tasks.get(userInput);
    	    changeTaskStatusDeveloper.changeTaskStatus();
    	    System.out.println("Status of Task " + changeTaskStatusDeveloper.getName() + " was set to " + changeTaskStatusDeveloper.giveEnumTaskStatus() + ".\n");  
    	
    	    String userInput3 = null;
   		    while(userInput3 == null) {
                userInput3 = readString("Do you want to add a comment to this task? Press Y or N: ");
   		    }
   		    if(userInput3.equalsIgnoreCase("Y")) {
   			    // add a comment to the task           
	            String commentContent = readString("Please enter the comment below");
	            changeTaskStatusDeveloper.addComment(new Comment(commentContent));
	        
	        System.out.println("Comment added to Task " +  changeTaskStatusDeveloper.getName() + ". \n");	
		    }			
        }
		
    	return this;
    }

    public Menu	addCommentToTask(Project project) {
		if (authenticatedUser instanceof Developer && !((Developer)authenticatedUser).hasTasks()) {
	        System.out.println("You have not been assigned to any Task! ");
	        return this;
	    } else {
	    	//show all assigned tasks in this project with status
	        String arrayInfoTask = "-------------------------------------------\n";
	        ArrayList<Task> developerTasks = ((Developer)authenticatedUser).getTasks(project);
	        for (int j = 0; j < developerTasks.size(); j++) { 
	    		arrayInfoTask += developerTasks.get(j).getName() + " - " + developerTasks.get(j).giveEnumTaskStatus() + " \n";
	    		 }  
	    	System.out.println("These are all your tasks in the project " + project.getName() + ":");
	    	System.out.println(arrayInfoTask + " \n");	
	    	
	    	Developer addComment = (Developer) authenticatedUser;
	    	
	    	// print the assigned tasks with numeric indicators
	        int i = 1;
	        HashMap<String, Task> tasks = new HashMap<>();
	        for (String taskID : addComment.getTasks()) {
	        	Task task = project.getTask(taskID);
	        	if (task == null) {
	        		continue;
				}
	        	 System.out.println(i + ". " + task.getName());
	             // save indicator to milestone in a map to retrieve the correct milestone upon input
	                tasks.put(Integer.toString(i), task);
	                ++i;
	        }
	        
	     // get valid input
	        String userInput = null;
	        while(tasks.get(userInput) == null) {
	            userInput = readString("Select the Task where you want to add a comment");           
	        }
	    // add a comment to the task           
	    	Task addCommentAsDeveloper = tasks.get(userInput);
	        String commentContent = readString("Please enter the comment below");
	        addCommentAsDeveloper.addComment(new Comment(commentContent));
	        
	        System.out.println("Comment added to Task " + addCommentAsDeveloper.getName() + ". \n");	
	    }
		return this;
	}

    public AccessLevel getMinimumAccessLevel() {
        return minimumAccessLevel;
    }
}
