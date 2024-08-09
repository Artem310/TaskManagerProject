package com.example.taskmanagerproject.controller;

import com.example.taskmanagerproject.model.Task;
import com.example.taskmanagerproject.model.TaskStatus;
import com.example.taskmanagerproject.model.TaskPriority;
import com.example.taskmanagerproject.payload.TaskCreateRequest;
import com.example.taskmanagerproject.payload.TaskResponse;
import com.example.taskmanagerproject.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    private TaskCreateRequest taskCreateRequest;
    private Task task;
    private TaskResponse taskResponse;

    @BeforeEach
    void setUp() {
        taskCreateRequest = new TaskCreateRequest();
        taskCreateRequest.setTitle("Test Task");
        taskCreateRequest.setDescription("Test Description");
        taskCreateRequest.setStatus(TaskStatus.PENDING);
        taskCreateRequest.setPriority(TaskPriority.MEDIUM);

        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setStatus(TaskStatus.PENDING);
        task.setPriority(TaskPriority.MEDIUM);

        taskResponse = new TaskResponse(task);
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void createTask_Success() throws Exception {
        when(taskService.createTask(any(Task.class), eq("user@example.com"))).thenReturn(task);

        mockMvc.perform(post("/api/tasks")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void updateTask_Success() throws Exception {
        when(taskService.updateTask(eq(1L), any(Task.class), eq("user@example.com"))).thenReturn(task);

        mockMvc.perform(put("/api/tasks/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskCreateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void deleteTask_Success() throws Exception {
        mockMvc.perform(delete("/api/tasks/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void getTasks_Success() throws Exception {
        when(taskService.getTasks(eq("user@example.com"), any(), any(), any(), any(), any()))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(Arrays.asList(taskResponse)));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].title").value("Test Task"))
                .andExpect(jsonPath("$.content[0].status").value("PENDING"));
    }
}