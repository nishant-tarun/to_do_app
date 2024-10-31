import java.sql.*;
import java.util.Scanner;

public class TodoListApp {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/todo_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            boolean exit = false;
            while (!exit) {
                displayMenu();
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> addTask(conn);
                    case 2 -> viewTasksByDate(conn);
                    case 3 -> editTask(conn);
                    case 4 -> deleteTask(conn);
                    case 5 -> exit = true;
                    default -> System.out.println("Invalid option. Please try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Thank you for using the To-Do List application. Goodbye!");
    }

    private static void displayMenu() {
        System.out.println("\nTo-Do List Application");
        System.out.println("1. Add a task");
        System.out.println("2. View tasks by date");
        System.out.println("3. Edit a task");
        System.out.println("4. Delete a task");
        System.out.println("5. Exit");
        System.out.print("Choose an option: ");
    }

    private static void addTask(Connection conn) throws SQLException {
        System.out.print("Enter the task description: ");
        String description = scanner.nextLine();

        System.out.print("Enter due date (YYYY-MM-DD): ");
        String dueDate = scanner.nextLine();

        System.out.print("Enter due time (HH:MM:SS): ");
        String dueTime = scanner.nextLine();

        String sql = "INSERT INTO tasks (description, due_date, due_time) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, description);
            pstmt.setDate(2, Date.valueOf(dueDate));
            pstmt.setTime(3, Time.valueOf(dueTime));
            pstmt.executeUpdate();
            System.out.println("Task added successfully.");
        }
    }

    private static void viewTasksByDate(Connection conn) throws SQLException {
        System.out.print("Enter date to view tasks (YYYY-MM-DD): ");
        String date = scanner.nextLine();

        String sql = "SELECT * FROM tasks WHERE due_date = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(date));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    System.out.println("No tasks found for this date.");
                } else {
                    while (rs.next()) {
                        System.out.println("Task ID: " + rs.getInt("id"));
                        System.out.println("Description: " + rs.getString("description"));
                        System.out.println("Due Date: " + rs.getDate("due_date"));
                        System.out.println("Due Time: " + rs.getTime("due_time"));
                        System.out.println("Created At: " + rs.getTimestamp("created_at"));
                        System.out.println("-----");
                    }
                }
            }
        }
    }

    private static void editTask(Connection conn) throws SQLException {
        System.out.print("Enter the task ID to edit: ");
        int taskId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter new task description: ");
        String description = scanner.nextLine();

        System.out.print("Enter new due date (YYYY-MM-DD): ");
        String dueDate = scanner.nextLine();

        System.out.print("Enter new due time (HH:MM:SS): ");
        String dueTime = scanner.nextLine();

        String sql = "UPDATE tasks SET description = ?, due_date = ?, due_time = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, description);
            pstmt.setDate(2, Date.valueOf(dueDate));
            pstmt.setTime(3, Time.valueOf(dueTime));
            pstmt.setInt(4, taskId);
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Task updated successfully.");
            } else {
                System.out.println("Task ID not found.");
            }
        }
    }

    private static void deleteTask(Connection conn) throws SQLException {
        System.out.print("Enter the task ID to delete: ");
        int taskId = scanner.nextInt();
        scanner.nextLine();

        String sql = "DELETE FROM tasks WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, taskId);
            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Task deleted successfully.");
            } else {
                System.out.println("Task ID not found.");
            }
        }
    }
}
