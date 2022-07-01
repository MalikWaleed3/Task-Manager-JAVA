package Model;

import java.util.Date;
import java.util.UUID;

public class Comment {
    private String content;
    private String commentID;
    private String date;

    public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCommentID() {
        return commentID;
    }

    public Comment(String content) {
        this.content = content;
        date = new Date().toString();
        commentID = UUID.randomUUID().toString();
    }

}
