package Controller.Menus;

import Model.*;

import java.util.ArrayList;
import java.util.HashMap;

	public class StatusMenu extends Menu {
		private Project project;
		private Menu previousMenu;
		private AccessLevel minimumAccessLevel;
		
    
    public StatusMenu(Project project, Menu previousMenu) {
        super();
        this.project = project;
        this.previousMenu = previousMenu;
        minimumAccessLevel = AccessLevel.Developer;
        setMenuText("What information would you like to view? ");
        addOption("Milestone Status overview", this::displayMilestoneStatusOverview);
        addOption("Task Status and comments overview", this::displayTasksStatusOverview);
        addOption("Back to Project Menu", () -> previousMenu);
    }
    
    public AccessLevel getMinimumAccessLevel() {
        return minimumAccessLevel;
    }
    
    public Menu displayMilestoneStatusOverview() {
        if (!project.hasMilestones()) {
            System.out.println("This project does not have any Milestone. Please create one first! \n");
            return this;
        }
    	//show all milestones in this project with status
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
        String userInput2 = null;
        while(keyMap.get(userInput2) == null) {
            userInput2 = readString("Select the Milestone where you want to view all comments");           
            }
    	
        Milestone viewMilestoneComments = keyMap.get(userInput2);
        
        //show all comment in this milestone
        System.out.println("These are all comments in Milestone " + viewMilestoneComments.getName() + ":");
        String arrayInfoMilestoneComments = "-------------------------------------------\n";
        ArrayList<Comment> milestoneComments = viewMilestoneComments.getArrayListComments();
        for (int l = 0; l < milestoneComments.size(); l++) { 
        	arrayInfoMilestoneComments += milestoneComments.get(l).getContent() + " - " + milestoneComments.get(l).getDate() +  " \n";
    		 }  
    	System.out.println(arrayInfoMilestoneComments + " \n");
    	
    	return this;
    }
    
    public Menu displayTasksStatusOverview() {
        if (!project.hasMilestones()) {
            System.out.println("This project does not have any Milestone. Please create one first! \n");
            return this;
        }
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
            userInput = readString("First select the Milestone in which you want to view Task Status and comments: ");
        }
        Milestone selectedMilestone = keyMap.get(userInput);

        if (!selectedMilestone.hasTasks()) {
            System.out.println("This milestone does not have any Task. Please create one first! \n");
            return this;
        }
    	
    	//show all tasks in this project with status
        ArrayList<String> milestoneTasks = selectedMilestone.getTasks();
        for (int j = 0; j < milestoneTasks.size(); j++) {
            Task task = project.getTask(milestoneTasks.get(j));
    		arrayInfoTasks += task.getName() + " - " + task.giveEnumTaskStatus() + " \n";
    		 }  
    	System.out.println("These are all tasks and their status in Milestone " + selectedMilestone.getName() + ":");
    	System.out.println(arrayInfoTasks + " \n");

        // print the all tasks with numeric indicators
        int k = 1;
        HashMap<String, Task> tasks = new HashMap<>();
        for (String taskID : selectedMilestone.getTasks()) {
        	Task task = project.getTask(taskID);
            System.out.println(k + ". " + task.getName());
             // save indicator to milestone in a map to retrieve the correct milestone upon input
            tasks.put(Integer.toString(k), task);
            ++k;
        }
        
        // get valid input
        String userInput2 = null;
        while(tasks.get(userInput2) == null) {
            userInput2 = readString("Select the Task where you want to view all comments");
            }
        Task viewComments = tasks.get(userInput2);
        
        //show all comment in this task
        System.out.println("These are all comments in Task " + viewComments.getName() + ":");
        String arrayInfoComments = "-------------------------------------------\n";
        ArrayList<Comment> taskComments = viewComments.getArrayListComments();
        for (int l = 0; l < taskComments.size(); l++) { 
    		arrayInfoComments += taskComments.get(l).getContent() + " - " + taskComments.get(l).getDate() +  " \n";
    		 }  
    	System.out.println(arrayInfoComments + " \n");
    	
    	return this;
    }
}
