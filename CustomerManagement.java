package Modules.Users.CustomerManagement;

import Database.Database;
import Modules.Address.Address;
import Modules.Users.User;
import Modules.Users.CustomerManagement.CustomerManagement;
import java.io.*;
import java.sql.*;
import java.util.Date;
import java.util.*;

import static Modules.Users.User.getUserById;
import static Modules.Users.User.loggedInUser;
//import static Modules.Users.User.user_id;

public class CustomerManagement
{
    static User user;
    static Scanner sc = new Scanner(System.in);

    public static void addAddress() throws Exception
    {
        System.out.println("Enter Modules.Address In formatted way ");
        System.out.println("Enter Address Line 1 : ");
        String addressLine1 = sc.nextLine();
        sc.nextLine();
        System.out.println("Enter Address Line 2 : ");
        String addressLine2 = sc.nextLine();
        System.out.println("Enter Area : ");
        String area = sc.nextLine();
        System.out.println("Enter City : ");
        String city = sc.nextLine();
        System.out.println("Enter State : ");
        String state = sc.nextLine();
        System.out.println("Enter Pin code : ");
        int pinCode = sc.nextInt();

        Address add = new Address(User.getCurrentUser().getFirstName(), addressLine1, addressLine2, area, city, state, pinCode, user);
    }

    private static void payment() throws Exception {
        System.out.println("1.Cash On Delivery\n2.Pay Online\n\nSelect Mode of Payment : ");
        int payMode = sc.nextInt();
        sc.nextLine(); // clear the newline character after nextInt()

        switch (payMode) {
            case 1:
                System.out.println("✅ You have selected Cash On Delivery.");
                String fetchCart = "SELECT p.product_id, p.product_name, c.quantity, c.price " +
                        "FROM cart c JOIN product p ON c.product_id = p.product_id where user_id = ?";

                try (PreparedStatement fetchCartItems = Database.getCon().prepareStatement(
                        fetchCart, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                    fetchCartItems.setInt(1,User.getCurrentUser().getUserId());
                    ResultSet rs = fetchCartItems.executeQuery();


                    boolean hasItems = false;
                    double  total = 0;

                    // First loop to display cart items
                    while (rs.next()) {
                        hasItems = true;
                        int quantity = rs.getInt("quantity");
                        double price = rs.getDouble("price");
                        double itemTotal = price * quantity;
                        total += itemTotal;

                    }
                    System.out.println("Total Bill-Amount - ₹"+total);
                    System.out.println("📦 Your order is placed successfully and will shipped soon!");
                }

                break;

            case 2:
                System.out.println("💳 You have selected Pay Online.");
                System.out.println("🔐 Redirecting to payment gateway...");
                Thread.sleep(1000);
                System.out.println("Enter your UPI ID");
                String UPI_ID = sc.next();
                System.out.println("Enter your PIN");
                int PIN = sc.nextInt();
                String fetchCart1 = "SELECT p.product_id, p.product_name, o.quantity, o.price " +
                        "FROM orders o JOIN product p ON o.product_id = p.product_id where user_id = ?" ;

                try (PreparedStatement fetchCartItems = Database.getCon().prepareStatement(
                        fetchCart1, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                    fetchCartItems.setInt(1,User.getCurrentUser().getUserId());
                    ResultSet rs = fetchCartItems.executeQuery();
                    boolean hasItems = false;
                    double total = 0;

                    // First loop to display cart items
                    while (rs.next()) {
                        hasItems = true;
                        int quantity = rs.getInt("quantity");
                        double price = rs.getDouble("price");
                        double itemTotal = price * quantity;
                        total += itemTotal;

                    }
                    System.out.println("Total Bill-Amount - ₹" + total);
                    Thread.sleep(1000);
                    System.out.println("📦 Your order is placed successfully and will shipped soon!");
                }


                break;

            default:
                System.out.println("❌ Invalid payment mode selected. Please choose 1 or 2.");
                // You may loop back or prompt again
                break;
        }
    }

    private static void searchProduct() throws Exception {
        String showproduct = "select product_id,category_id,subcategory_id,product_name,description,price from product";
        PreparedStatement viewProduct = Database.getCon().prepareStatement(showproduct);
        ResultSet rs5 = viewProduct.executeQuery();
        System.out.println("product_id  category_id  subcategory_id  product_name  description  price ");
        while(rs5.next())
        {
            System.out.println(rs5.getInt(1)+"\t"+rs5.getInt(2)+"\t"+rs5.getInt(3)+"\t"+rs5.getString(4)+"\t"+rs5.getString(5)+"\t"+rs5.getDouble(6));

        }

        String productName ;
        do {
            System.out.print("Enter product Name : ");
            productName = sc.next().toLowerCase();
            if (isValidproductName(productName)) {
                System.out.println("Valid product Name ✅");
                break;
            } else {
                System.out.println("Invalid product Name ❌ (only letters allowed)");
            }

        }
        while (true);
        // String productName = sc.nextLine().toLowerCase();
        //int customerId = 1;
        int typeScrollInsensitive = ResultSet.TYPE_SCROLL_INSENSITIVE;
        String fetchProduct = "SELECT product_name , description , price FROM Product WHERE product_name = ?";
        try (PreparedStatement insertStmt = Database.getCon().prepareStatement(fetchProduct,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY)) {
            insertStmt.setString(1, productName);
            ResultSet rs = insertStmt.executeQuery();

            boolean found = false;
            while (rs.next()) {
                if(rs.getString("product_name").equalsIgnoreCase(productName)) {
                    found = true;
                    String name = rs.getString("product_name");
                    String description = rs.getString("description");
                    double price = rs.getDouble("price");
                    System.out.println();
                    System.out.println("Product Name : " + name);
                    System.out.println("Description  : " + description);
                    System.out.println("Price        : ₹" + price);
                    System.out.println("----------------------------------");
                }

            }
            if(found)
            {
                System.out.println("Do you want to Add item to Cart(yes/no)");
                String ans = sc.next().toLowerCase();
                if (ans.equals("yes")) {
//                System.out.println("Enter subcategory_id of product ");
//                int sub_id = sc.nextInt();
                    String select = "select * from product ";
                    PreparedStatement ps = Database.getCon().prepareStatement(select, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    //ps.setInt(1,1);
                    //ps.setInt(2,101);
                    ResultSet rs1 = ps.executeQuery();
                    //rs.beforeFirst();
                    int quantity = 0;
                    boolean found1 = false;
                    boolean sufficientstock = false;
                    while (rs1.next()) {

                        if(rs1.getString("product_name").equalsIgnoreCase(productName))
                        {
                            System.out.println("founded");
                            System.out.println("Enter Quantity to be added to Cart ");
                            quantity = sc.nextInt();
                            if(rs1.getInt(7)>quantity)
                            {
                                sufficientstock = true;
                            }
                            found1 = true;
                            break;
                        }
                    }
                    if (found1 && sufficientstock) {
                        String insert = "insert into cart(product_id,product_name,description,price,quantity,user_id) values(?,?,?,?,?,?)";
                        PreparedStatement ps1 = Database.getCon().prepareStatement(insert);
                        ps1.setInt(1, rs1.getInt(1));
                        ps1.setString(2, rs1.getString(4));
                        ps1.setString(3, rs1.getString(5));
                        ps1.setDouble(4, rs1.getDouble(6));
                        ps1.setInt(5, quantity);
                        ps1.setInt(6, User.getCurrentUser().getUserId());
                        ps1.executeUpdate();
                    } else {
                        System.out.println("not founded");
                    }
                }            }
            else {
                System.out.println("no product found");

            }
        }
    }

    private static boolean isValidproductName(String productName) {
        return productName.matches("[a-zA-Z]+");
    }

    private static void viewCategories() throws Exception {
        int choice = 0;
        int customerId = 1;
        int typeScrollInsensitive = ResultSet.TYPE_SCROLL_INSENSITIVE;
        Connection con = Database.getCon();
        do {
            System.out.println("1 - Grocery");
            System.out.println("2 - Electronics");
            System.out.println("3 - Personal Care");
            System.out.println("4 - Beverages");
            System.out.println("5 - Home & Kitchen Appliances");
            System.out.println("6 - Stationery Items");
            System.out.println("7 - Fashion");
            System.out.println("8 - Cleaning Supplies");
            System.out.println("9 - Exit");
            System.out.println("Enter your choice - ");
            choice = sc.nextInt();
            switch (choice)
            {
                case 1:
                    int choice1 = 0;
                    do {
                        System.out.println("1 - Fruits");
                        System.out.println("2 - Vegetables");
                        System.out.println("3 - Snacks");
                        System.out.println("4 - Exit");
                        System.out.println("Enter your choice");
                        choice1 = sc.nextInt();
//                        String usersid = "select user_id from users";
//                        PreparedStatement ps5 = Database.getCon().prepareStatement(usersid);
//                        ResultSet rs5 = ps5.executeQuery();
                        switch (choice1)
                        {
                            case 1:
                                String select = "select * from product where category_id = ? and subcategory_id = ? ";
                                PreparedStatement ps = con.prepareStatement(select,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
                                ps.setInt(1,1);
                                ps.setInt(2,101);
                                //ps.setInt(2,101);
                                ResultSet rs = ps.executeQuery();
                                while(rs.next())
                                {
                                    System.out.println(rs.getInt(1)+"\t"+rs.getInt(2)+"\t"+rs.getInt(3)+"\t"+rs.getString(4)+"\t"+rs.getString(5)+"\t"+rs.getDouble(6)+"\t"+rs.getInt(7));
                                }
                                System.out.println("Do you want to Add any item to Cart(yes/no)");
                                String ans = sc.next().toLowerCase();
                                if(ans.equals("yes"))
                                {
                                    System.out.println("Enter p_id of product ");
                                    int p_id = sc.nextInt();

                                    rs.beforeFirst();
                                    int quantity = 0 ;
                                    boolean found = false;
                                    boolean sufficientstock = false;
                                    while (rs.next())
                                    {

                                        if(rs.getInt(1)==p_id)
                                        {
                                            System.out.println("founded");
                                            System.out.println("Enter Quantity to be added to Cart ");
                                            quantity = sc.nextInt();
                                            if(rs.getInt(7)>quantity)
                                            {
                                                sufficientstock = true;
                                            }
                                            found = true;
                                            break;
                                        }
                                    }
                                    if(found && sufficientstock)
                                    {
                                        String insert = "insert into cart(product_id,product_name,description,price,quantity,user_id) values(?,?,?,?,?,?)";
                                        PreparedStatement ps1 = con.prepareStatement(insert);
                                        ps1.setInt(1,rs.getInt(1));
                                        ps1.setString(2, rs.getString(4));
                                        ps1.setString(3, rs.getString(5));
                                        ps1.setDouble(4,rs.getDouble(6));
                                        ps1.setInt(5,quantity);
                                        ps1.setInt(6,User.getCurrentUser().getUserId());
                                        ps1.executeUpdate();
                                    }
                                    else
                                    {
                                        System.out.println("not founded");
                                    }
                                }
//
//
//

                                break;
                            case 2:String select1 = "select * from product where category_id = ? and subcategory_id = ? ";
                                PreparedStatement ps1 = con.prepareStatement(select1,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
                                ps1.setInt(1,1);
                                ps1.setInt(2,102);
                                //  ps1.setInt(2,101);
                                ResultSet rs1 = ps1.executeQuery();
                                while(rs1.next())
                                {
                                    System.out.println(rs1.getInt(1)+"\t"+rs1.getInt(2)+"\t"+rs1.getInt(3)+"\t"+rs1.getString(4)+"\t"+rs1.getString(5)+"\t"+rs1.getDouble(6)+"\t"+rs1.getInt(7));
                                }
                                System.out.println("Do you want to Add any item to Cart(yes/no)");
                                String ans1 = sc.next().toLowerCase();
                                if(ans1.equals("yes"))
                                {
                                    System.out.println("Enter product_id of product ");
                                    int p_id = sc.nextInt();

                                    rs1.beforeFirst();
                                    int quantity = 0 ;
                                    boolean found = false;
                                    boolean sufficientstock = false;
                                    while (rs1.next())
                                    {

                                        if(rs1.getInt(1)==p_id)
                                        {
                                            System.out.println("founded");
                                            System.out.println("Enter Quantity to be added to Cart ");
                                            quantity = sc.nextInt();
                                            if(rs1.getInt(7)>quantity)
                                            {
                                                sufficientstock = true;
                                            }
                                            found = true;
                                            break;
                                        }
                                    }
                                    if(found && sufficientstock)
                                    {
                                        String insert = "insert into cart(product_id,product_name,description,price,quantity,user_id) values(?,?,?,?,?,?)";                                        PreparedStatement ps2 = con.prepareStatement(insert);
                                        ps2.setInt(1,rs1.getInt(1));
                                        ps2.setString(2, rs1.getString(4));
                                        ps2.setString(3, rs1.getString(5));
                                        ps2.setDouble(4,rs1.getDouble(6));
                                        ps2.setInt(5,quantity);
                                        ps2.setInt(6,User.getCurrentUser().getUserId());
                                        ps2.executeUpdate();
                                    }
                                    else
                                    {
                                        System.out.println("not founded");
                                    }
                                }break;
                            case 3:String select2 = "select * from product where category_id = ? and subcategory_id = ?";
                                PreparedStatement ps2 = con.prepareStatement(select2,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
                                ps2.setInt(1,1);
                                ps2.setInt(2,103);
                                //ps2.setInt(2,101);
                                ResultSet rs2 = ps2.executeQuery();
                                while(rs2.next())
                                {
                                    System.out.println(rs2.getInt(1)+"\t"+rs2.getInt(2)+"\t"+rs2.getInt(3)+"\t"+rs2.getString(4)+"\t"+rs2.getString(5)+"\t"+rs2.getDouble(6)+"\t"+rs2.getInt(7));
                                }System.out.println("Do you want to Add any item to Cart(yes/no)");
                                String ans2 = sc.next().toLowerCase();
                                if(ans2.equals("yes"))
                                {
                                    System.out.println("Enter product_id of product ");
                                    int sub_id = sc.nextInt();

                                    rs2.beforeFirst();
                                    int quantity = 0 ;
                                    boolean found = false;
                                    boolean sufficientstock = false;
                                    while (rs2.next())
                                    {

                                        if(rs2.getInt(1)==sub_id)
                                        {
                                            System.out.println("founded");
                                            System.out.println("Enter Quantity to be added to Cart ");
                                            quantity = sc.nextInt();
                                            if(rs2.getInt(7)>quantity)
                                            {
                                                sufficientstock = true;
                                            }
                                            found = true;
                                            break;
                                        }
                                    }
                                    if(found && sufficientstock)
                                    {
                                        String insert = "insert into cart(product_id,product_name,description,price,quantity,user_id) values(?,?,?,?,?,?)";
                                        PreparedStatement ps3 = con.prepareStatement(insert);
                                        ps3.setInt(1,rs2.getInt(1));
                                        ps3.setString(2, rs2.getString(4));
                                        ps3.setString(3, rs2.getString(5));
                                        ps3.setDouble(4,rs2.getDouble(6));
                                        ps3.setInt(5,quantity);
                                        ps3.setInt(6,User.getCurrentUser().getUserId());
                                        ps3.executeUpdate();
                                    }
                                    else
                                    {
                                        System.out.println("not founded");
                                    }
                                }break;
                            case 4:
                                System.out.println("Exiting");break;
                            default:
                                System.out.println("Invalid Choice");
                                break;
                        }
                    }
                    while (choice1!=4);
                    break;

                case 2:
                    int choice2 = 0;
                    do {
                        System.out.println("1 - Mobiles Phones");
                        System.out.println("2 - Laptops");
                        System.out.println("3 - Phone and Laptop Accessories");
                        System.out.println("4 - Exit");
                        System.out.println("Enter your choice - ");
                        choice2 = sc.nextInt();
                        switch (choice2)
                        {
                            case 1: String select = "select * from product where category_id = ? and subcategory_id = ?  ";
                                PreparedStatement ps = con.prepareStatement(select,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
                                ps.setInt(1,2);
                                ps.setInt(2,201);
                                // ps.setInt(2,101);
                                ResultSet rs = ps.executeQuery();
                                while(rs.next())
                                {
                                    System.out.println(rs.getInt(1)+"\t"+rs.getInt(2)+"\t"+rs.getInt(3)+"\t"+rs.getString(4)+"\t"+rs.getString(5)+"\t"+rs.getDouble(6)+"\t"+rs.getInt(7));
                                }
                                System.out.println("Do you want to Add any item to Cart(yes/no)");
                                String ans = sc.next().toLowerCase();
                                if(ans.equals("yes"))
                                {
                                    System.out.println("Enter product_id of product ");
                                    int p_id = sc.nextInt();

                                    rs.beforeFirst();
                                    int quantity = 0 ;
                                    boolean found = false;
                                    boolean sufficientstock = false;
                                    while (rs.next())
                                    {

                                        if(rs.getInt(1)==p_id)
                                        {
                                            System.out.println("founded");
                                            System.out.println("Enter Quantity to be added to Cart ");
                                            quantity = sc.nextInt();
                                            if(rs.getInt(7)>quantity)
                                            {
                                                sufficientstock = true;
                                            }
                                            found = true;
                                            break;
                                        }
                                    }
                                    if(found && sufficientstock)
                                    {
                                        String insert = "insert into cart(product_id,product_name,description,price,quantity,user_id) values(?,?,?,?,?,?)";
                                        PreparedStatement ps1 = con.prepareStatement(insert);
                                        ps1.setInt(1,rs.getInt(1));
                                        ps1.setString(2, rs.getString(4));
                                        ps1.setString(3, rs.getString(5));
                                        ps1.setDouble(4,rs.getDouble(6));
                                        ps1.setInt(5,quantity);
                                        ps1.setInt(6,User.getCurrentUser().getUserId());
                                        ps1.executeUpdate();
                                    }
                                    else
                                    {
                                        System.out.println("not founded");
                                    }
                                }break;
                            case 2:String select1 = "select * from product where category_id = ? and subcategory_id = ?  ";
                                PreparedStatement ps1 = con.prepareStatement(select1,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
                                ps1.setInt(1,2);
                                ps1.setInt(2,202);
                                //ps1.setInt(2,101);
                                ResultSet rs1 = ps1.executeQuery();
                                while(rs1.next())
                                {
                                    System.out.println(rs1.getInt(1)+"\t"+rs1.getInt(2)+"\t"+rs1.getInt(3)+"\t"+rs1.getString(4)+"\t"+rs1.getString(5)+"\t"+rs1.getDouble(6)+"\t"+rs1.getInt(7));
                                }
                                System.out.println("Do you want to Add any item to Cart(yes/no)");
                                String ans1 = sc.next().toLowerCase();
                                if(ans1.equals("yes"))
                                {
                                    System.out.println("Enter product_id of product ");
                                    int p_id = sc.nextInt();

                                    rs1.beforeFirst();
                                    int quantity = 0 ;
                                    boolean found = false;
                                    boolean sufficientstock = false;
                                    while (rs1.next())
                                    {

                                        if(rs1.getInt(1)==p_id)
                                        {
                                            System.out.println("founded");
                                            System.out.println("Enter Quantity to be added to Cart ");
                                            quantity = sc.nextInt();
                                            if(rs1.getInt(7)>quantity)
                                            {
                                                sufficientstock = true;
                                            }
                                            found = true;
                                            break;
                                        }
                                    }
                                    if(found && sufficientstock)
                                    {
                                        String insert = "insert into cart(product_id,product_name,description,price,quantity,user_id) values(?,?,?,?,?,?)";
                                        PreparedStatement ps2 = con.prepareStatement(insert);
                                        ps2.setInt(1,rs1.getInt(1));
                                        ps2.setString(2, rs1.getString(4));
                                        ps2.setString(3, rs1.getString(5));
                                        ps2.setDouble(4,rs1.getDouble(6));
                                        ps2.setInt(5,quantity);
                                        ps2.setInt(6,User.getCurrentUser().getUserId());
                                        ps2.executeUpdate();
                                    }
                                    else
                                    {
                                        System.out.println("not founded");
                                    }
                                }break;
                            case 3:String select2 = "select * from product where category_id = ?  and subcategory_id = ?";
                                PreparedStatement ps2 = con.prepareStatement(select2,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
                                ps2.setInt(1,2);
                                ps2.setInt(2,203);
                                //ps2.setInt(2,101);
                                ResultSet rs2 = ps2.executeQuery();
                                while(rs2.next())
                                {
                                    System.out.println(rs2.getInt(1)+"\t"+rs2.getInt(2)+"\t"+rs2.getInt(3)+"\t"+rs2.getString(4)+"\t"+rs2.getString(5)+"\t"+rs2.getDouble(6)+"\t"+rs2.getInt(7));
                                }break;
                            case 4:
                                System.out.println("Exiting");break;
                            default:
                                System.out.println("Invalid Choice");
                                break;
                        }
                    }
                    while (choice2!=4);
                    break;


                case 3:
                    int choice3 = 0;
                    do {
                        System.out.println("1 - Skincare");
                        System.out.println("2 - Haircare");
                        System.out.println("3 - Exit");
                        choice3 = sc.nextInt();
                        switch (choice3)
                        {
                            case 1: String select = "select * from product where category_id = ? and subcategory_id = ? ";
                                PreparedStatement ps = con.prepareStatement(select,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
                                ps.setInt(1,3);
                                ps.setInt(2,301);
                                // ps.setInt(2,101);
                                ResultSet rs = ps.executeQuery();
                                while(rs.next())
                                {
                                    System.out.println(rs.getInt(1)+"\t"+rs.getInt(2)+"\t"+rs.getInt(3)+"\t"+rs.getString(4)+"\t"+rs.getString(5)+"\t"+rs.getDouble(6)+"\t"+rs.getInt(7));
                                }
                                System.out.println("Do you want to Add any item to Cart(yes/no)");
                                String ans = sc.next().toLowerCase();
                                if(ans.equals("yes"))
                                {
                                    System.out.println("Enter product_id of product ");
                                    int p_id = sc.nextInt();

                                    rs.beforeFirst();
                                    int quantity = 0 ;
                                    boolean found = false;
                                    boolean sufficientstock = false;
                                    while (rs.next())
                                    {

                                        if(rs.getInt(1)==p_id)
                                        {
                                            System.out.println("founded");
                                            System.out.println("Enter Quantity to be added to Cart ");
                                            quantity = sc.nextInt();
                                            if(rs.getInt(7)>quantity)
                                            {
                                                sufficientstock = true;
                                            }
                                            found = true;
                                            break;
                                        }
                                    }
                                    if(found && sufficientstock)
                                    {
                                        String insert = "insert into cart(product_id,product_name,description,price,quantity,user_id) values(?,?,?,?,?,?)";                                        PreparedStatement ps1 = con.prepareStatement(insert);
                                        ps1.setInt(1,rs.getInt(1));
                                        ps1.setString(2, rs.getString(4));
                                        ps1.setString(3, rs.getString(5));
                                        ps1.setDouble(4,rs.getDouble(6));
                                        ps1.setInt(5,quantity);
                                        ps1.setInt(6,User.getCurrentUser().getUserId());
                                        ps1.executeUpdate();
                                    }
                                    else
                                    {
                                        System.out.println("not founded");
                                    }
                                }break;
                            case 2: String select1 = "select * from product where category_id = ? and subcategory_id = ? ";
                                PreparedStatement ps1 = con.prepareStatement(select1,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
                                ps1.setInt(1,3);
                                ps1.setInt(2,302);
                                //  ps1.setInt(2,101);
                                ResultSet rs1 = ps1.executeQuery();
                                while(rs1.next())
                                {
                                    System.out.println(rs1.getInt(1)+"\t"+rs1.getInt(2)+"\t"+rs1.getInt(3)+"\t"+rs1.getString(4)+"\t"+rs1.getString(5)+"\t"+rs1.getDouble(6)+"\t"+rs1.getInt(7));
                                }
                                System.out.println("Do you want to Add any item to Cart(yes/no)");
                                String ans1 = sc.next().toLowerCase();
                                if(ans1.equals("yes"))
                                {
                                    System.out.println("Enter product_id of product ");
                                    int p_id = sc.nextInt();

                                    rs1.beforeFirst();
                                    int quantity = 0 ;
                                    boolean found = false;
                                    boolean sufficientstock = false;
                                    while (rs1.next())
                                    {

                                        if(rs1.getInt(1)==p_id)
                                        {
                                            System.out.println("founded");
                                            System.out.println("Enter Quantity to be added to Cart ");
                                            quantity = sc.nextInt();
                                            if(rs1.getInt(7)>quantity)
                                            {
                                                sufficientstock = true;
                                            }
                                            found = true;
                                            break;
                                        }
                                    }
                                    if(found && sufficientstock)
                                    {
                                        String insert = "insert into cart(product_id,product_name,description,price,quantity,user_id) values(?,?,?,?,?,?)";                                        PreparedStatement ps2 = con.prepareStatement(insert);
                                        ps2.setInt(1,rs1.getInt(1));
                                        ps2.setString(2, rs1.getString(4));
                                        ps2.setString(3, rs1.getString(5));
                                        ps2.setDouble(4,rs1.getDouble(6));
                                        ps2.setInt(5,quantity);
                                        ps2.setInt(6,User.getCurrentUser().getUserId());
                                        ps2.executeUpdate();
                                    }
                                    else
                                    {
                                        System.out.println("not founded");
                                    }
                                }break;
                            case 3:
                                System.out.println("Exiting");break;
                            default:
                                System.out.println("Invalid choice");
                                break;
                        }
                    }
                    while (choice3!=3);


                    break;
                case 4:
                    int choice4 = 0;
                    do {
                        System.out.println("1 - Juices");
                        System.out.println("2 - Soft Drinks");
                        System.out.println("3 - Exit");
                        choice4 = sc.nextInt();
                        switch (choice4)
                        {
                            case 1:String select = "select * from product where category_id = ? and subcategory_id = ? ";
                                PreparedStatement ps = con.prepareStatement(select,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
                                ps.setInt(1,4);
                                ps.setInt(2,401);
                                //ps.setInt(2,101);
                                ResultSet rs = ps.executeQuery();
                                while(rs.next())
                                {
                                    System.out.println(rs.getInt(1)+"\t"+rs.getInt(2)+"\t"+rs.getInt(3)+"\t"+rs.getString(4)+"\t"+rs.getString(5)+"\t"+rs.getDouble(6)+"\t"+rs.getInt(7));
                                }
                                System.out.println("Do you want to Add any item to Cart(yes/no)");
                                String ans = sc.next().toLowerCase();
                                if(ans.equals("yes"))
                                {
                                    System.out.println("Enter product_id of product ");
                                    int p_id = sc.nextInt();

                                    rs.beforeFirst();
                                    int quantity = 0 ;
                                    boolean found = false;
                                    boolean sufficientstock = false;
                                    while (rs.next())
                                    {

                                        if(rs.getInt(1)==p_id)
                                        {
                                            System.out.println("founded");
                                            System.out.println("Enter Quantity to be added to Cart ");
                                            quantity = sc.nextInt();
                                            if(rs.getInt(7)>quantity)
                                            {
                                                sufficientstock = true;
                                            }
                                            found = true;
                                            break;
                                        }
                                    }
                                    if(found && sufficientstock)
                                    {
                                        String insert = "insert into cart(product_id,product_name,description,price,quantity,user_id) values(?,?,?,?,?,?)";                                        PreparedStatement ps1 = con.prepareStatement(insert);
                                        ps1.setInt(1,rs.getInt(1));
                                        ps1.setString(2, rs.getString(4));
                                        ps1.setString(3, rs.getString(5));
                                        ps1.setDouble(4,rs.getDouble(6));
                                        ps1.setInt(5,quantity);
                                        ps1.setInt(6,User.getCurrentUser().getUserId());
                                        ps1.executeUpdate();
                                    }
                                    else
                                    {
                                        System.out.println("not founded");
                                    }
                                }break;
                            case 2:String select1 = "select * from product where category_id = ? and subcategory = ? ";
                                PreparedStatement ps1 = con.prepareStatement(select1,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
                                ps1.setInt(1,4);
                                ps1.setInt(2,402);
                                //  ps1.setInt(2,101);
                                ResultSet rs1 = ps1.executeQuery();
                                while(rs1.next())
                                {
                                    System.out.println(rs1.getInt(1)+"\t"+rs1.getInt(2)+"\t"+rs1.getInt(3)+"\t"+rs1.getString(4)+"\t"+rs1.getString(5)+"\t"+rs1.getDouble(6)+"\t"+rs1.getInt(7));
                                }
                                System.out.println("Do you want to Add any item to Cart(yes/no)");
                                String ans1 = sc.next().toLowerCase();
                                if(ans1.equals("yes"))
                                {
                                    System.out.println("Enter product_id of product ");
                                    int p_id = sc.nextInt();

                                    rs1.beforeFirst();
                                    int quantity = 0 ;
                                    boolean found = false;
                                    boolean sufficientstock = false;
                                    while (rs1.next())
                                    {

                                        if(rs1.getInt(1)==p_id)
                                        {
                                            System.out.println("founded");
                                            System.out.println("Enter Quantity to be added to Cart ");
                                            quantity = sc.nextInt();
                                            if(rs1.getInt(7)>quantity)
                                            {
                                                sufficientstock = true;
                                            }
                                            found = true;
                                            break;
                                        }
                                    }
                                    if(found && sufficientstock)
                                    {
                                        String insert = "insert into cart(product_id,product_name,description,price,quantity,user_id) values(?,?,?,?,?,?)";                                        PreparedStatement ps2 = con.prepareStatement(insert);
                                        ps2.setInt(1,rs1.getInt(1));
                                        ps2.setString(2, rs1.getString(4));
                                        ps2.setString(3, rs1.getString(5));
                                        ps2.setDouble(4,rs1.getDouble(6));
                                        ps2.setInt(5,quantity);
                                        ps2.setInt(6,User.getCurrentUser().getUserId());
                                        ps2.executeUpdate();
                                    }
                                    else
                                    {
                                        System.out.println("not founded");
                                    }
                                }break;
                            case 3:
                                System.out.println("Exiting");break;
                            default:
                                System.out.println("Invalid Choice");
                                break;
                        }
                    }
                    while (choice4!=3);
                    break;

                case 5:
                    int choice5 = 0;
                    do {
                        System.out.println("1 - Cookware");
                        System.out.println("2 - Dining");
                        System.out.println("3 - Exit");
                        System.out.println("Enter your choice - ");
                        choice5 = sc.nextInt();
                        switch (choice5)
                        {
                            case 1: String select = "select * from product where category_id = ? and subcategory_id = ? ";
                                PreparedStatement ps = con.prepareStatement(select,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
                                ps.setInt(1,5);
                                ps.setInt(2,501);
                                // ps.setInt(2,101);
                                ResultSet rs = ps.executeQuery();
                                while(rs.next())
                                {
                                    System.out.println(rs.getInt(1)+"\t"+rs.getInt(2)+"\t"+rs.getInt(3)+"\t"+rs.getString(4)+"\t"+rs.getString(5)+"\t"+rs.getDouble(6)+"\t"+rs.getInt(7));
                                }
                                System.out.println("Do you want to Add any item to Cart(yes/no)");
                                String ans = sc.next().toLowerCase();
                                if(ans.equals("yes"))
                                {
                                    System.out.println("Enter product_id of product ");
                                    int p_id = sc.nextInt();

                                    rs.beforeFirst();
                                    int quantity = 0 ;
                                    boolean found = false;
                                    boolean sufficientstock = false;
                                    while (rs.next())
                                    {

                                        if(rs.getInt(1)==p_id)
                                        {
                                            System.out.println("founded");
                                            System.out.println("Enter Quantity to be added to Cart ");
                                            quantity = sc.nextInt();
                                            if(rs.getInt(7)>quantity)
                                            {
                                                sufficientstock = true;
                                            }
                                            found = true;
                                            break;
                                        }
                                    }
                                    if(found && sufficientstock)
                                    {
                                        String insert = "insert into cart(product_id,product_name,description,price,quantity,user_id) values(?,?,?,?,?,?)";                                        PreparedStatement ps1 = con.prepareStatement(insert);
                                        ps1.setInt(1,rs.getInt(1));
                                        ps1.setString(2, rs.getString(4));
                                        ps1.setString(3, rs.getString(5));
                                        ps1.setDouble(4,rs.getDouble(6));
                                        ps1.setInt(5,quantity);
                                        ps1.setInt(6,User.getCurrentUser().getUserId());
                                        ps1.executeUpdate();
                                    }
                                    else
                                    {
                                        System.out.println("not founded");
                                    }
                                }break;
                            case 2: String select1 = "select * from product where category_id = ? and subcategory_id = ?";
                                PreparedStatement ps1 = con.prepareStatement(select1,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
                                ps1.setInt(1,5);
                                ps1.setInt(2,502);
                                // ps1.setInt(2,101);
                                ResultSet rs1 = ps1.executeQuery();
                                while(rs1.next())
                                {
                                    System.out.println(rs1.getInt(1)+"\t"+rs1.getInt(2)+"\t"+rs1.getInt(3)+"\t"+rs1.getString(4)+"\t"+rs1.getString(5)+"\t"+rs1.getDouble(6)+"\t"+rs1.getInt(7));
                                }
                                System.out.println("Do you want to Add any item to Cart(yes/no)");
                                String ans1 = sc.next().toLowerCase();
                                if(ans1.equals("yes"))
                                {
                                    System.out.println("Enter product_id of product ");
                                    int p_id = sc.nextInt();

                                    rs1.beforeFirst();
                                    int quantity = 0 ;
                                    boolean found = false;
                                    boolean sufficientstock = false;
                                    while (rs1.next())
                                    {

                                        if(rs1.getInt(1)==p_id)
                                        {
                                            System.out.println("founded");
                                            System.out.println("Enter Quantity to be added to Cart ");
                                            quantity = sc.nextInt();
                                            if(rs1.getInt(7)>quantity)
                                            {
                                                sufficientstock = true;
                                            }
                                            found = true;
                                            break;
                                        }
                                    }
                                    if(found && sufficientstock)
                                    {
                                        String insert = "insert into cart(product_id,product_name,description,price,quantity,user_id) values(?,?,?,?,?,?)";                                        PreparedStatement ps2 = con.prepareStatement(insert);
                                        ps2.setInt(1,rs1.getInt(1));
                                        ps2.setString(2, rs1.getString(4));
                                        ps2.setString(3, rs1.getString(5));
                                        ps2.setDouble(4,rs1.getDouble(6));
                                        ps2.setInt(5,quantity);
                                        ps2.setInt(6,User.getCurrentUser().getUserId());
                                        ps2.executeUpdate();
                                    }
                                    else
                                    {
                                        System.out.println("not founded");
                                    }
                                }break;
                            case 3:
                                System.out.println("Exiting"); break;
                            default:
                                System.out.println("Invalid Choice");
                                break;
                        }
                    }
                    while (choice5!=3);
                    break;

                case 6:
                    int choice6 = 0;
                    do {
                        System.out.println("1 - Notebooks");
                        System.out.println("2 - Pens");
                        System.out.println("3 - Exit");
                        System.out.println("Enter your choice - ");
                        choice6 = sc.nextInt();
                        switch (choice6)
                        {
                            case 1:String select = "select * from product where category_id = ? and subcategory_id = ? ";
                                PreparedStatement ps = con.prepareStatement(select,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
                                ps.setInt(1,6);
                                ps.setInt(2,601);
                                // ps.setInt(2,101);
                                ResultSet rs = ps.executeQuery();
                                while(rs.next())
                                {
                                    System.out.println(rs.getInt(1)+"\t"+rs.getInt(2)+"\t"+rs.getInt(3)+"\t"+rs.getString(4)+"\t"+rs.getString(5)+"\t"+rs.getDouble(6)+"\t"+rs.getInt(7));
                                }
                                System.out.println("Do you want to Add any item to Cart(yes/no)");
                                String ans = sc.next().toLowerCase();
                                if(ans.equals("yes"))
                                {
                                    System.out.println("Enter product_id of product ");
                                    int p_id = sc.nextInt();

                                    rs.beforeFirst();
                                    int quantity = 0 ;
                                    boolean found = false;
                                    boolean sufficientstock = false;
                                    while (rs.next())
                                    {

                                        if(rs.getInt(1)==p_id)
                                        {
                                            System.out.println("founded");
                                            System.out.println("Enter Quantity to be added to Cart ");
                                            quantity = sc.nextInt();
                                            if(rs.getInt(7)>quantity)
                                            {
                                                sufficientstock = true;
                                            }
                                            found = true;
                                            break;
                                        }
                                    }
                                    if(found && sufficientstock)
                                    {
                                        String insert = "insert into cart(product_id,product_name,description,price,quantity,user_id) values(?,?,?,?,?,?)";                                        PreparedStatement ps1 = con.prepareStatement(insert);
                                        ps1.setInt(1,rs.getInt(1));
                                        ps1.setString(2, rs.getString(4));
                                        ps1.setString(3, rs.getString(5));
                                        ps1.setDouble(4,rs.getDouble(6));
                                        ps1.setInt(5,quantity);
                                        ps1.setInt(6,User.getCurrentUser().getUserId());
                                        ps1.executeUpdate();
                                    }
                                    else
                                    {
                                        System.out.println("not founded");
                                    }
                                }break;
                            case 2: String select1 = "select * from product where category_id = ? and subcategory_id = ? ";
                                PreparedStatement ps1 = con.prepareStatement(select1,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
                                ps1.setInt(1,6);
                                ps1.setInt(2,602);
                                // ps1.setInt(2,101);
                                ResultSet rs1 = ps1.executeQuery();
                                while(rs1.next())
                                {
                                    System.out.println(rs1.getInt(1)+"\t"+rs1.getInt(2)+"\t"+rs1.getInt(3)+"\t"+rs1.getString(4)+"\t"+rs1.getString(5)+"\t"+rs1.getDouble(6)+"\t"+rs1.getInt(7));
                                }
                                System.out.println("Do you want to Add any item to Cart(yes/no)");
                                String ans1 = sc.next().toLowerCase();
                                if(ans1.equals("yes"))
                                {
                                    System.out.println("Enter product_id of product ");
                                    int p_id = sc.nextInt();

                                    rs1.beforeFirst();
                                    int quantity = 0 ;
                                    boolean found = false;
                                    boolean sufficientstock = false;
                                    while (rs1.next())
                                    {

                                        if(rs1.getInt(1)==p_id)
                                        {
                                            System.out.println("founded");
                                            System.out.println("Enter Quantity to be added to Cart ");
                                            quantity = sc.nextInt();
                                            if(rs1.getInt(7)>quantity)
                                            {
                                                sufficientstock = true;
                                            }
                                            found = true;
                                            break;
                                        }
                                    }
                                    if(found && sufficientstock)
                                    {
                                        String insert = "insert into cart(product_id,product_name,description,price,quantity,user_id) values(?,?,?,?,?,?)";                                        PreparedStatement ps2 = con.prepareStatement(insert);
                                        ps2.setInt(1,rs1.getInt(1));
                                        ps2.setString(2, rs1.getString(4));
                                        ps2.setString(3, rs1.getString(5));
                                        ps2.setDouble(4,rs1.getDouble(6));
                                        ps2.setInt(5,quantity);
                                        ps2.setInt(6,User.getCurrentUser().getUserId());
                                        ps2.executeUpdate();
                                    }
                                    else
                                    {
                                        System.out.println("not founded");
                                    }
                                }break;
                            case 3:
                                System.out.println("Exiting");break;
                            default:
                                System.out.println("Invalid Choice");
                        }
                    }
                    while (choice6!=3);



                    break;
                case 7:
                    int choice7 = 0;
                    do {
                        System.out.println("1 - Men's Wear");
                        System.out.println("2 - Women's Wear");
                        System.out.println("3 - Exit");
                        System.out.println("Enter your Choice - ");
                        choice7 = sc.nextInt();
                        switch (choice7)
                        {
                            case 1: String select = "select * from product where category_id = ? and subcategory_id = ? ";
                                PreparedStatement ps = con.prepareStatement(select,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
                                ps.setInt(1,7);
                                ps.setInt(2,701);
                                //ps.setInt(2,101);
                                ResultSet rs = ps.executeQuery();
                                while(rs.next())
                                {
                                    System.out.println(rs.getInt(1)+"\t"+rs.getInt(2)+"\t"+rs.getInt(3)+"\t"+rs.getString(4)+"\t"+rs.getString(5)+"\t"+rs.getDouble(6)+"\t"+rs.getInt(7));
                                }
                                System.out.println("Do you want to Add any item to Cart(yes/no)");
                                String ans = sc.next().toLowerCase();
                                if(ans.equals("yes"))
                                {
                                    System.out.println("Enter product_id of product ");
                                    int p_id = sc.nextInt();

                                    rs.beforeFirst();
                                    int quantity = 0 ;
                                    boolean found = false;
                                    boolean sufficientstock = false;
                                    while (rs.next())
                                    {

                                        if(rs.getInt(1)==p_id)
                                        {
                                            System.out.println("founded");
                                            System.out.println("Enter Quantity to be added to Cart ");
                                            quantity = sc.nextInt();
                                            if(rs.getInt(7)>quantity)
                                            {
                                                sufficientstock = true;
                                            }
                                            found = true;
                                            break;
                                        }
                                    }
                                    if(found && sufficientstock)
                                    {
                                        String insert = "insert into cart(product_id,product_name,description,price,quantity,user_id) values(?,?,?,?,?,?)";                                        PreparedStatement ps1 = con.prepareStatement(insert);
                                        ps1.setInt(1,rs.getInt(1));
                                        ps1.setString(2, rs.getString(4));
                                        ps1.setString(3, rs.getString(5));
                                        ps1.setDouble(4,rs.getDouble(6));
                                        ps1.setInt(5,quantity);
                                        ps1.setInt(6,User.getCurrentUser().getUserId());
                                        ps1.executeUpdate();
                                    }
                                    else
                                    {
                                        System.out.println("not founded");
                                    }
                                }break;
                            case 2: String select1 = "select * from product where category_id = ? and subcategory_id = ? ";
                                PreparedStatement ps1 = con.prepareStatement(select1,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
                                ps1.setInt(1,7);
                                ps1.setInt(2,702);
                                //  ps1.setInt(2,101);
                                ResultSet rs1 = ps1.executeQuery();
                                while(rs1.next())
                                {
                                    System.out.println(rs1.getInt(1)+"\t"+rs1.getInt(2)+"\t"+rs1.getInt(3)+"\t"+rs1.getString(4)+"\t"+rs1.getString(5)+"\t"+rs1.getDouble(6)+"\t"+rs1.getInt(7));
                                }
                                System.out.println("Do you want to Add any item to Cart(yes/no)");
                                String ans1 = sc.next().toLowerCase();
                                if(ans1.equals("yes"))
                                {
                                    System.out.println("Enter product_id of product ");
                                    int p_id = sc.nextInt();

                                    rs1.beforeFirst();
                                    int quantity = 0 ;
                                    boolean found = false;
                                    boolean sufficientstock = false;
                                    while (rs1.next())
                                    {

                                        if(rs1.getInt(1)==p_id)
                                        {
                                            System.out.println("founded");
                                            System.out.println("Enter Quantity to be added to Cart ");
                                            quantity = sc.nextInt();
                                            if(rs1.getInt(7)>quantity)
                                            {
                                                sufficientstock = true;
                                            }
                                            found = true;
                                            break;
                                        }
                                    }
                                    if(found && sufficientstock)
                                    {
                                        String insert = "insert into cart(product_id,product_name,description,price,quantity,user_id) values(?,?,?,?,?,?)";                                        PreparedStatement ps2 = con.prepareStatement(insert);
                                        ps2.setInt(1,rs1.getInt(1));
                                        ps2.setString(2, rs1.getString(4));
                                        ps2.setString(3, rs1.getString(5));
                                        ps2.setDouble(4,rs1.getDouble(6));
                                        ps2.setInt(5,quantity);
                                        ps2.setInt(6,User.getCurrentUser().getUserId());
                                        ps2.executeUpdate();
                                    }
                                    else
                                    {
                                        System.out.println("not founded");
                                    }
                                }break;
                            case 3:
                                System.out.println("Exiting");break;
                            default:
                                System.out.println("Invalid Choice");
                        }
                    }while (choice7!=3);
                    break;

                case 8:
                    int choice8 = 0;
                    do {
                        System.out.println("1 - Floor Cleaner");
                        System.out.println("2 - Detergents");
                        System.out.println("3 - Exit");
                        System.out.println("Enter your Choice");
                        switch (choice8) {
                            case 1:
                                String select = "select * from product where category_id = ? and subcategory = ? ";
                                PreparedStatement ps = con.prepareStatement(select,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
                                ps.setInt(1, 8);
                                ps.setInt(2,801);
                                //ps.setInt(2,101);
                                ResultSet rs = ps.executeQuery();
                                while (rs.next()) {
                                    System.out.println(rs.getInt(1) + "\t" + rs.getInt(2) + "\t" + rs.getInt(3) + "\t" + rs.getString(4) + "\t" + rs.getString(5) + "\t" + rs.getDouble(6) + "\t" + rs.getInt(7));
                                }
                                System.out.println("Do you want to Add any item to Cart(yes/no)");
                                String ans = sc.next().toLowerCase();
                                if (ans.equals("yes")) {
                                    System.out.println("Enter product_id of product ");
                                    int p_id = sc.nextInt();

                                    rs.beforeFirst();
                                    int quantity = 0;
                                    boolean found = false;
                                    boolean sufficientstock = false;
                                    while (rs.next()) {

                                        if (rs.getInt(1) == p_id) {
                                            System.out.println("founded");
                                            System.out.println("Enter Quantity to be added to Cart ");
                                            quantity = sc.nextInt();
                                            if (rs.getInt(7) > quantity) {
                                                sufficientstock = true;
                                            }
                                            found = true;
                                            break;
                                        }
                                    }
                                    if (found && sufficientstock) {
                                        String insert = "insert into cart(product_id,product_name,description,price,quantity,user_id) values(?,?,?,?,?,?)";                                        PreparedStatement ps1 = con.prepareStatement(insert);
                                        ps1.setInt(1, rs.getInt(1));
                                        ps1.setString(2, rs.getString(4));
                                        ps1.setString(3, rs.getString(5));
                                        ps1.setDouble(4, rs.getDouble(6));
                                        ps1.setInt(5, quantity);
                                        ps1.setInt(6,User.getCurrentUser().getUserId());
                                        ps1.executeUpdate();
                                    } else {
                                        System.out.println("not founded");
                                    }
                                }
                                break;
                            case 2:
                                String select1 = "select * from product where category_id = ? and subcategory_id = ?  ";
                                PreparedStatement ps1 = con.prepareStatement(select1,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
                                ps1.setInt(1, 8);
                                ps1.setInt(2,802);
                                // ps1.setInt(2,101);
                                ResultSet rs1 = ps1.executeQuery();
                                while (rs1.next()) {
                                    System.out.println(rs1.getInt(1) + "\t" + rs1.getInt(2) + "\t" + rs1.getInt(3) + "\t" + rs1.getString(4) + "\t" + rs1.getString(5) + "\t" + rs1.getDouble(6) + "\t" + rs1.getInt(7));
                                }
                                System.out.println("Do you want to Add any item to Cart(yes/no)");
                                String ans1 = sc.next().toLowerCase();
                                if (ans1.equals("yes")) {
                                    System.out.println("Enter product_id of product ");
                                    int p_id = sc.nextInt();

                                    rs1.beforeFirst();
                                    int quantity = 0;
                                    boolean found = false;
                                    boolean sufficientstock = false;
                                    while (rs1.next()) {

                                        if (rs1.getInt(1) == p_id) {
                                            System.out.println("founded");
                                            System.out.println("Enter Quantity to be added to Cart ");
                                            quantity = sc.nextInt();
                                            if (rs1.getInt(7) > quantity) {
                                                sufficientstock = true;
                                            }
                                            found = true;
                                            break;
                                        }
                                    }
                                    if (found && sufficientstock) {
                                        String insert = "insert into cart(product_id,product_name,description,price,quantity,user_id) values(?,?,?,?,?,?)";                                        PreparedStatement ps2 = con.prepareStatement(insert);
                                        ps2.setInt(1, rs1.getInt(1));
                                        ps2.setString(2, rs1.getString(4));
                                        ps2.setString(3, rs1.getString(5));
                                        ps2.setDouble(4, rs1.getDouble(6));
                                        ps2.setInt(5, quantity);
                                        ps2.setInt(6,User.getCurrentUser().getUserId());
                                        ps2.executeUpdate();

                                    } else {
                                        System.out.println("not founded");
                                    }
                                }
                                break;
                            case 3:
                                System.out.println("Exit");break;
                            default:
                                System.out.println("Invalid Choice");
                                break;
                        }
                    }
                    while (choice8!=3);




                    break;
                case 9:break;
                default:
                    System.out.println("Invalid Choice");
            }
        }
        while (choice!=9);

    }

    private static void profileManagement() throws Exception {
        System.out.println("1.FirstName\n2.LastName\n3.UserName\n4.MobileNo\n5.Password\n6.Add Address\n7.EXIT\n\nEnter your Choice : ");
        int currentUserId = 1;
        User user = loggedInUser.get(User.getCurrentUser().getUserId());
        // System.out.println(User.getCurrentUser().getUserName());
        int choice = sc.nextInt();
        switch (choice) {
            case 1:
                System.out.print("Enter new FirstName : ");
                String newFirstName = sc.next();
                String updateFirstName = "UPDATE users SET first_name = ? WHERE username = ?";
                try (PreparedStatement updateStmt = Database.getCon().prepareStatement(updateFirstName)) {
                    updateStmt.setString(1, newFirstName);
                    updateStmt.setString(2, User.getCurrentUser().getUserName());
                    updateStmt.executeUpdate();
                }
                System.out.println("✅ First Name Updated Successfully");
                user.setFirstName(newFirstName);
                break;
            case 2:
                System.out.print("Enter new LastName : ");
                String newLastName = sc.next();
                String updateLastName = "UPDATE users SET last_name = ? WHERE userName = ?";
                try (PreparedStatement updateStmt = Database.getCon().prepareStatement(updateLastName)) {
                    updateStmt.setString(1, newLastName);
                    updateStmt.setString(2, User.getCurrentUser().getLastName());
                    updateStmt.executeUpdate();
                }
                System.out.println("✅ Last Name Updated Successfully");
                user.setLastName(newLastName);
                break;
            case 3:
                System.out.print("Enter new UserName : ");
                String newUserName = sc.next();
                String updateUserName = "UPDATE users SET username = ? WHERE username = ?";
                try (PreparedStatement updateStmt = Database.getCon().prepareStatement(updateUserName)) {
                    updateStmt.setString(1, newUserName);
                    updateStmt.setString(2, User.getCurrentUser().getUserName());
                    updateStmt.executeUpdate();
                }
                System.out.println("✅ User Name Updated Successfully");
                user.setUserName(newUserName);
                break;
            case 4://int currentUserId = 1;
                User user1 = loggedInUser.get(User.getCurrentUser().getUserId());
                System.out.print("Enter new MobileNumber : ");
                String newmobileno = sc.next();
                String updatemobileno = "UPDATE users SET mobile_no = ? WHERE username = ?";
                try (PreparedStatement updateStmt = Database.getCon().prepareStatement(updatemobileno)) {
                    updateStmt.setString(1, newmobileno);
                    updateStmt.setString(2, User.getCurrentUser().getUserName());
                    updateStmt.executeUpdate();
                }
                System.out.println("✅ Mobile No Updated Successfully");
                user.setMobileNo(newmobileno);
                break;
            case 5:
                //System.out.println(user.getPassword());
                System.out.print("Enter new Password : ");
                String newPassword = sc.next();
                String updatePassword = "UPDATE users SET password = ? WHERE username = ?";
                try (PreparedStatement updateStmt = Database.getCon().prepareStatement(updatePassword)) {
                    updateStmt.setString(1, newPassword);
                    updateStmt.setString(2, User.getCurrentUser().getUserName());
                    updateStmt.executeUpdate();
                }
                System.out.println("✅ Password Updated Successfully");
                user.setPassword(newPassword);
                break;
            case 6:
                addAddress();
                System.out.println("✅ Address Added Successfully");
                break;
            case 7:
                System.out.println("EXITING");
                break;
            default:
                System.out.println("Invalid Choice");
        }
    }

    public static void viewCart() throws Exception, IOException {
        // int currentUserId = 1;

//        String usersid = "select user_id from users";
//        PreparedStatement ps5 = Database.getCon().prepareStatement(usersid, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
//        ResultSet rs5 = ps5.executeQuery();
        User user = loggedInUser.get(User.getCurrentUser().getUserId());


        String fetchCart = "SELECT p.product_id, p.product_name, c.quantity, c.price " +
                "FROM cart c JOIN product p ON c.product_id = p.product_id where user_id = ?";

        try (PreparedStatement fetchCartItems = Database.getCon().prepareStatement(
                fetchCart, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            fetchCartItems.setInt(1,User.getCurrentUser().getUserId());
            ResultSet rs = fetchCartItems.executeQuery();

            System.out.println("\n----------- 🛒 Your Cart -----------");
            boolean hasItems = false;
            double  total = 0;


            // First loop to display cart items
            while (rs.next()) {
                hasItems = true;
                int productId = rs.getInt("product_id");
                String productName = rs.getString("product_name");
                int quantity = rs.getInt("quantity");
                double price = rs.getDouble("price");
                double itemTotal = price * quantity;
                total += itemTotal;

                System.out.println("Product ID     : " + productId);
                System.out.println("Product Name   : " + productName);
                System.out.println("Quantity       : " + quantity);
                System.out.println("Price per unit : ₹" + price);
                System.out.println("Total          : ₹" + itemTotal);
                System.out.println("-----------------------------------");
            }
            if(hasItems) {
                System.out.println("Do you want to update Cart details(yes/no)");
                String ans = sc.next().toLowerCase();
                if (ans.equals("yes")) {
                    System.out.print("Enter Product ID to update quantity: ");
                    int updatePid = sc.nextInt();
                    System.out.print("Enter new quantity: ");
                    int newQty = sc.nextInt();
                    String stockCheckQuery = "SELECT stock FROM product WHERE product_id = ?";
                    try (PreparedStatement stockStmt = Database.getCon().prepareStatement(stockCheckQuery)) {
                        stockStmt.setInt(1, updatePid);
                        ResultSet rs1 = stockStmt.executeQuery();

                        if (rs1.next()) {
                            int availableStock = rs.getInt(1);

                            // Step 2: Check condition
                            if (availableStock < 2) {
                                System.out.println("❌ Out of Stock! Only " + availableStock + " left.");
                            } else if (newQty > availableStock) {
                                System.out.println("❌ Cannot update. Requested quantity exceeds available stock (" + availableStock + ").");
                            } else {
                                // Step 3: Proceed with update
                                String updateCart = "UPDATE cart SET quantity = ? WHERE product_id = ?";
                                try (PreparedStatement updateStmt = Database.getCon().prepareStatement(updateCart)) {
                                    updateStmt.setInt(1, newQty);
                                    updateStmt.setInt(2, updatePid);
                                    int rowsUpdated = updateStmt.executeUpdate();

                                    if (rowsUpdated > 0) {
                                        System.out.println("✅ Cart updated successfully!");
                                    } else {
                                        System.out.println("❌ Product not found in cart.");
                                    }
                                }
                            }
                        } else {
                            System.out.println("❌ Product not found in product table.");
                        }
                    }
                }

            }
            if (!hasItems) {
                System.out.println("🛒 Your cart is empty.");
                return;
            }



            System.out.println("🧾 Grand Total: ₹" + total);
            System.out.println("1. Place Order");
            System.out.println("2. CheckOut");
            System.out.println("3. Back");
            System.out.print("Enter Choice: ");

            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter Product ID to be ordered: ");
                    int pid = sc.nextInt();

                    // Move cursor to start
                    rs.beforeFirst();
                    while (rs.next()) {
                        if (rs.getInt("product_id") == pid) {
                            double tprice = rs.getDouble("price") * rs.getInt("quantity");

                            String placingOrder = "INSERT INTO orders " +
                                    "(product_id, product_name, quantity, price, total_price, order_date,user_id) " +
                                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
                            try (PreparedStatement ps = Database.getCon().prepareStatement(placingOrder)) {
                                ps.setInt(1, rs.getInt("product_id"));
                                ps.setString(2, rs.getString("product_name"));
                                ps.setInt(3, rs.getInt("quantity"));
                                ps.setDouble(4, rs.getDouble("price"));
                                ps.setDouble(5, tprice);
                                ps.setTimestamp(6, new java.sql.Timestamp(System.currentTimeMillis()));
                                ps.setInt(7, User.getCurrentUser().getUserId());
                                String delete = "delete from cart where product_id = ?";
                                PreparedStatement ps6 = Database.getCon().prepareStatement(delete);
                                ps6.setInt(1,rs.getInt("product_id"));
                                ps6.executeUpdate();

                                ps.executeUpdate();

                                System.out.println("do you want to order more(yes/no)");
                                String ans1 = sc.next().toLowerCase();

                                if(ans1.equals("yes"))
                                {
                                    viewCart();
                                }
                                else {
                                    System.out.println("✅ Order placed successfully!");
                                    String users = "select first_name,user_id from users where user_id = ?";
                                    PreparedStatement ps3 = Database.getCon().prepareStatement(users);
                                    ps3.setInt(1,User.getCurrentUser().getUserId());
                                    ResultSet rs3 = ps3.executeQuery();

                                    if(rs3.next())
                                    {
                                        String name = rs3.getString(1);
                                        String orders = "select * from orders where user_id = ? ";
                                        PreparedStatement ps4 = Database.getCon().prepareStatement(orders);
                                        ps4.setInt(1,User.getCurrentUser().getUserId());
                                        ResultSet rsOrder = ps4.executeQuery();
                                        FileWriter writer = new FileWriter(name + ".txt");
                                        writer.write("Orders for " + name + ":\n");
                                        while (rsOrder.next()) {
                                            int orderId = rsOrder.getInt("order_id");
                                            int productId = rsOrder.getInt("product_id");
                                            int quantity = rsOrder.getInt("quantity");
                                            String productName = rsOrder.getString("product_name");
                                            double price = rsOrder.getDouble("price");
                                            double totalPrice = rsOrder.getDouble("total_price");
                                            Date date = rsOrder.getDate("order_date");

                                            writer.write("OrderID \n" + orderId +
                                                    ", ProductID \n" + productId +
                                                    ", Product_Name \n" + productName + ", Quantity \n"+ quantity +", Price \n"+price+", Total_Price \n"+totalPrice+", Order_Date \n"+date+"\n");
                                            writer.flush();
                                            writer.write("------\n");
                                        }

                                        String join = "select users.user_id,orders.order_date from users inner join orders on users.user_id = orders.user_id";
                                        PreparedStatement ps1 = Database.getCon().prepareStatement(join);
                                        ResultSet rs1 = ps1.executeQuery();
                                        while (rs1.next())
                                        {
                                            String insertbill = "Insert into bills(customer_id,bill_date,bill) values(?,?,?)";
                                            PreparedStatement ps2 = Database.getCon().prepareStatement(insertbill);
                                            ps2.setInt(1,rs1.getInt(1));
                                            ps2.setDate(2,rs1.getDate(2));
                                            FileInputStream fis = new FileInputStream(name+".txt");

                                            ps2.setBlob(3,fis);

                                            ps2.executeUpdate();
                                            fis.close();

                                        }



                                    }


                                    break;
                                }
                            }


                        }
                    }
                    break;

                case 2:
                    System.out.println("✅ Proceeding to checkout...");
                    checkOut();
                    break;

                case 3:
                    System.out.println("🔙 Returning to previous menu...");
                    break;

                default:
                    System.out.println("❌ Invalid choice! Please select 1, 2, or 3.");
            }
        }
    }


    private static void checkOut() throws Exception, InterruptedException {
        User user = User.getCurrentUser();
        int currentUserId = 1;
        // User user = loggedInUser.get(currentUserId);
        //loggedInUser.put(currentUserId,user);
        String fetchAddress = "SELECT * from address where user_id = ? " ;
        try (PreparedStatement fetchCartItems = Database.getCon().prepareStatement(fetchAddress)) {
            fetchCartItems.setInt(1, User.getCurrentUser().getUserId());
            ResultSet rs = fetchCartItems.executeQuery();
            boolean flag = true;
            boolean found = true;

            while (rs.next()) {
                String name = rs.getString("name");
                String address1 = rs.getString("address_line_1");
                String address2 = rs.getString("address_line_2");
                // String area = rs.getString("area");
                String city = rs.getString("city");
                String state = rs.getString("state");
                int pincode = rs.getInt("pincode");
                System.out.println("----------------------------------");
                System.out.println("\n📦 Saved Address:");
                System.out.println("Name           : " + name);
                System.out.println("Address Line1  : " + address1);
                System.out.println("Address Line2  : " + address2);
                // System.out.println("Area         : " + area);
                System.out.println("City           : " + city);
                System.out.println("State          : " + state);
                System.out.println("Pin Code       : " + pincode);
                System.out.println("----------------------------------");

                if (flag) {
                    System.out.println("Want to Delivered to your default address");
                    String choice = sc.next();
                    if (choice.equalsIgnoreCase("yes")) {
                        System.out.println("✅ Delivery address Confirmed.");
                        break;
                    } else {
                        flag = false;
                    }
                }
            }
            if (!found) {
                System.out.println("❌ No address found. You can add a new address");
                addAddress();
                System.out.println("✅ Address Added Successfully");
            }
        }
        System.out.println("Do you want to see your order(yes/no)");
        String ans = sc.next().toLowerCase();
        if(ans.equals("yes"))
        {
            viewOrders();
            return;
        }
        else {
            //System.out.println("Revisit details");
            //checkOut();
            return;

        }



    }

    public static void viewOrders() throws Exception {
        String fetchOrders = "Select * from orders where user_id = ? ";
        PreparedStatement ps = Database.getCon().prepareStatement(fetchOrders);
        ps.setInt(1,User.getCurrentUser().getUserId());
        //System.out.println(User.getCurrentUser().getUserId());
        ResultSet rs = ps.executeQuery();
        System.out.println("User_id  order_id  product_id  product_name    quantity    price    total_price   order_date");
        while(rs.next())
        {
            System.out.println(rs.getInt(1)+"   \t   "+rs.getInt(2)+"   \t   "+rs.getInt(3)+"   \t   "+rs.getString(4)+"   \t   "+rs.getInt(5)+"   \t   "+rs.getInt(6)+"   \t   "+rs.getInt(7)+"   \t   "+rs.getDate(8));

        }
        System.out.println("1.Proceed To Pay\n2.Back\n\nEnter your choice : ");
        int choice = sc.nextInt();

        switch (choice) {
            case 1:
                payment();
                break;
            case 2:
                System.out.println("↩️ Returning to previous menu...");
                // Possibly call main menu or cart view method again
                break;
            default:
                System.out.println("❌ Invalid choice! Please enter 1 or 2.");
        }


    }

    public static void start() throws Exception
    {
        Scanner sc = new Scanner(System.in);
        OrderStack stack = new OrderStack();
        OrderProcessor processor = new OrderProcessor();

        while (true)
        {
            System.out.println("\n----- 🛍️ Welcome to Shopping Cart - Inventory Management System -----");
            System.out.println("1. 🔍 Search Product");
            System.out.println("2. 🗂️ Categories");
            System.out.println("3. 🛒 View Cart");
            System.out.println("4. 👤 Profile Management");
            System.out.println("5. 📦 My Orders");
            System.out.println("6.    Process Order(by stack)");
            System.out.println("7.    View Order Stack");
            System.out.println("8.    Undo Last Order");
            System.out.println("9. 🚪 Logout / Exit");
            // System.out.println("7. 💳 Back To Last Menu");
            System.out.print("Choose an option (1-9): ");

            int choice = sc.nextInt();
            sc.nextLine(); // Database.getCon()some newline

            try {
                switch (choice) {
                    case 1:
                        searchProduct();
                        break;
                    case 2:
                        viewCategories();
                        break;
                    case 3:
                        viewCart();
                        break;
                    case 4:
                        // Fully Working
                        profileManagement();
                        break;
                    case 5:
                        viewOrders();
                        break;
                    case 6:
                        System.out.print("Enter Order ID: ");
                        int id = sc.nextInt();
                        processor.processOrder(id, Database.getCon(), stack);
                        break;
                    case 7:  stack.display(); break;
                    case 8:     Order removed = stack.pop();
                        if (removed != null) {
                            System.out.println("Removed: " + removed);
                        }
                        break;
                    case 9:
                        System.out.println("👋 Thank you for visiting! Goodbye.");
                        // loggedInUser.remove();
                        return;
//
                    default:
                        System.out.println("❌ Invalid choice. Please enter between 1 and 7.");
                }
            } catch (SQLException e) {
                System.out.println("❌ Error: " + e.getMessage());
            }
        }
    }
}
class Order {
    int user_id;
    int order_id;
    int product_id;
    String product_name;
    int quantity;
    double total_price;
    Date order_date;

    public Order(String product_name, int user_id, int order_id, int product_id, int quantity, double total_price, Date order_date) {
        this.product_name = product_name;
        this.user_id = user_id;
        this.order_id = order_id;
        this.product_id = product_id;
        this.quantity = quantity;
        this.total_price = total_price;
        this.order_date = order_date;
    }

    @Override
    public String toString() {
        return "Order{" +
                "user_id=" + user_id +
                ", order_id=" + order_id +
                ", product_id=" + product_id +
                ", product_name='" + product_name + '\'' +
                ", quantity=" + quantity +
                ", total_price=" + total_price +
                ", order_date=" + order_date +
                '}';
    }
}
class OrderStack {
    private final int MAX = 100;
    private Order[] stack = new Order[MAX];
    private int top = -1;

    // Manual push logic
    public void push(Order order) {
        if (top == MAX - 1) {
            System.out.println("Stack Overflow! Cannot add more orders.");
            return;
        }
        top++;
        stack[top] = order;
        System.out.println("Order pushed to stack: " + order);
    }

    // Manual pop logic (if needed)
    public Order pop() {
        if (top == -1) {
            System.out.println("Stack Underflow! No orders to pop.");
            return null;
        }
        Order removedOrder = stack[top];
        top--;
        return removedOrder;
    }

    // View stack contents
    public void display() {
        if (top == -1) {
            System.out.println("Stack is empty.");
            return;
        }
        System.out.println("----- Order History (Top to Bottom) -----");
        for (int i = top; i >= 0; i--) {
            System.out.println(stack[i]);
            System.out.println();
        }
    }
}


class OrderProcessor
{
    public void processOrder(int orderId, Connection con, OrderStack stack)
    {
        try
        {
            // 1. Fetch order
            String selectQuery = "SELECT * FROM orders WHERE order_id = ?";
            PreparedStatement selectPs = con.prepareStatement(selectQuery);
            selectPs.setInt(1, orderId);
            ResultSet rs = selectPs.executeQuery();

            if (rs.next()) {
                String productName = rs.getString("product_name");
                int userId = rs.getInt("user_id");
                int productId = rs.getInt("product_id");
                int quantity = rs.getInt("quantity");
                double totalPrice = rs.getDouble("total_price");
                Date orderDate = rs.getDate("order_date");

                // 2. Create order object
                Order order = new Order(productName, userId, orderId, productId, quantity, totalPrice, orderDate);

                // 3. Delete from DB
                String deleteQuery = "DELETE FROM orders WHERE order_id = ?";
                PreparedStatement deletePs = con.prepareStatement(deleteQuery);
                deletePs.setInt(1, orderId);
                int rowsAffected = deletePs.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Order processed and deleted from DB.");
                    stack.push(order);  // use manual push logic
                } else {
                    System.out.println("Failed to delete order.");
                }
            } else {
                System.out.println("Order ID not found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


