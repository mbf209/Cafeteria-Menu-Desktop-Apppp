package users;

import database.DBConnection;
import models.User;

import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class UserDAO {

    public boolean register(String username, String password) {
        try (Connection conn = DBConnection.getConnection()) {
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            String query = "INSERT INTO users (username, password_hash) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User login(String username, String password) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                if (BCrypt.checkpw(password, storedHash)) {
                    return new User(rs.getInt("id"), rs.getString("username"), storedHash);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
