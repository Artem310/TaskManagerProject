package com.example.taskmanagerproject.service;

import com.example.taskmanagerproject.exception.ResourceNotFoundException;
import com.example.taskmanagerproject.model.Comment;
import com.example.taskmanagerproject.model.Task;
import com.example.taskmanagerproject.model.User;
import com.example.taskmanagerproject.repository.CommentRepository;
import com.example.taskmanagerproject.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserService userService;

    public Comment addComment(Long taskId, Comment comment, String userEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        User user = userService.findByEmail(userEmail);

        comment.setTask(task);
        comment.setUser(user);
        comment.setCreatedAt(LocalDateTime.now());

        return commentRepository.save(comment);
    }

    public Page<Comment> getComments(Long taskId, Pageable pageable) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        try {
            return commentRepository.findByTask(task, pageable);
        } catch (Exception e) {
            // Логирование ошибки
            throw new RuntimeException("Error retrieving comments for task: " + taskId, e);
        }
    }

    public List<Comment> getCommentsByTaskId(Long taskId) {
        return commentRepository.findByTaskId(taskId);
    }
}
