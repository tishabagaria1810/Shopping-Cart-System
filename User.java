package Modules.Users;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class User
{
    private static User currentUser = null;
    public static HashMap<Integer, User> loggedInUser = new HashMap<>();

    // Instance variables
    private int user_id;
    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private String mobileNo;
    private String email;
    private String role;

    public User(int user_id, String firstName, String lastName, String userName,
                String password, String email, String mobileNo, String role) {
        this.user_id = user_id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
        this.mobileNo = mobileNo;
        this.email = email;
        this.role = role;

    }

    // Create and log in the user from DB data
    public static void addLoggedInUser(ResultSet userData) throws SQLException
    {
        User loggedUser = new User(
                userData.getInt("user_id"),
                userData.getString("first_name"),
                userData.getString("last_name"),
                userData.getString("username"),
                userData.getString("password"),
                userData.getString("email"),
                userData.getString("mobile_no"),
                userData.getString("role")
        );
        loggedInUser.put(loggedUser.getUserId(), loggedUser);
        setCurrentUser(loggedUser);
    }

    public static void setCurrentUser(User loggeduser)
    {
        currentUser = loggeduser;
        loggedInUser.put(loggeduser.getUserId(), loggeduser);
    }

    public static User getCurrentUser()
    {
        return currentUser;
    }

    public static User getUserById(int id) {
        return loggedInUser.get(id);
    }

    public int getUserId() { return user_id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPassword() { return password; }
    public String getUserName() { return userName; }
    public String getMobileNo() { return mobileNo; }
    public String getEmail() { return email; }
    public String getRole() { return role; }

    public void setFirstName(String newFirstName) {
        this.firstName = newFirstName;
    }

    public void setLastName(String newLastName) {
        this.lastName = newLastName;
    }

    public void setPassword(String newPassword) {
        this.password = password;
    }

    public void setUserName(String newUserName) {
        this.userName = newUserName;
    }
    public void setMobileNo(String newmobileno) {
        this.mobileNo = newmobileno;
    }
}