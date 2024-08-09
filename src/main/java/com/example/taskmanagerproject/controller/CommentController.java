package com.example.taskmanagerproject.controller;

import com.example.taskmanagerproject.model.Comment;
import com.example.taskmanagerproject.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/tasks/{taskId}/comments")
@RequiredArgsConstructor
@Tag(name = "Comment Management", description = "APIs for managing comments on tasks")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @Operation(summary = "Add a comment to a task", description = "Adds a new comment to the specified task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Comment added successfully",
                    content = @Content(schema = @Schema(implementation = Comment.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<Comment> addComment(@PathVariable Long taskId, @RequestBody @Valid Comment comment, Authentication auth) {
        String userEmail = auth.getName();
        Comment addedComment = commentService.addComment(taskId, comment, userEmail);
        return ResponseEntity.created(URI.create("/api/tasks/" + taskId + "/comments/" + addedComment.getId())).body(addedComment);
    }

    @GetMapping
    @Operation(summary = "Get comments for a task", description = "Retrieves all comments for the specified task with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved comments",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })

    public ResponseEntity<Page<Comment>> getComments(
            @PathVariable Long taskId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageRequest safePageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        Page<Comment> comments = commentService.getComments(taskId, safePageable);
        return ResponseEntity.ok(comments);
    }
}
