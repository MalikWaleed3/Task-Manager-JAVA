package Controller.Menus;

import Model.*;

import java.io.IOException;
import java.time.LocalDate;

public class TaskMenu extends Menu {

    private AccessLevel minimumAccessLevel;
    private Project project;
    private Task task;
    private StopWatch stopWatch;
    private boolean inProgress;
    private ProjectMenu projectMenu;

    public TaskMenu(Project project, Task task, ProjectMenu projectMenu) {
        minimumAccessLevel = AccessLevel.Developer;
        this.project = project;
        this.task = task;
        this.projectMenu = projectMenu;
        stopWatch = new StopWatch();
        inProgress = false;
        setMenuText("This Task has status " + task.giveEnumTaskStatus() + " and is due on: " + task.getDueDate() + ". What would you like to do: ");
        addOption("Start working on current Task", this::startWork);
        addOption("Stop working on current Task", this::stopWork);
        addOption("Select a different Task", () -> new SelectTaskMenu(project, this,
                this::selectTask));
        addOption("Finalize Task Status", this::changeTaskStatus);
        addOption("Add a comment to the Task", this::addCommentToTask);
        addOption("Return to Project Menu", () -> projectMenu);
    }

    public Menu changeTaskStatus() throws IOException {
        task.changeTaskStatus();
        System.out.println("Status of Task " + task.getName() + " was set to " + task.giveEnumTaskStatus() + ".\n");

        //optional to add a comment
        String userInput = null;
   		while(userInput == null) {
   			userInput = readString("Do you want to add a comment to this task? Press Y or N: ");
   		}
   		if(userInput.equalsIgnoreCase("Y")) {
   			// add a comment to the task           
	        String commentContent = readString("Please enter the comment below");
	        task.addComment(new Comment(commentContent));
	        
	        System.out.println("Comment added to Task " +  task.getName() + ". \n");	
		}
        save();
        return this;
    }

    public Menu addCommentToTask() throws IOException {
    	String commentContent = readString("Please enter the comment below");
    	task.addComment(new Comment(commentContent));
        
        System.out.println("Comment added to Task " + task.getName() + ". \n");
    	save();
        return this;
    }

    private Menu selectTask(Object task) {
        this.task = (Task)task;
        return this;
    }

    public Menu startWork() throws IOException {
        stopWatch.starTimer();
        inProgress = true;
        System.out.println("The timer has been started. ");
        if (project.getStartDate() == null) {
            project.setStartDate(LocalDate.now().toString());
        }
        save();
        return this;
    }

    public Menu stopWork() throws IOException {
        if (!inProgress) {
            System.out.println("You haven't started working on anything! ");
            return this;
        }
        stopWatch.endTimer();
        double time = stopWatch.getMinutes();
        ((Developer)authenticatedUser).addTime(task, time);
        inProgress = false;
        System.out.println("The timer has been stopped. You have worked " + time + " minutes. ");
        save();
        return this;
    }

    public AccessLevel getMinimumAccessLevel() {
        return minimumAccessLevel;
    }
}
