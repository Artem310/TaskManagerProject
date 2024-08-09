package com.example.taskmanagerproject.controller;

import com.example.taskmanagerproject.model.Task;
import com.example.taskmanagerproject.model.TaskPriority;
import com.example.taskmanagerproject.model.TaskStatus;
import com.example.taskmanagerproject.payload.TaskCreateRequest;
import com.example.taskmanagerproject.payload.TaskResponse;
import com.example.taskmanagerproject.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.net.URI;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Task Management", description = "APIs for managing tasks")
public class TaskController {

    private final TaskService taskService;
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @PostMapping
    @Operation(summary = "Create a new task", description = "Creates a new task with the given details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task created successfully",
                    content = @Content(schema = @Schema(implementation = Task.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Task> createTask(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Task details", required = true,
                    content = @Content(schema = @Schema(implementation = TaskCreateRequest.class)))
            @RequestBody @Valid TaskCreateRequest taskCreateRequest,
            Authentication auth) {
        logger.info("Received request to create task: {}", taskCreateRequest);
        String userEmail = auth.getName();
        logger.info("User email from authentication: {}", userEmail);
        Task createdTask = taskService.createTask(taskCreateRequest.toTask(), userEmail);
        return ResponseEntity.created(URI.create("/api/tasks/" + createdTask.getId())).body(createdTask);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a task", description = "Updates an existing task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody @Valid Task taskDetails, Authentication auth) {
        String userEmail = auth.getName();
        Task updatedTask = taskService.updateTask(id, taskDetails, userEmail);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task", description = "Deletes an existing task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<?> deleteTask(@PathVariable Long id, Authentication auth) {
        String userEmail = auth.getName();
        taskService.deleteTask(id, userEmail);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Get tasks", description = "Retrieve tasks with optional filtering and pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved tasks",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<TaskResponse>> getTasks(
            @Parameter(description = "ID of the task author") @RequestParam(required = false) Long authorId,
            @Parameter(description = "ID of the task assignee") @RequestParam(required = false) Long assigneeId,
            @Parameter(description = "Status of the task") @RequestParam(required = false) TaskStatus status,
            @Parameter(description = "Priority of the task") @RequestParam(required = false) TaskPriority priority,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "20") int size,
            Authentication auth) {

        String userEmail = auth.getName();
        Pageable pageable = PageRequest.of(page, size);
        Page<TaskResponse> tasks = taskService.getTasks(userEmail, authorId, assigneeId, status, priority, pageable);
        return ResponseEntity.ok(tasks);
    }
}
