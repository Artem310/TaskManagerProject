package com.example.taskmanagerproject.service;

import com.example.taskmanagerproject.model.Task;
import com.example.taskmanagerproject.model.User;
import com.example.taskmanagerproject.model.TaskStatus;
import com.example.taskmanagerproject.model.TaskPriority;
import com.example.taskmanagerproject.repository.TaskRepository;
import com.example.taskmanagerproject.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTask_Success() {
        // Arrange
        String authorEmail = "author@example.com";
        User author = new User();
        author.setEmail(authorEmail);

        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setStatus(TaskStatus.PENDING);
        task.setPriority(TaskPriority.MEDIUM);

        when(userService.findByEmail(authorEmail)).thenReturn(author);
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // Act
        Task createdTask = taskService.createTask(task, authorEmail);

        // Assert
        assertNotNull(createdTask);
        assertEquals("Test Task", createdTask.getTitle());
        assertEquals(author, createdTask.getAuthor());
        assertEquals(TaskStatus.PENDING, createdTask.getStatus());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void updateTask_Success() {
        // Arrange
        Long taskId = 1L;
        String userEmail = "user@example.com";
        User user = new User();
        user.setEmail(userEmail);

        Task existingTask = new Task();
        existingTask.setId(taskId);
        existingTask.setTitle("Old Title");
        existingTask.setAuthor(user);

        Task updatedTask = new Task();
        updatedTask.setTitle("New Title");
        updatedTask.setDescription("New Description");
        updatedTask.setStatus(TaskStatus.IN_PROGRESS);
        updatedTask.setPriority(TaskPriority.HIGH);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(userService.findByEmail(userEmail)).thenReturn(user);
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        // Act
        Task result = taskService.updateTask(taskId, updatedTask, userEmail);

        // Assert
        assertNotNull(result);
        assertEquals("New Title", result.getTitle());
        assertEquals("New Description", result.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());
        assertEquals(TaskPriority.HIGH, result.getPriority());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void updateTask_TaskNotFound() {
        // Arrange
        Long taskId = 1L;
        String userEmail = "user@example.com";
        Task updatedTask = new Task();

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.updateTask(taskId, updatedTask, userEmail);
        });
    }

    @Test
    void updateTask_Unauthorized() {
        // Arrange
        Long taskId = 1L;
        String userEmail = "user@example.com";
        User user = new User();
        user.setEmail(userEmail);

        User anotherUser = new User();
        anotherUser.setEmail("another@example.com");

        Task existingTask = new Task();
        existingTask.setId(taskId);
        existingTask.setAuthor(anotherUser);

        Task updatedTask = new Task();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(userService.findByEmail(userEmail)).thenReturn(user);

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> {
            taskService.updateTask(taskId, updatedTask, userEmail);
        });
    }

    @Test
    void deleteTask_Success() {
        // Arrange
        Long taskId = 1L;
        String userEmail = "user@example.com";
        User user = new User();
        user.setEmail(userEmail);

        Task task = new Task();
        task.setId(taskId);
        task.setAuthor(user);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userService.findByEmail(userEmail)).thenReturn(user);

        // Act
        taskService.deleteTask(taskId, userEmail);

        verify(taskRepository, times(1)).delete(task);
    }

    @Test
    void deleteTask_TaskNotFound() {

        Long taskId = 1L;
        String userEmail = "user@example.com";

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.deleteTask(taskId, userEmail);
        });
    }

    @Test
    void deleteTask_Unauthorized() {

        Long taskId = 1L;
        String userEmail = "user@example.com";
        User user = new User();
        user.setEmail(userEmail);

        User anotherUser = new User();
        anotherUser.setEmail("another@example.com");

        Task task = new Task();
        task.setId(taskId);
        task.setAuthor(anotherUser);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userService.findByEmail(userEmail)).thenReturn(user);

        assertThrows(AccessDeniedException.class, () -> {
            taskService.deleteTask(taskId, userEmail);
        });
    }
}