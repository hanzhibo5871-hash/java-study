package com.example.taskmanager.controller;

import com.example.taskmanager.common.ApiResponse;
import com.example.taskmanager.dto.TaskCreateRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.dto.TaskUpdateRequest;
import com.example.taskmanager.entity.TaskStatus;
import com.example.taskmanager.security.SecurityUtils;
import com.example.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ApiResponse<List<TaskResponse>> list(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {
        Long userId = SecurityUtils.currentUser().id();
        return ApiResponse.ok(taskService.list(userId, status, keyword, page, size));
    }

    @PostMapping
    public ApiResponse<TaskResponse> create(@Valid @RequestBody TaskCreateRequest request) {
        Long userId = SecurityUtils.currentUser().id();
        return ApiResponse.ok(taskService.create(userId, request));
    }

    @GetMapping("/{id}")
    public ApiResponse<TaskResponse> getById(@PathVariable Long id) {
        Long userId = SecurityUtils.currentUser().id();
        return ApiResponse.ok(taskService.getById(userId, id));
    }

    @PutMapping("/{id}")
    public ApiResponse<TaskResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody TaskUpdateRequest request
    ) {
        Long userId = SecurityUtils.currentUser().id();
        return ApiResponse.ok(taskService.update(userId, id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        Long userId = SecurityUtils.currentUser().id();
        taskService.delete(userId, id);
        return ApiResponse.ok();
    }
}
