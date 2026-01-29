package persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Edit these defaults or provide a config file at runtime.
    private static final String URL = "jdbc:mysql://localhost:3306/clinica?serverTimezone=UTC&useSSL=false";
    private static final String USER = "root";
    private static final String PASS = ""; // set your DB password

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // Driver not found; user must add MySQL Connector/J to classpath
            System.err.println("MySQL JDBC driver not found. Add the connector JAR to classpath.");
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static boolean isAvailable() {
        try (Connection c = getConnection()) {
            return c != null && !c.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
    
    public static void main(String[] args) {
		if (isAvailable()) {
			System.out.println("Database connection is available.");
		} else {
			System.out.println("Database connection is NOT available.");
		}
	}
}
