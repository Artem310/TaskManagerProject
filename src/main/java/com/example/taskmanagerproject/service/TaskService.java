package com.example.taskmanagerproject.service;

import com.example.taskmanagerproject.exception.ResourceNotFoundException;
import com.example.taskmanagerproject.model.Task;
import com.example.taskmanagerproject.model.TaskPriority;
import com.example.taskmanagerproject.model.TaskStatus;
import com.example.taskmanagerproject.model.User;
import com.example.taskmanagerproject.payload.TaskResponse;
import com.example.taskmanagerproject.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;
    private final CommentService commentService;


    public Task createTask(Task task, String authorEmail) {
        User author = userService.findByEmail(authorEmail);
        task.setAuthor(author);

        if (task.getAssigneeId() != null) {
            User assignee = userService.findById(task.getAssigneeId());
            task.setAssignee(assignee);
        }

        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.PENDING);
        }

        return taskRepository.save(task);
    }

    public Task updateTask(Long id, Task taskDetails, String userEmail) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        User user = userService.findByEmail(userEmail);

        if (!task.getAuthor().equals(user) && (task.getAssignee() == null || !task.getAssignee().equals(user))) {
            throw new AccessDeniedException("You don't have permission to update this task");
        }

        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setPriority(taskDetails.getPriority());

        if (task.getAssignee() != null && task.getAssignee().equals(user)) {
            task.setStatus(taskDetails.getStatus());
        }

        if (taskDetails.getAssigneeId() != null) {
            User newAssignee = userService.findById(taskDetails.getAssigneeId());
            task.setAssignee(newAssignee);
        }

        return taskRepository.save(task);
    }

    public Page<TaskResponse> getTasks(String userEmail, Long authorId, Long assigneeId, TaskStatus status, TaskPriority priority, Pageable pageable) {
        User currentUser = userService.findByEmail(userEmail);

        User author = (authorId != null) ? userService.findById(authorId) : null;
        User assignee = (assigneeId != null) ? userService.findById(assigneeId) : null;

        Page<Task> tasks;
        if (author != null && assignee != null) {
            tasks = taskRepository.findByAuthorAndAssignee(author, assignee, pageable);
        } else if (author != null) {
            tasks = taskRepository.findByAuthor(author, pageable);
        } else if (assignee != null) {
            tasks = taskRepository.findByAssignee(assignee, pageable);
        } else {
            tasks = taskRepository.findAll(pageable);
        }

        return tasks.map(task -> {
            TaskResponse response = new TaskResponse(task);
            response.setComments(commentService.getCommentsByTaskId(task.getId()));
            return response;
        });
    }

    public void deleteTask(Long id, String userEmail) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        User user = userService.findByEmail(userEmail);

        if (!task.getAuthor().equals(user)) {
            throw new AccessDeniedException("You don't have permission to delete this task");
        }

        taskRepository.delete(task);
    }
}
