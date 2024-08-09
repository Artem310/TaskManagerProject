package com.example.taskmanagerproject.payload;

import com.example.taskmanagerproject.model.Task;
import com.example.taskmanagerproject.model.TaskPriority;
import com.example.taskmanagerproject.model.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request object for creating a new task")
public class TaskCreateRequest {

    @Schema(description = "Title of the task", example = "Implement new feature")
    private String title;

    @Schema(description = "Description of the task", example = "Implement the new login functionality")
    private String description;

    @Schema(description = "Status of the task", example = "PENDING")
    private TaskStatus status;

    @Schema(description = "Priority of the task", example = "HIGH")
    private TaskPriority priority;

    @Schema(description = "ID of the user to be assigned to this task", example = "1")
    private Long assigneeId;

    public Task toTask() {
        Task task = new Task();
        task.setTitle(this.title);
        task.setDescription(this.description);
        task.setStatus(this.status);
        task.setPriority(this.priority);
        task.setAssigneeId(this.assigneeId);
        return task;
    }
}