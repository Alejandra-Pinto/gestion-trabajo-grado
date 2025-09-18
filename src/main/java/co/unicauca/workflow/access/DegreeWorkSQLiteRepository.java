package co.unicauca.workflow.access;

import co.unicauca.workflow.domain.entities.DegreeWork;
import co.unicauca.workflow.domain.entities.EstadoFormatoA;
import co.unicauca.workflow.domain.entities.Modalidad;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DegreeWorkSQLiteRepository implements IDegreeWorkRepository {

    private Connection conn;

    public DegreeWorkSQLiteRepository() {
        connect();
        createTableIfNotExists();
    }

    private void connect() {
        try {
            String url = "jdbc:sqlite:workflow.db";
            conn = DriverManager.getConnection(url);
            System.out.println("Conexión a SQLite establecida.");
        } catch (SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS degree_work ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "id_estudiante TEXT NOT NULL,"
                + "id_profesor TEXT NOT NULL,"
                + "titulo_proyecto TEXT NOT NULL,"
                + "modalidad TEXT NOT NULL,"
                + "fecha_actual TEXT NOT NULL,"
                + "director_proyecto TEXT NOT NULL,"
                + "codirector_proyecto TEXT,"
                + "objetivo_general TEXT NOT NULL,"
                + "objetivos_especificos TEXT,"
                + "archivo_pdf TEXT,"
                + "carta_aceptacion_empresa TEXT,"
                + "estado TEXT NOT NULL"
                + ");";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Error creando tabla: " + e.getMessage());
        }
    }

    @Override
    public boolean save(DegreeWork formato) {
        String sql = "INSERT INTO degree_work(id_estudiante, id_profesor, titulo_proyecto, modalidad, "
                   + "fecha_actual, director_proyecto, codirector_proyecto, objetivo_general, "
                   + "objetivos_especificos, archivo_pdf, carta_aceptacion_empresa, estado) "
                   + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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

            int affected = pstmt.executeUpdate();
            if (affected == 0) return false;

            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    formato.setId(keys.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            System.out.println("Error guardando DegreeWork: " + e.getMessage());
            return false;
        }
    }

    @Override
    public DegreeWork findById(int id) {
        String sql = "SELECT * FROM degree_work WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return buildFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error consultando DegreeWork: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<DegreeWork> listAllDegreeWork() {
        List<DegreeWork> list = new ArrayList<>();
        String sql = "SELECT * FROM degree_work ORDER BY id";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(buildFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error listando DegreeWork: " + e.getMessage());
        }
        return list;
    }

    @Override
    public boolean update(DegreeWork formato) {
        String sql = "UPDATE degree_work SET "
                + "id_estudiante=?, id_profesor=?, titulo_proyecto=?, modalidad=?, fecha_actual=?, "
                + "director_proyecto=?, codirector_proyecto=?, objetivo_general=?, objetivos_especificos=?, "
                + "archivo_pdf=?, carta_aceptacion_empresa=?, estado=? "
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
            System.out.println("Error actualizando DegreeWork: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM degree_work WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error eliminando DegreeWork: " + e.getMessage());
            return false;
        }
    }

    // Helpers
    private DegreeWork buildFromResultSet(ResultSet rs) throws SQLException {
        DegreeWork f = new DegreeWork(
                rs.getString("id_estudiante"),
                rs.getString("id_profesor"),
                rs.getString("titulo_proyecto"),
                Modalidad.valueOf(rs.getString("modalidad")),
                LocalDate.parse(rs.getString("fecha_actual")),
                rs.getString("director_proyecto"),
                rs.getString("codirector_proyecto"),
                rs.getString("objetivo_general"),
                deserializeObjetivos(rs.getString("objetivos_especificos")),
                rs.getString("archivo_pdf")
        );
        f.setId(rs.getInt("id"));
        f.setEstado(EstadoFormatoA.valueOf(rs.getString("estado")));
        f.setCartaAceptacionEmpresa(rs.getString("carta_aceptacion_empresa"));
        return f;
    }

    private String serializeObjetivos(List<String> objetivos) {
        if (objetivos == null || objetivos.isEmpty()) return "";
        return String.join("||", objetivos);
    }

    private List<String> deserializeObjetivos(String raw) {
        if (raw == null || raw.isEmpty()) return new ArrayList<>();
        String[] parts = raw.split("\\|\\|");
        List<String> list = new ArrayList<>();
        for (String p : parts) list.add(p);
        return list;
    }
}
