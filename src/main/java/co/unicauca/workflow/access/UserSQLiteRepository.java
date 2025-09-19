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
            addStatusColumnIfNotExists(); // Agregar columna status si falta
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // 🔹 Crear tabla si no existe
    private void createUsersTableIfNotExists() throws SQLException {
        String sqlUsers = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "firstName TEXT NOT NULL," +
                "lastName TEXT NOT NULL," +
                "phone TEXT," +
                "program TEXT NOT NULL," +
                "email TEXT UNIQUE NOT NULL," +
                "password TEXT NOT NULL," +
                "role TEXT NOT NULL," +
                "status TEXT DEFAULT 'ACEPTADO')"; // Nueva columna
        Statement stmt1 = conn.createStatement();
        stmt1.execute(sqlUsers);
    }

    // Intentar agregar columna status si no existe
    private void addStatusColumnIfNotExists() {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE users ADD COLUMN status TEXT DEFAULT 'ACEPTADO'");
        } catch (SQLException e) {
            // Si ya existe, ignoramos el error
            if (!e.getMessage().contains("duplicate column name")) {
                System.out.println("Error agregando columna status: " + e.getMessage());
            }
        }
    }

    @Override
    public boolean save(User user) {
        String sql = "INSERT INTO users(firstName, lastName, phone, program, email, password, role, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getFirstName());
            pstmt.setString(2, user.getLastName());
            pstmt.setString(3, user.getPhone());
            pstmt.setString(4, user.getProgram());
            pstmt.setString(5, user.getEmail());
            pstmt.setString(6, user.getPassword());
            pstmt.setString(7, user.getRole());
            pstmt.setString(8, user.getStatus());
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
                // Validar login de coordinador (solo si está aceptado)
                if ("COORDINATOR".equalsIgnoreCase(rs.getString("role")) &&
                    !"ACEPTADO".equalsIgnoreCase(rs.getString("status"))) {
                    System.out.println("El coordinador aún no está aceptado.");
                    return null;
                }
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
        String status = rs.getString("status");

        if ("STUDENT".equalsIgnoreCase(role)) {
            Student s = new Student(
                    rs.getString("firstName"),
                    rs.getString("lastName"),
                    rs.getString("phone"),
                    rs.getString("program"),
                    rs.getString("email"),
                    rs.getString("password")
            );
            s.setStatus(status);
            return s;
        } else if ("COORDINATOR".equalsIgnoreCase(role)) {
            Coordinator c = new Coordinator(
                    rs.getString("firstName"),
                    rs.getString("lastName"),
                    rs.getString("phone"),
                    rs.getString("program"),
                    rs.getString("email"),
                    rs.getString("password")
            );
            c.setStatus(status);
            return c;
        } else { // TEACHER
            Teacher t = new Teacher(
                    rs.getString("firstName"),
                    rs.getString("lastName"),
                    rs.getString("phone"),
                    rs.getString("program"),
                    rs.getString("email"),
                    rs.getString("password")
            );
            t.setStatus(status);
            return t;
        }
    }
}
