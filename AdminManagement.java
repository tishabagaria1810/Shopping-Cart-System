package Modules.Users.AdminManagement;

import Database.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class AdminManagement
{
    static Scanner sc = new Scanner(System.in);

    public static void addProduct() throws Exception
    {
        System.out.print("📝 Enter Name of Product: ");
        String name = sc.nextLine();

        System.out.print("💬 Enter Description of Product: ");
        String description = sc.nextLine();

        System.out.print("💰 Enter Price of Product: ");
        double price = sc.nextDouble();

        System.out.print("📦 Enter Stock Quantity: ");
        int stockQuantity = sc.nextInt();

        System.out.println("Enter category id of product");
        int c_id = sc.nextInt();

        System.out.println("Enter subcategory id of product");
        int sub_id = sc.nextInt();

        String insert = "INSERT INTO product(product_name,category_id,subcategory_id ,description, price, stock) VALUES (?,?,?,?,?,?)";
        PreparedStatement ps = Database.getCon().prepareStatement(insert);
        try
        {
            ps.setString(1, name);
            ps.setInt(2, c_id);
            ps.setInt(3, sub_id);
            ps.setString(4, description);
            ps.setDouble(5, price);
            ps.setInt(6, stockQuantity);
            ps.executeUpdate();
            System.out.println("✅ Product Added Successfully!");
        }
        catch (Exception e)
        {
//            throw new RuntimeException(e);
        }
    }

    public static void updateProduct() throws Exception
    {
        int choice;
        do
        {
            System.out.println("\n--- ✏️ Update Product ---");
            System.out.println("💵  1 - Update Product Price");
            System.out.println("📦  2 - Update Stock Quantity");
            System.out.println("🚪  3 - Exit Update Menu");

            System.out.print("🧭 Enter your choice: ");
            choice = sc.nextInt();

            switch (choice)
            {
                case 1:
                    System.out.print("🔢 Enter Product ID: ");
                    int id = sc.nextInt();
                    System.out.print("💰 Enter New Price: ");
                    double price = sc.nextDouble();

                    String updatePrice = "UPDATE product SET price = ? WHERE product_id = ?";
                    try (PreparedStatement ps = Database.getCon().prepareStatement(updatePrice))
                    {
                        ps.setDouble(1, price);
                        ps.setInt(2, id);
                        ps.executeUpdate();
                        System.out.println("✅ Price Updated Successfully!");
                    }
                    break;

                case 2:
                    System.out.print("🔢 Enter Product ID: ");
                    int product_id = sc.nextInt();
                    System.out.print("📦 Enter New Stock Quantity: ");
                    int stock = sc.nextInt();

                    String updateStock = "UPDATE product SET stock_quantity = ? WHERE product_id = ?";
                    try (PreparedStatement ps = Database.getCon().prepareStatement(updateStock)) {
                        ps.setInt(1, stock);
                        ps.setInt(2, product_id);
                        ps.executeUpdate();
                        System.out.println("✅ Stock Quantity Updated Successfully!");
                    }
                    break;

                case 3:
                    System.out.println("👋 Returning to Admin Dashboard...");
                    break;

                default:
                    System.out.println("❌ Invalid choice. Please try again.");
            }
        } while (choice != 3);
    }

    public static void viewProducts() throws Exception {
        String select = "SELECT * FROM product";

        try (PreparedStatement ps = Database.getCon().prepareStatement(select);
             ResultSet rs = ps.executeQuery()) {

            if (rs == null) {
                System.out.println("⚠️ No product found.");
                return;
            }

            System.out.println("\n📋 Product List:");
            System.out.println("ID\t    Name\t\t  Description\t\t Price\t Stock");

            while (rs.next())
            {
                System.out.printf("%d\t%s\t\t%s\t\t%.2f\t%d\n",
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getInt("stock"));
            }
        }
    }

    public static void deleteProduct() throws Exception {
        System.out.print("❗ Enter Product ID to Delete: ");
        int product_id = sc.nextInt();

        String delete = "DELETE FROM product WHERE product_id = ?";
        try (PreparedStatement ps = Database.getCon().prepareStatement(delete)) {
            ps.setInt(1, product_id);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0)
                System.out.println("✅ Product Deleted Successfully!");
            else
                System.out.println("⚠️ No product found with that ID.");
        }
    }

    public static void main(String[] args) throws Exception
    {
        Scanner sc = new Scanner(System.in);
        int choice;

        while (true)
        {
            System.out.println("\n----------- 🛠️ Admin Dashboard -----------");
            System.out.println("📦  1 - Add New Product");
            System.out.println("✏️  2 - Update Existing Product");
            System.out.println("🔍  3 - View All Products");
            System.out.println("🗑️  4 - Delete a Product");
            System.out.println("🚪  5 - Exit Admin Panel");
            System.out.print("🔐 Enter your choice: ");

            choice = sc.nextInt();

            switch (choice)
            {
                case 1:
                    addProduct();
                    break;
                case 2:
                    updateProduct();
                    break;
                case 3:
                    viewProducts();
                    break;
                case 4:
                    deleteProduct();
                    break;
                case 5:
                    System.out.println("👋 Logging out of Admin Panel.");
                    return;
                default:
                    System.out.println("❌ Invalid choice. Please try again.");
            }
        }
    }
}