package Database;
import java.sql.*;

public class Database
{
    static Connection con = null;
    static public Connection getCon() throws Exception
    {
        String dburl = "jdbc:mysql://localhost:3306/shopping_system";
        String dbuser = "root";
        String dbpass = "";
        String driver = "com.mysql.cj.jdbc.Driver";
        Class.forName(driver);
        con = DriverManager.getConnection(dburl,dbuser,dbpass);
        return con;
    }
}
