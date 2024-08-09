package com.example.taskmanagerproject.payload;

import com.example.taskmanagerproject.model.Comment;
import com.example.taskmanagerproject.model.Task;
import com.example.taskmanagerproject.model.TaskPriority;
import com.example.taskmanagerproject.model.TaskStatus;
import lombok.Data;

import java.util.List;

@Data
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private Long authorId;
    private Long assigneeId;
    private List<Comment> comments;

    public TaskResponse(Task task) {
        this.id = task.getId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.status = task.getStatus();
        this.priority = task.getPriority();
        this.authorId = task.getAuthor() != null ? task.getAuthor().getId() : null;
        this.assigneeId = task.getAssignee() != null ? task.getAssignee().getId() : null;
    }
}