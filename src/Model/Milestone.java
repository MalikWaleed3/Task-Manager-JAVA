package Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Milestone {
    private String projectID;
    private String milestoneID;
    private String name;
    private ArrayList<String> tasks;
    private boolean milestoneStatus;
    private HashMap<String, Comment> comments;

    public Milestone(String name) {
        milestoneID = UUID.randomUUID().toString();
        this.name = name;
        tasks = new ArrayList<>();
        this.milestoneStatus = false;
        comments = new HashMap<>();
    }

    public Milestone() {
        milestoneID = UUID.randomUUID().toString();
        name = "Untitled Milestone";
        tasks = new ArrayList<>();
        milestoneStatus = false;
        comments = new HashMap<>();
    }

    public HashMap<String, Comment> getComments() {
		return comments;
	}
	
	public ArrayList<Comment> getArrayListComments() {
        ArrayList<Comment> milestoneComments = new ArrayList<>();
        for (Comment comment : comments.values()) {
        	milestoneComments.add(comment);
            }
        return milestoneComments;
    }

    public void addComment(Comment comment) {
        comments.put(comment.getCommentID(), comment);     
    }

    public boolean hasTasks() {
        return !tasks.isEmpty();
    }
    
    public boolean isMilestoneStatus() {
		return milestoneStatus;
	}

	public Status giveEnumMilestoneStatus() {
    	if(this.milestoneStatus) {
    		return Status.COMPLETE;
    	} else {return Status.INCOMPLETE;
    	}
	}

	public void changeMilestoneStatus() {
		if(!this.milestoneStatus) {
			this.milestoneStatus = true;
		} else {this.milestoneStatus = false;
		}
	}

    public void addTask(Task task) {
        task.setProjectID(projectID);
        tasks.add(task.getID());
    }

    public String getName() {
        return name;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    public String getID() {
        return milestoneID;
    }

    public ArrayList<String> getTasks() {
        return tasks;
    }
}
