# 第 2 周：集合与控制台任务管理器

这一周开始把多个 `Task` 对象组织起来，做一个真正能操作的控制台小项目。你会第一次接触“项目逻辑”：菜单、输入、集合、查找、修改、删除。

现在的数据还不会保存到数据库，而是临时保存在内存中的 `ArrayList` 里。这很重要，因为真实后端项目也是先理解业务逻辑，再把数据存储换成数据库。

## 本周学习成果

完成本周后，你应该能够做到：

- 理解 `ArrayList<Task>` 为什么能保存多个任务。
- 看懂控制台菜单如何循环运行。
- 看懂新增、查看、修改、删除任务的代码。
- 独立新增一个“按状态筛选任务”的菜单功能。
- 理解“内存数据”和“数据库数据”的区别。

## 第 1 天：运行控制台项目

进入源码目录：

```powershell
cd E:\task-manager-api\console-practice\src
```

编译：

```powershell
javac *.java
```

运行：

```powershell
java TaskManagerConsoleApp
```

你应该看到菜单：

```text
=== 控制台任务管理器 ===
1. 新增任务
2. 查看任务列表
3. 修改任务状态
4. 删除任务
0. 退出
请选择：
```

### 今日练习

按顺序操作：

1. 输入 `1` 新增一个任务。
2. 输入标题：`学习 ArrayList`
3. 输入描述：`理解多个任务如何保存`
4. 截止日期可以留空，直接回车。
5. 输入 `2` 查看任务列表。
6. 输入 `0` 退出。

### 结果要求

你应该看到新增的任务被打印出来。

你要理解：

- 程序运行期间，任务保存在 `tasks` 集合里。
- 程序退出后，任务会消失。
- 这是因为还没有使用数据库或文件保存。

## 第 2 天：理解菜单循环

打开：

```text
console-practice/src/TaskManagerConsoleApp.java
```

重点看：

```java
private void run() {
    boolean running = true;
    while (running) {
        printMenu();
        String choice = scanner.nextLine();
        switch (choice) {
            case "1" -> createTask();
            case "2" -> listTasks();
            case "3" -> updateTaskStatus();
            case "4" -> deleteTask();
            case "0" -> running = false;
            default -> System.out.println("请输入菜单中的数字。");
        }
    }
    System.out.println("已退出任务管理器。");
}
```

这段代码的运行顺序：

1. `running` 设置为 `true`。
2. 进入 `while` 循环。
3. 打印菜单。
4. 读取用户输入。
5. 根据输入调用不同方法。
6. 如果输入不是 `0`，继续下一轮循环。
7. 如果输入 `0`，把 `running` 改为 `false`，循环结束。

### 今日练习

在 `printMenu()` 里增加一行：

```java
System.out.println("5. 按状态筛选任务");
```

此时先不要写功能，只看菜单是否多了一行。

### 结果要求

运行后菜单里应该出现：

```text
5. 按状态筛选任务
```

你能回答：

- `while (running)` 为什么会重复执行？
- `scanner.nextLine()` 读取的是什么？
- `switch` 为什么适合处理菜单？

## 第 3 天：理解新增任务

重点看：

```java
private void createTask() {
    System.out.print("任务标题：");
    String title = scanner.nextLine();
    System.out.print("任务描述：");
    String description = scanner.nextLine();
    System.out.print("截止日期（例如 2026-06-01，可留空）：");
    String dueDateText = scanner.nextLine();

    LocalDate dueDate = dueDateText.isBlank() ? null : LocalDate.parse(dueDateText);
    Task task = new Task(nextId++, title, description, dueDate);
    tasks.add(task);
    System.out.println("新增成功：" + task);
}
```

这里要抓住 3 件事：

- 从控制台读取字符串。
- 用读取到的数据创建 `Task` 对象。
- 把对象加入 `tasks` 集合。

`nextId++` 的意思是：先使用当前 ID，然后自动加 1。

### 今日练习

新增两个任务：

```text
任务 1：学习 Java 方法
任务 2：学习 Java 集合
```

然后查看任务列表。

### 结果要求

两个任务的 ID 应该分别是 `1` 和 `2`。

你能回答：

- 为什么第二个任务 ID 会自动变成 2？
- `tasks.add(task)` 的作用是什么？
- `LocalDate.parse(...)` 为什么要求日期格式是 `2026-06-01` 这种形式？

## 第 4 天：理解查看任务和集合遍历

重点看：

```java
private void listTasks() {
    if (tasks.isEmpty()) {
        System.out.println("暂无任务。");
        return;
    }
    for (Task task : tasks) {
        System.out.println(task);
    }
}
```

这段代码分两步：

1. 如果集合为空，提示暂无任务并结束方法。
2. 如果集合不为空，用 `for` 循环逐个打印任务。

`return` 在这里的作用是提前结束方法。

### 今日练习

先不要新增任务，直接选择 `2` 查看任务。

然后新增一个任务，再选择 `2` 查看任务。

### 结果要求

第一次应该显示：

```text
暂无任务。
```

第二次应该显示任务详情。

你能回答：

- `tasks.isEmpty()` 判断的是什么？
- `for (Task task : tasks)` 每次循环拿到的是什么？
- 为什么空列表时要提前 `return`？

## 第 5 天：理解按 ID 查找任务

修改和删除都需要先找到任务，所以项目里抽出了这个方法：

```java
private Task findTaskByInputId() {
    System.out.print("请输入任务 ID：");
    int id;
    try {
        id = Integer.parseInt(scanner.nextLine());
    } catch (NumberFormatException exception) {
        System.out.println("ID 必须是数字。");
        return null;
    }

    for (Task task : tasks) {
        if (task.getId() == id) {
            return task;
        }
    }
    System.out.println("没有找到这个任务。");
    return null;
}
```

这段代码非常重要，因为真实项目里经常要“根据 ID 查询一条数据”。

现在是在 `ArrayList` 里找，以后会去数据库里找。

### 今日练习

运行程序，测试三种输入：

1. 输入存在的 ID，例如 `1`。
2. 输入不存在的 ID，例如 `999`。
3. 输入不是数字的内容，例如 `abc`。

### 结果要求

你应该分别看到：

- 找到任务并继续修改或删除。
- 提示没有找到这个任务。
- 提示 ID 必须是数字。

你能回答：

- `Integer.parseInt(...)` 的作用是什么？
- 为什么需要 `try/catch`？
- 找不到任务时为什么返回 `null`？

## 第 6 天：新增按状态筛选功能

现在完成一个真正的小需求：菜单 `5` 按状态筛选任务。

### 第一步：补菜单

在 `printMenu()` 中加入：

```java
System.out.println("5. 按状态筛选任务");
```

### 第二步：补分支

在 `switch` 中加入：

```java
case "5" -> listTasksByStatus();
```

### 第三步：新增方法

在类里新增：

```java
private void listTasksByStatus() {
    System.out.print("请输入状态（TODO/DOING/DONE）：");
    String statusText = scanner.nextLine();

    try {
        TaskStatus status = TaskStatus.valueOf(statusText);
        boolean found = false;

        for (Task task : tasks) {
            if (task.getStatus() == status) {
                System.out.println(task);
                found = true;
            }
        }

        if (!found) {
            System.out.println("没有这个状态的任务。");
        }
    } catch (IllegalArgumentException exception) {
        System.out.println("状态只能是 TODO、DOING 或 DONE。");
    }
}
```

### 结果要求

你要测试：

1. 新增 3 个任务。
2. 把其中一个改成 `DOING`。
3. 把另一个改成 `DONE`。
4. 输入 `5`，分别查询 `TODO`、`DOING`、`DONE`。
5. 输入错误状态，例如 `doing`，观察提示。

你能回答：

- `TaskStatus.valueOf(statusText)` 做了什么？
- `found` 变量为什么需要？
- `task.getStatus() == status` 为什么可以判断状态相等？

## 第 7 天：本周复盘

请画出这个程序的数据流：

```text
用户输入菜单
    ↓
switch 判断选择
    ↓
调用 create/list/update/delete 方法
    ↓
操作 tasks 集合
    ↓
打印结果到控制台
```

## 本周综合练习

在不看答案的情况下，自己完成两个增强功能：

### 练习 1：修改任务标题

新增菜单：

```text
6. 修改任务标题
```

要求：

- 输入任务 ID。
- 如果任务存在，输入新标题。
- 调用 `task.setTitle(newTitle)` 修改。
- 打印修改后的任务。

### 练习 2：统计任务数量

新增菜单：

```text
7. 查看任务统计
```

要求输出：

```text
总任务数：3
TODO：1
DOING：1
DONE：1
```

提示：可以用三个 `int` 变量分别计数。

## 本周验收清单

完成本周后，请确认你能做到：

- 能运行控制台项目。
- 能解释 `List<Task> tasks = new ArrayList<>()` 的意思。
- 能看懂 `while`、`switch`、`for` 在项目里的用法。
- 能独立新增一个菜单项。
- 能独立写一个遍历任务列表的方法。
- 能处理用户输入错误，例如 ID 不是数字、状态写错。
- 能说清楚现在数据保存在内存里，不是数据库里。

这周结束后，你已经具备进入 SQL 的准备了：你知道程序里有任务数据，下一步就是学习如何把这些数据永久保存。
