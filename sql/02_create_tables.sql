USE task_manager;

DROP TABLE IF EXISTS tasks;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户 ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '登录用户名',
    password_hash VARCHAR(100) NOT NULL COMMENT 'BCrypt 加密后的密码',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '用户表';

CREATE TABLE tasks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '任务 ID',
    user_id BIGINT NOT NULL COMMENT '所属用户 ID',
    title VARCHAR(100) NOT NULL COMMENT '任务标题',
    description VARCHAR(500) NULL COMMENT '任务描述',
    status VARCHAR(20) NOT NULL DEFAULT 'TODO' COMMENT '任务状态：TODO/DOING/DONE',
    due_date DATE NULL COMMENT '截止日期',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT fk_tasks_user_id FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_tasks_user_status (user_id, status),
    INDEX idx_tasks_due_date (due_date)
) COMMENT '任务表';
