package co.unicauca.workflow.access;

import co.unicauca.workflow.domain.entities.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserSQLiteRepository implements IUsersRepository {
    private Connection conn;

    public UserSQLiteRepository() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:workflow.db");
            createUsersTableIfNotExists();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void createUsersTableIfNotExists() throws SQLException {
        String sqlUsers = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "firstName TEXT NOT NULL," +
                "lastName TEXT NOT NULL," +
                "phone TEXT," +
                "program TEXT NOT NULL," +
                "email TEXT UNIQUE NOT NULL," +
                "password TEXT NOT NULL," +
                "role TEXT NOT NULL)";
        Statement stmt1 = conn.createStatement();
        stmt1.execute(sqlUsers);
    }

    @Override
    public boolean save(User user) {
        String sql = "INSERT INTO users(firstName, lastName, phone, program, email, password, role) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getFirstName());
            pstmt.setString(2, user.getLastName());
            pstmt.setString(3, user.getPhone());
            pstmt.setString(4, user.getProgram());
            pstmt.setString(5, user.getEmail());
            pstmt.setString(6, user.getPassword());
            pstmt.setString(7, user.getRole());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error guardando usuario: " + e.getMessage());
            return false;
        }
    }

    @Override
    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return buildUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error consultando usuario: " + e.getMessage());
        }
        return null;
    }

    @Override
    public User login(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return buildUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error en login: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<User> listAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(buildUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error listando usuarios: " + e.getMessage());
        }
        return users;
    }

    private User buildUserFromResultSet(ResultSet rs) throws SQLException {
        String role = rs.getString("role");
        if ("STUDENT".equalsIgnoreCase(role)) {
            return new Student(
                    rs.getString("firstName"),
                    rs.getString("lastName"),
                    rs.getString("phone"),
                    rs.getString("program"),
                    rs.getString("email"),
                    rs.getString("password")
            );
        } else {
            return new Teacher(
                    rs.getString("firstName"),
                    rs.getString("lastName"),
                    rs.getString("phone"),
                    rs.getString("program"),
                    rs.getString("email"),
                    rs.getString("password")
            );
        }
    }
}
