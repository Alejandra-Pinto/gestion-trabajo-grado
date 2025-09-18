package co.unicauca.workflow.access;

import co.unicauca.workflow.domain.entities.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLiteRepository implements IUsersRepository, IDegreeWorkRepository {
    private Connection conn;

    public SQLiteRepository() {
        try {
            // Conexión a SQLite
            conn = DriverManager.getConnection("jdbc:sqlite:workflow.db");
            createTablesIfNotExists();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void createTablesIfNotExists() throws SQLException {
        // Tabla de usuarios
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

        // Tabla de formato_a
        String sqlFormatoA = "CREATE TABLE IF NOT EXISTS formato_a ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "idEstudiante TEXT NOT NULL,"
                + "idProfesor TEXT NOT NULL,"
                + "tituloProyecto TEXT NOT NULL,"
                + "modalidad TEXT NOT NULL,"
                + // Enum como texto
                "fechaActual TEXT NOT NULL,"
                + // Guardado como ISO-8601 (LocalDate.toString)
                "directorProyecto TEXT NOT NULL,"
                + "codirectorProyecto TEXT,"
                + // Opcional
                "objetivoGeneral TEXT NOT NULL,"
                + "objetivosEspecificos TEXT,"
                + // Serializado (ver helpers abajo)
                "archivoPdf TEXT NOT NULL,"
                + "cartaAceptacionEmpresa TEXT,"
                + // Opcional
                "estado TEXT NOT NULL"
                + // Enum como texto
                ")";
        Statement stmtFormatoA = conn.createStatement();
        stmtFormatoA.execute(sqlFormatoA);

    }

    /* ======================= MÉTODOS DE USUARIOS ======================= */

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


    /* ======================= MÉTODOS DE FORMATO A ======================= */
    @Override
    public boolean save(DegreeWork formato) {
        String sql = "INSERT INTO formato_a("
                + "idEstudiante, idProfesor, tituloProyecto, modalidad, fechaActual, "
                + "directorProyecto, codirectorProyecto, objetivoGeneral, objetivosEspecificos, "
                + "archivoPdf, cartaAceptacionEmpresa, estado"
                + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, formato.getIdEstudiante());
            pstmt.setString(2, formato.getIdProfesor());
            pstmt.setString(3, formato.getTituloProyecto());
            pstmt.setString(4, formato.getModalidad().name());
            pstmt.setString(5, formato.getFechaActual().toString()); // ISO-8601 (yyyy-MM-dd)
            pstmt.setString(6, formato.getDirectorProyecto());
            pstmt.setString(7, formato.getCodirectorProyecto());      // puede ser null
            pstmt.setString(8, formato.getObjetivoGeneral());
            pstmt.setString(9, serializeObjetivos(formato.getObjetivosEspecificos())); // TEXT serializado
            pstmt.setString(10, formato.getArchivoPdf());
            pstmt.setString(11, formato.getCartaAceptacionEmpresa());  // puede ser null
            pstmt.setString(12, formato.getEstado().name());

            int affected = pstmt.executeUpdate();
            if (affected == 0) {
                return false;
            }

            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    formato.setId(keys.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            System.out.println("Error guardando FormatoA: " + e.getMessage());
            return false;
        }
    }

    @Override
    public DegreeWork findById(int id) {
        String sql = "SELECT * FROM formato_a WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return buildFormatoAFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error consultando FormatoA: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<DegreeWork> listAllFormatoA() {
        List<DegreeWork> list = new ArrayList<>();
        String sql = "SELECT * FROM formato_a ORDER BY id";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(buildFormatoAFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error listando FormatoA: " + e.getMessage());
        }
        return list;
    }

    @Override
    public boolean update(DegreeWork formato) {
        String sql = "UPDATE formato_a SET "
                + "idEstudiante=?, idProfesor=?, tituloProyecto=?, modalidad=?, fechaActual=?, "
                + "directorProyecto=?, codirectorProyecto=?, objetivoGeneral=?, objetivosEspecificos=?, "
                + "archivoPdf=?, cartaAceptacionEmpresa=?, estado=? "
                + "WHERE id=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, formato.getIdEstudiante());
            pstmt.setString(2, formato.getIdProfesor());
            pstmt.setString(3, formato.getTituloProyecto());
            pstmt.setString(4, formato.getModalidad().name());
            pstmt.setString(5, formato.getFechaActual().toString());
            pstmt.setString(6, formato.getDirectorProyecto());
            pstmt.setString(7, formato.getCodirectorProyecto());
            pstmt.setString(8, formato.getObjetivoGeneral());
            pstmt.setString(9, serializeObjetivos(formato.getObjetivosEspecificos()));
            pstmt.setString(10, formato.getArchivoPdf());
            pstmt.setString(11, formato.getCartaAceptacionEmpresa());
            pstmt.setString(12, formato.getEstado().name());
            pstmt.setInt(13, formato.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error actualizando FormatoA: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM formato_a WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error eliminando FormatoA: " + e.getMessage());
            return false;
        }
    }

    private DegreeWork buildFormatoAFromResultSet(ResultSet rs) throws SQLException {
        // Construye con el constructor existente
        DegreeWork f = new DegreeWork(
                rs.getString("idEstudiante"),
                rs.getString("idProfesor"),
                rs.getString("tituloProyecto"),
                Modalidad.valueOf(rs.getString("modalidad")),
                java.time.LocalDate.parse(rs.getString("fechaActual")),
                rs.getString("directorProyecto"),
                rs.getString("codirectorProyecto"),
                rs.getString("objetivoGeneral"),
                deserializeObjetivos(rs.getString("objetivosEspecificos")),
                rs.getString("archivoPdf")
        );
        // id y estado desde BD
        f.setId(rs.getInt("id"));
        f.setEstado(EstadoFormatoA.valueOf(rs.getString("estado")));
        // carta (se respeta tu setter: solo se guarda si modalidad es PRACTICA_PROFESIONAL)
        f.setCartaAceptacionEmpresa(rs.getString("cartaAceptacionEmpresa"));
        return f;
    }
    private String serializeObjetivos(List<String> objetivos) {
        if (objetivos == null || objetivos.isEmpty()) {
            return null;
        }
        return String.join("||", objetivos); // separador simple y seguro
    }

    private List<String> deserializeObjetivos(String raw) {
        if (raw == null || raw.isEmpty()) {
            return new ArrayList<>();
        }
        String[] parts = raw.split("\\|\\|");
        List<String> list = new ArrayList<>();
        for (String p : parts) {
            if (p != null) {
                list.add(p);
            }
        }
        return list;
    }

}
