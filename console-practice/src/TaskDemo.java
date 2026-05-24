public class TaskDemo {
    public static void main(String[] args){
//        构造方法在new的时候执行
        Task task = new Task(1,"学习JAVA对象","简介",null);
        Task task1 = new Task(2,"学习封装","理解 private 和 get/set",null);
        Task task2 = new Task(3,"学习枚举","理解TaskStatus",null);
//        使用枚举可以规定状态的固定值，而不是随意乱写
        task1.setStatus(TaskStatus.DOING);
        task2.setStatus(TaskStatus.DONE);
        task.setStatus(TaskStatus.TODO);
        task.setTitle("学习 Java 封装");
        task.setDescription("理解 private 和 get/set");
        task.setStatus(TaskStatus.DOING);
        System.out.println(task);
        System.out.println(task1);
        System.out.println(task2);
    }
//
}