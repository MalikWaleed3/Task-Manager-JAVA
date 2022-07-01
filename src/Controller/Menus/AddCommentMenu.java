package Controller.Menus;

import Model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

	public class AddCommentMenu extends Menu {
		private Project project;
		private Menu previousMenu;
		private AccessLevel minimumAccessLevel;
		
	    public AddCommentMenu(Project project, Menu previousMenu) {
	        super();
	        this.project = project;
	        this.previousMenu = previousMenu;
	        minimumAccessLevel = AccessLevel.ProjectManager;
	        setMenuText("Select on of the below options? ");
	        addOption("Add a comment to a Milestone", this::addCommentToMilestone);
	        addOption("Add a comment to a Task", this::addCommentToTask);
	        addOption("Back to Project Menu", () -> previousMenu);
	    }
	    
	    public AccessLevel getMinimumAccessLevel() {
	        return minimumAccessLevel;
	    }	

	    public Menu addCommentToMilestone() throws IOException {
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
	            userInput = readString("Select the Milestone where you want to add a comment");           
			}
	        Milestone addCommentMilestone = keyMap.get(userInput);
	        // add a comment to the milestone          
	        String commentContent = readString("Please enter the comment below");
	        addCommentMilestone.addComment(new Comment(commentContent));

	        System.out.println("Comment added to Milestone " +  addCommentMilestone.getName() + ". \n");
	        save();
	    	return this;
	    }
	    
	    public Menu addCommentToTask() throws IOException {
	    	if (!project.hasMilestones()) {
	            System.out.println("This project does not have any Milestone. Please create one first! \n");
	            return this;
	        }
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
	            userInput = readString("First select a Milestone");           
	            }
	        Milestone addCommentTask = keyMap.get(userInput);
	        
	        if (!addCommentTask.hasTasks()) {
	            System.out.println("This milestone does not have any Task. Please create one first! \n");
	            return this;
	        }
	    	
	      //show all tasks in this project with status
	        String arrayInfoTasks = "-------------------------------------------\n";
	        ArrayList<String> milestoneTasks = addCommentTask.getTasks();
	        for (int j = 0; j < milestoneTasks.size(); j++) {
				Task task = project.getTask(milestoneTasks.get(j));
				arrayInfoTasks += task.getName() + " - " + task.giveEnumTaskStatus() + " \n";
			}

	    	System.out.println("These are all tasks and their status in Milestone " + addCommentTask.getName() + ":");
	    	System.out.println(arrayInfoTasks + " \n");
	    	
	    	// print the all tasks with numeric indicators
	        int k = 1;
	        HashMap<String, Task> tasks = new HashMap<>();
	        for (String taskID : addCommentTask.getTasks()) {
	        	Task task = project.getTask(taskID);
	        	System.out.println(k + ". " + task.getName());
	             // save indicator to milestone in a map to retrieve the correct milestone upon input
	                tasks.put(Integer.toString(k), task);
	                ++k;
	        }
	        
	        // get valid input
	        String userInput2 = null;
	        while(tasks.get(userInput2) == null) {
	            userInput2 = readString("Select the Task where you want to add a comment");
			}
	    	
	        Task addCommentToTaskAsManager = tasks.get(userInput2);
	        
	        //Add comment to Task
	        String commentContent = readString("Please enter the comment below");
	        addCommentToTaskAsManager.addComment(new Comment(commentContent));
	        
	        System.out.println("Comment added to Milestone " +  addCommentToTaskAsManager.getName() + ". \n");
	        save();
	    	return this;
	    }
		
}
