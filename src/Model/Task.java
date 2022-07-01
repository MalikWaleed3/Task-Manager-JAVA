package Model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Task {
    private String projectID;
    private String taskID;
    private String name;
    private String dueDate;
    private boolean taskStatus;
    private String assigne;
    @JsonProperty("MyDeveloperList")
    private ArrayList<String> developers;
    private HashMap<String, Comment> comments;

    public Task(String name, String dueDate) {
        taskID = UUID.randomUUID().toString();
        this.name = name;
        this.dueDate = dueDate;
        this.taskStatus = false;
        comments = new HashMap<>();
        developers = new ArrayList<>();
    }

    public HashMap<String, Comment> getComments() {
		return comments;
	}
	
	public ArrayList<Comment> getArrayListComments() {
        ArrayList<Comment> taskComments = new ArrayList<>();
        for (Comment comment : comments.values()) {
        		taskComments.add(comment);
            }
        return taskComments;
    }

    public void addComment(Comment comment) {
        comments.put(comment.getCommentID(), comment);
    }

    public Task() {
        taskID = UUID.randomUUID().toString();
        name = "Untitled Task";
        dueDate = "No due date set";
        taskStatus = false;
        comments = new HashMap<>();
    }

    public void addDeveloper(String userID) {
        if (developers.isEmpty()) {
            developers.add(userID);
        } else if (!developers.contains(userID)) {
            developers.add(userID);
        }
    }

    public boolean hasDeveloper(String userID) {
        if (developers != null && developers.contains(userID)) {
            return true;
        } else return assigne != null && assigne.equals(userID);
    }

    public String getAssigne() {
		return assigne;
	}

	public String getDueDate() {
        return dueDate;
    }

    public boolean isTaskStatus() {
        return taskStatus;
    }

    public Status giveEnumTaskStatus() {
        if(this.taskStatus) {
            return Status.COMPLETE;
        } else {return Status.INCOMPLETE;
        }
    }

    public void changeTaskStatus() {
        if(!this.taskStatus) {
            this.taskStatus = true;
        } else {this.taskStatus = false;
        }
    }

    public String getAssigneName() {
        if (assigne == null) {
            return "Unassigned";
        }
    	User assignee = Users.getUser(assigne);
    	if(assignee == null) {
    		return "Unassigned";
    	} else {return assignee.getName();}
	}

	public void setAssigne(String assigneeID) {
		this.assigne = assigneeID;
	}

    public String getID() {
        return taskID;
    }

    public String getProjectID() {
        return projectID;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    public String getName() {
        return name;
    }

}
