import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/** A small terminal-only Eisenhower Matrix task prioritiser. */
public class Main {
    static final Scanner INPUT = new Scanner(System.in);
    static final List<Task> tasks = new ArrayList<>();
    static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    static class Task {
        String title;
        LocalDateTime dueDate;
        boolean important;
        boolean completed;

        Task(String title, LocalDateTime dueDate, boolean important) {
            this.title = title;
            this.dueDate = dueDate;
            this.important = important;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== PriorityPro: Eisenhower Matrix ===");
        while (true) {
            System.out.println("\n1. Add task\n2. View prioritised tasks\n3. Complete task\n4. Exit");
            System.out.print("Choose an option: ");
            String choice = INPUT.nextLine().trim();

            switch (choice) {
                case "1" -> addTask();
                case "2" -> showTasks();
                case "3" -> completeTask();
                case "4" -> { System.out.println("Goodbye. Stay focused!"); return; }
                default -> System.out.println("Please enter a number from 1 to 4.");
            }
        }
    }

    static void addTask() {
        System.out.print("Task title: ");
        String title = INPUT.nextLine().trim();
        if (title.isEmpty()) { System.out.println("A task needs a title."); return; }

        LocalDateTime dueDate;
        try {
            System.out.print("Due date (yyyy-MM-dd HH:mm): ");
            dueDate = LocalDateTime.parse(INPUT.nextLine().trim(), FORMAT);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format.");
            return;
        }

        System.out.print("Is it important? (yes/no): ");
        boolean important = INPUT.nextLine().trim().equalsIgnoreCase("yes");
        Task task = new Task(title, dueDate, important);
        tasks.add(task);
        System.out.println("Added to " + quadrant(task) + ".");
    }

    // A task is urgent when its deadline is within the next 24 hours, or overdue.
    static boolean isUrgent(Task task) {
        return !task.dueDate.isAfter(LocalDateTime.now().plusHours(24));
    }

    static String quadrant(Task task) {
        if (isUrgent(task) && task.important) return "Q1: DO FIRST";
        if (!isUrgent(task) && task.important) return "Q2: SCHEDULE";
        if (isUrgent(task)) return "Q3: HANDLE OR DELEGATE";
        return "Q4: DO LATER";
    }

    static int priority(Task task) {
        if (isUrgent(task) && task.important) return 1;
        if (!isUrgent(task) && task.important) return 2;
        if (isUrgent(task)) return 3;
        return 4;
    }

    static void showTasks() {
        if (tasks.isEmpty()) { System.out.println("No tasks added yet."); return; }
        List<Task> sorted = sortedTasks();
        for (int i = 0; i < sorted.size(); i++) {
            Task task = sorted.get(i);
            String state = task.completed ? "COMPLETED" : "PENDING";
            String overdue = !task.completed && task.dueDate.isBefore(LocalDateTime.now()) ? " [OVERDUE]" : "";
            System.out.printf("%d. %s | %s | due %s | %s%s%n", i + 1,
                    task.title, quadrant(task), task.dueDate.format(FORMAT), state, overdue);
        }
    }

    static List<Task> sortedTasks() {
        return tasks.stream()
                .sorted(Comparator.comparing(Main::priority).thenComparing(t -> t.dueDate))
                .toList();
    }

    static void completeTask() {
        if (tasks.isEmpty()) { System.out.println("No tasks to complete."); return; }
        showTasks();
        try {
            System.out.print("Enter task number: ");
            int number = Integer.parseInt(INPUT.nextLine().trim());
            Task task = sortedTasks().get(number - 1);
            task.completed = true;
            System.out.println("Completed: " + task.title);
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            System.out.println("That task number does not exist.");
        }
    }
}
