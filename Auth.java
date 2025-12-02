package Modules.Auth;

import Database.Database;
import Modules.Address.Address;
import Modules.Users.AdminManagement.AdminManagement;
import Modules.Users.CustomerManagement.CustomerManagement;
import Modules.Users.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;



//import static Modules.Users.CustomerManagement.CustomerManagement.user;

public class Auth
{
    Scanner sc = new Scanner(System.in);

    private boolean isValidEmail(String email)
    {
        // Accepts emails like test@example.com
        String emailPattern = "^[\\w.-]+@[a-zA-Z\\d.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailPattern);
    }

    private boolean isValidMobileNo(String mobileNo)
    {
        // Accepts exactly 10 digit mobile numbers
        return mobileNo.matches("\\d{10}");
    }

    private boolean isValidPassword(String password)
    {
        String pattern = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}:;\"'<>?,./]).{8,}$";
        return password.matches(pattern);
    }

    // Method to check if first name is only alphabets
    public static boolean isValidFirstName(String name)
    {
        return name.matches("[a-zA-Z]+");
    }
    public static boolean isValidLastName(String name)
    {
        return name.matches("[a-zA-Z]+");
    }


    public void signUp() throws Exception
    {
        Connection con = Database.getCon();
        String firstName;
        do
        {
            System.out.print("Enter First Name : ");
            firstName = sc.next().toLowerCase();
            if (isValidFirstName(firstName))
            {
                System.out.println("Valid first name ✅");
                break;
            }
            else
            {
                System.out.println("Invalid first name ❌ (only letters allowed)");
            }
        } while (true);

        String lastName;
        do
        {
            System.out.print("Enter last Name : ");
            lastName = sc.next().toLowerCase();
            if (isValidLastName(lastName))
            {
                System.out.println("Valid Last name ✅");
                break;
            }
            else
            {
                System.out.println("Invalid last name ❌ (only letters allowed)");
            }

        } while (true);

        String email;
        sc.nextLine();
        do
        {
            System.out.print("Enter your email: ");
            email = sc.nextLine().trim();

            if (isValidEmail(email))
            {
                System.out.println("✅ Valid email!");
                break;
            } else {
                System.out.println("❌ Invalid email! Please enter a valid email address.");
            }
        } while (true);

        String mobileNo;
        do
        {
            System.out.print("Enter your mobile number: ");
            mobileNo = sc.nextLine().trim();

            if (isValidMobileNo(mobileNo))
            {
                System.out.println("✅ Valid mobile No!");
                break;
            }
            else
            {
                System.out.println("❌ Invalid mobile number! It must contain exactly 10 digits.");
            }
        } while (true);

        System.out.print("Enter userName : ");
        String userName = sc.next();

        String password;
        while (true)
        {
            System.out.print("Enter Password : ");
            password = sc.next();
            if (isValidPassword(password))
            {
                break;
            }
            else
            {
                System.out.println("❌ Password must be at least 8 characters long, contain:");
                System.out.println("   → At least one uppercase letter");
                System.out.println("   → At least one digit");
                System.out.println("   → At least one special character (!@#$%^&* etc.)");
            }
        }

        String insertUser = "INSERT INTO users(first_name,last_name,username,mobile_no,email,password,role) VALUES(?,?,?,?,?,?,?)";
        PreparedStatement insertStmt = con.prepareStatement(insertUser);
        try
        {
            insertStmt.setString(1, firstName);
            insertStmt.setString(2, lastName);
            insertStmt.setString(3, userName);
            insertStmt.setString(4, mobileNo);
            insertStmt.setString(5, email);
            insertStmt.setString(6, password);
            insertStmt.setString(7, "user");
            int rows = insertStmt.executeUpdate();
            if (rows > 0)
            {
                ResultSet keys = insertStmt.getGeneratedKeys();
                int newUser = 0;
                if (keys.next())
                {
                    newUser = keys.getInt(1);
                }
                PreparedStatement ps = Database.getCon().prepareStatement("select * from users where user_id = ?");
                ps.setInt(1, newUser);
                ResultSet rss = ps.executeQuery();
                if (rss.next())
                {
                    User.addLoggedInUser(rss);
                }
            }

        }
        catch (Exception e)
        {
//            throw new Exception(e);
        }
        System.out.println("✅ Signed Up Successfully");
    }


    public int userLogin() throws Exception
    {
        Scanner sc = new Scanner(System.in);
        Connection con = Database.getCon();

        System.out.print("Enter userName : ");
        String userName = sc.next();
        System.out.print("Enter Password : ");
        String password = sc.next();

        String fetchUserDetails = "SELECT * FROM users WHERE username = ? AND password = ?";
        PreparedStatement insertStmt = con.prepareStatement(fetchUserDetails);
        try
        {
            insertStmt.setString(1, userName);
            insertStmt.setString(2, password);

            ResultSet rs = insertStmt.executeQuery();
            while (rs.next())
            {
                String fetchedPassword = rs.getString("password");
                if (password.equals(fetchedPassword))
                {
                    System.out.println("✅ Logged In Successfully");
                    User.addLoggedInUser(rs);
                    CustomerManagement.start();
                    return rs.getInt("user_id");
                }
                else
                {
                    System.out.println("❌ Invalid Credentials");
                }
            }
        }
        catch (Exception e)
        {
//            throw new RuntimeException(e);
        }
        return 0;
    }
}