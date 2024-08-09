package com.example.taskmanagerproject.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tasks")
@Data
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    private TaskPriority priority;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    @JsonIgnore
    @Schema(hidden = true)
    private User author;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    @JsonIgnore
    @Schema(hidden = true)
    private User assignee;

    @Transient
    @Schema(description = "ID of the user to be assigned to this task")
    private Long assigneeId;
}
