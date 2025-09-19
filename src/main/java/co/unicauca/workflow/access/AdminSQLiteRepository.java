package co.unicauca.workflow.access;

import co.unicauca.workflow.domain.entities.SuperAdmin;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminSQLiteRepository implements IAdminRepository {
    private Connection conn;

    public AdminSQLiteRepository() {
        try {
            String url = "jdbc:sqlite:workflow.db";
            conn = DriverManager.getConnection(url);
            // crear tabla si no existe
            createAdminsTableIfNotExists();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void createAdminsTableIfNotExists() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS super_admins (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "firstName TEXT NOT NULL," +
                "lastName TEXT NOT NULL," +
                "phone TEXT," +
                "email TEXT UNIQUE NOT NULL," +
                "password TEXT NOT NULL)";
        Statement stmt = conn.createStatement();
        stmt.execute(sql);
    }

    @Override
    public boolean save(SuperAdmin admin) {
        String sql = "INSERT INTO super_admins(firstName, lastName, phone, email, password) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, admin.getFirstName());
            pstmt.setString(2, admin.getLastName());
            pstmt.setString(3, admin.getPhone());
            pstmt.setString(4, admin.getEmail());
            pstmt.setString(5, admin.getPassword());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error guardando admin: " + e.getMessage());
            return false;
        }
    }

    @Override
    public SuperAdmin findByEmail(String email) {
        String sql = "SELECT * FROM super_admins WHERE email = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new SuperAdmin(
                        rs.getInt("id"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("password")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error consultando admin: " + e.getMessage());
        }
        return null;
    }

    @Override
    public SuperAdmin login(String email, String password) {
        String sql = "SELECT * FROM super_admins WHERE email = ? AND password = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new SuperAdmin(
                        rs.getInt("id"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("password")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error en login admin: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<SuperAdmin> listAll() {
        List<SuperAdmin> admins = new ArrayList<>();
        String sql = "SELECT * FROM super_admins";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                admins.add(new SuperAdmin(
                        rs.getInt("id"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("password")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error listando admins: " + e.getMessage());
        }
        return admins;
    }
    
}
