import java.sql.*;
import java.util.*;

class Recipe {
    String name;
    String ingredients;
    String steps;

    Recipe(String name, String ingredients, String steps) {
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    void display(int index) {
        System.out.println("\nRecipe #" + index);
        System.out.println("Name: " + name);
        System.out.println("Ingredients: " + ingredients);
        System.out.println("Steps: " + steps);
        System.out.println("------------------------");
    }
}

public class RecipeNest {

    static final String URL = "jdbc:mysql://localhost:3306/recipenest";
    static final String USER = "root";
    static final String PASSWORD = "shyamala@123";

    static Scanner sc = new Scanner(System.in);

    static Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n=== RecipeNest Menu ===");
            System.out.println("1. Add Recipe");
            System.out.println("2. View Recipes");
            System.out.println("3. Search Recipe");
            System.out.println("4. Delete Recipe");
            System.out.println("5. Exit");

            System.out.print("Enter choice: ");
            String input = sc.nextLine();

            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (Exception e) {
                System.out.println("Invalid input! Enter numbers only.");
                continue;
            }

            switch (choice) {
                case 1:
                    addRecipe();
                    break;
                case 2:
                    viewRecipes();
                    break;
                case 3:
                    searchRecipe();
                    break;
                case 4:
                    deleteRecipe();
                    break;
                case 5:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    static void addRecipe() {
        try {
            System.out.print("Enter recipe name: ");
            String name = sc.nextLine();

            System.out.print("Enter ingredients: ");
            String ingredients = sc.nextLine();

            System.out.print("Enter steps: ");
            String steps = sc.nextLine();

            Connection con = getConnection();
            String query = "INSERT INTO recipes (name, ingredients, steps) VALUES (?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(query);

            pst.setString(1, name);
            pst.setString(2, ingredients);
            pst.setString(3, steps);

            pst.executeUpdate();
            System.out.println("Recipe added successfully!");

            pst.close();
            con.close();
        } catch (Exception e) {
            System.out.println("Error adding recipe!");
            e.printStackTrace();
        }
    }

    static void viewRecipes() {
        try {
            Connection con = getConnection();
            String query = "SELECT * FROM recipes";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);

            int i = 1;
            boolean found = false;

            while (rs.next()) {
                found = true;
                Recipe r = new Recipe(
                        rs.getString("name"),
                        rs.getString("ingredients"),
                        rs.getString("steps")
                );
                r.display(i);
                i++;
            }

            if (!found) {
                System.out.println("No recipes available!");
            }

            rs.close();
            st.close();
            con.close();
        } catch (Exception e) {
            System.out.println("Error viewing recipes!");
            e.printStackTrace();
        }
    }

    static void searchRecipe() {
        try {
            System.out.print("Enter recipe name to search: ");
            String search = sc.nextLine();

            Connection con = getConnection();
            String query = "SELECT * FROM recipes WHERE name = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, search);

            ResultSet rs = pst.executeQuery();

            int i = 1;
            boolean found = false;

            while (rs.next()) {
                found = true;
                Recipe r = new Recipe(
                        rs.getString("name"),
                        rs.getString("ingredients"),
                        rs.getString("steps")
                );
                r.display(i);
                i++;
            }

            if (!found) {
                System.out.println("Recipe not found!");
            }

            rs.close();
            pst.close();
            con.close();
        } catch (Exception e) {
            System.out.println("Error searching recipe!");
            e.printStackTrace();
        }
    }

    static void deleteRecipe() {
        try {
            System.out.print("Enter recipe name to delete: ");
            String name = sc.nextLine();

            System.out.print("Are you sure? (yes/no): ");
            String confirm = sc.nextLine();

            if (!confirm.equalsIgnoreCase("yes")) {
                System.out.println("Delete cancelled.");
                return;
            }

            Connection con = getConnection();
            String query = "DELETE FROM recipes WHERE name = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, name);

            int rows = pst.executeUpdate();

            if (rows > 0) {
                System.out.println("Recipe deleted!");
            } else {
                System.out.println("Recipe not found!");
            }

            pst.close();
            con.close();
        } catch (Exception e) {
            System.out.println("Error deleting recipe!");
            e.printStackTrace();
        }
    }
}