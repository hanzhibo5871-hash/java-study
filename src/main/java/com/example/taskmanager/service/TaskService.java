package com.example.taskmanager.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.taskmanager.dto.TaskCreateRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.dto.TaskUpdateRequest;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.TaskStatus;
import com.example.taskmanager.exception.BusinessException;
import com.example.taskmanager.mapper.TaskMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {
    private final TaskMapper taskMapper;

    public TaskService(TaskMapper taskMapper) {
        this.taskMapper = taskMapper;
    }

    public List<TaskResponse> list(Long userId, TaskStatus status, String keyword, int page, int size) {
        LambdaQueryWrapper<Task> query = new LambdaQueryWrapper<Task>()
                .eq(Task::getUserId, userId)
                .eq(status != null, Task::getStatus, status)
                .like(StringUtils.hasText(keyword), Task::getTitle, keyword)
                .orderByDesc(Task::getCreatedAt);

        Page<Task> result = taskMapper.selectPage(Page.of(page, size), query);
        return result.getRecords().stream().map(TaskResponse::from).toList();
    }

    public TaskResponse getById(Long userId, Long id) {
        return TaskResponse.from(findOwnedTask(userId, id));
    }

    public TaskResponse create(Long userId, TaskCreateRequest request) {
        Task task = new Task();
        task.setUserId(userId);
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(request.status() == null ? TaskStatus.TODO : request.status());
        task.setDueDate(request.dueDate());
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        taskMapper.insert(task);
        return TaskResponse.from(task);
    }

    public TaskResponse update(Long userId, Long id, TaskUpdateRequest request) {
        Task task = findOwnedTask(userId, id);
        if (request.title() != null) {
            if (!StringUtils.hasText(request.title())) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "Title cannot be blank");
            }
            task.setTitle(request.title());
        }
        if (request.description() != null) {
            task.setDescription(request.description());
        }
        if (request.status() != null) {
            task.setStatus(request.status());
        }
        if (request.dueDate() != null) {
            task.setDueDate(request.dueDate());
        }
        task.setUpdatedAt(LocalDateTime.now());
        taskMapper.updateById(task);
        return TaskResponse.from(task);
    }

    public void delete(Long userId, Long id) {
        Task task = findOwnedTask(userId, id);
        taskMapper.deleteById(task.getId());
    }

    private Task findOwnedTask(Long userId, Long id) {
        Task task = taskMapper.selectOne(new LambdaQueryWrapper<Task>()
                .eq(Task::getId, id)
                .eq(Task::getUserId, userId));
        if (task == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "Task not found");
        }
        return task;
    }
}
