# 第 1 周：Java 基础与任务对象

这一周不要急着写完整项目。目标只有一个：把 Java 的“类型、变量、方法、类、对象”串起来，理解一个真实业务对象是怎么被代码表达出来的。

本周你会围绕 `Task` 任务对象学习。任务对象以后会出现在控制台项目、数据库表、Spring Boot 接口里，所以这是整个项目的第一块地基。

## 本周学习成果

完成本周后，你应该能够做到：

- 看懂 `Task.java` 中每个字段和方法的作用。
- 独立创建一个 `Task` 对象并打印出来。
- 修改任务标题、描述、状态、截止日期。
- 解释 `private`、构造方法、`get/set`、`toString` 分别解决什么问题。
- 明白“类是模板，对象是具体数据”这句话。

## 第 1 天：看懂一个 Java 类

打开：

```text
console-practice/src/Task.java
```

先只看这几行：

```java
public class Task {
    private final int id;
    private String title;
    private String description;
    private TaskStatus status;
    private LocalDate dueDate;
}
```

你要理解：

- `Task` 是类名，表示“任务”这种东西。
- `id` 是任务编号。
- `title` 是任务标题。
- `description` 是任务描述。
- `status` 是任务状态。
- `dueDate` 是截止日期。

这里每一行都叫字段。字段就是一个对象内部保存的数据。

### 今日练习

用自己的话写下这些字段的含义：

```text
id:
title:
description:
status:
dueDate:
```

### 结果要求

你能回答：

- 一个任务为什么需要 `id`？
- `title` 和 `description` 有什么区别？
- 为什么 `status` 不用普通字符串，而是用 `TaskStatus`？

## 第 2 天：理解构造方法

看 `Task.java` 里的构造方法：

```java
public Task(int id, String title, String description, LocalDate dueDate) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.status = TaskStatus.TODO;
    this.dueDate = dueDate;
}
```

构造方法的作用：创建对象时，给对象放入初始数据。

比如：

```java
Task task = new Task(1, "学习 Java 对象", "完成 Task 类练习", null);
```

这行代码做了四件事：

- 创建一个新的任务对象。
- 把 `id` 设置为 `1`。
- 把 `title` 设置为 `学习 Java 对象`。
- 把 `description` 设置为 `完成 Task 类练习`。

`status` 没有从外面传入，因为构造方法内部默认设置成了 `TaskStatus.TODO`。

### 今日练习

打开：

```text
console-practice/src/TaskDemo.java
```

把里面的任务标题改成你自己的学习任务，例如：

```java
Task task = new Task(1, "复习 Java 方法", "理解参数和返回值", null);
```

运行：

```powershell
cd E:\task-manager-api\console-practice\src
javac *.java
java TaskDemo
```

### 结果要求

控制台应该打印出类似内容：

```text
Task{id=1, title='复习 Java 方法', description='理解参数和返回值', status=TODO, dueDate=null}
```

你能解释：

- `new Task(...)` 是在创建对象。
- 括号里的值会传给构造方法。
- 打印出来的内容来自 `toString()`。

## 第 3 天：理解方法和封装

看这两个方法：

```java
public String getTitle() {
    return title;
}

public void setTitle(String title) {
    this.title = title;
}
```

`getTitle()` 用来读取标题。

`setTitle(...)` 用来修改标题。

字段本身是 `private`，外部不能直接写：

```java
task.title = "新标题";
```

外部应该通过方法修改：

```java
task.setTitle("新标题");
```

这就是封装：字段藏起来，提供方法作为入口。

### 今日练习

修改 `TaskDemo.java`：

```java
public class TaskDemo {
    public static void main(String[] args) {
        Task task = new Task(1, "学习 Java 对象", "完成 Task 类练习", null);

        task.setTitle("学习 Java 封装");
        task.setDescription("理解 private 和 get/set");
        task.setStatus(TaskStatus.DOING);

        System.out.println(task);
    }
}
```

### 结果要求

你应该看到标题、描述、状态都发生变化。

你能回答：

- `setTitle` 为什么没有返回值？
- `getTitle` 为什么有返回值？
- `private` 字段为什么不能直接从外部访问？

## 第 4 天：理解枚举

打开：

```text
console-practice/src/TaskStatus.java
```

内容是：

```java
public enum TaskStatus {
    TODO,
    DOING,
    DONE
}
```

枚举适合表达固定范围的值。

任务状态不应该随便写，比如：

```text
todo
Doing
完成了
快好了
```

这些写法太乱，不适合程序判断。所以我们用枚举限制状态只能是：

- `TODO`：待办
- `DOING`：进行中
- `DONE`：已完成

### 今日练习

在 `TaskDemo.java` 里分别试试：

```java
task.setStatus(TaskStatus.TODO);
task.setStatus(TaskStatus.DOING);
task.setStatus(TaskStatus.DONE);
```

每次修改后运行一次，观察打印结果。

### 结果要求

你能解释：

- 枚举能防止状态乱写。
- `TaskStatus.DOING` 表示 `TaskStatus` 枚举里的一个固定值。
- 后面做数据库和接口时，也会继续使用这些状态。

## 第 5 天：理解 `toString`

看 `Task.java` 里的：

```java
@Override
public String toString() {
    return "Task{" +
            "id=" + id +
            ", title='" + title + '\'' +
            ", description='" + description + '\'' +
            ", status=" + status +
            ", dueDate=" + dueDate +
            '}';
}
```

`toString()` 决定了对象被打印时显示什么。

如果没有它，打印对象时可能会看到类似：

```text
Task@3f99bd52
```

这对学习和调试都不友好。

### 今日练习

尝试修改 `toString()` 的显示格式，例如改成：

```java
return "任务编号：" + id +
        "，标题：" + title +
        "，状态：" + status +
        "，截止日期：" + dueDate;
```

然后运行：

```powershell
javac *.java
java TaskDemo
```

### 结果要求

控制台输出应该变成更接近中文说明的格式。

你能回答：

- `return` 后面的字符串是方法返回结果。
- `+` 可以拼接字符串。
- `toString()` 常用于调试和打印对象。

## 本周综合练习

创建 3 个任务对象并打印：

```java
public class TaskDemo {
    public static void main(String[] args) {
        Task task1 = new Task(1, "学习类和对象", "理解 class 和 object", null);
        Task task2 = new Task(2, "学习封装", "理解 private 和 get/set", null);
        Task task3 = new Task(3, "学习枚举", "理解 TODO/DOING/DONE", null);

        task2.setStatus(TaskStatus.DOING);
        task3.setStatus(TaskStatus.DONE);

        System.out.println(task1);
        System.out.println(task2);
        System.out.println(task3);
    }
}
```

## 本周验收清单

完成本周后，请确认你能做到：

- 能独立写出 `new Task(...)`。
- 能使用 `setTitle`、`setDescription`、`setStatus` 修改对象。
- 能说明 `Task.java` 中 5 个字段的含义。
- 能说明构造方法什么时候执行。
- 能说明枚举比普通字符串更适合表示状态。
- 能根据打印结果判断对象内部数据是否正确。

如果上面任何一项说不清楚，不要进入第 2 周，先回到对应小节重新练一次。
