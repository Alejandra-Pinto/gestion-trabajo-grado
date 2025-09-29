package co.unicauca.workflow.access;

import co.unicauca.workflow.domain.entities.*;
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
                + "director_proyecto TEXT NOT NULL,"
                + "titulo_proyecto TEXT NOT NULL,"
                + "modalidad TEXT NOT NULL,"
                + "fecha_actual TEXT NOT NULL,"
                + "codirector_proyecto TEXT,"
                + "objetivo_general TEXT NOT NULL,"
                + "objetivos_especificos TEXT,"
                + "archivo_pdf TEXT,"
                + "carta_aceptacion_empresa TEXT,"
                + "estado TEXT NOT NULL,"
                + "correcciones TEXT,"
                + "no_aprobado_count INTEGER DEFAULT 0"
                + ");";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Error creando tabla: " + e.getMessage());
        }
        
    }

    @Override
    public boolean save(DegreeWork formato) {
        String sql = "INSERT INTO degree_work(id_estudiante, titulo_proyecto, modalidad, "
                + "fecha_actual, director_proyecto, codirector_proyecto, objetivo_general, "
                + "objetivos_especificos, archivo_pdf, carta_aceptacion_empresa, estado, correcciones, no_aprobado_count) "
                + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, formato.getEstudiante().getEmail());
            pstmt.setString(2, formato.getTituloProyecto());
            pstmt.setString(3, formato.getModalidad().name());
            pstmt.setString(4, formato.getFechaActual().toString());
            pstmt.setString(5, formato.getDirectorProyecto().getEmail());
            pstmt.setString(6, formato.getCodirectorProyecto() != null ? formato.getCodirectorProyecto().getEmail() : null);
            pstmt.setString(7, formato.getObjetivoGeneral());
            pstmt.setString(8, serializeObjetivos(formato.getObjetivosEspecificos()));
            pstmt.setString(9, formato.getArchivoPdf());
            pstmt.setString(10, formato.getCartaAceptacionEmpresa());
            pstmt.setString(11, formato.getEstado().name());
            pstmt.setString(12, formato.getCorrecciones());
            pstmt.setInt(13, formato.getNoAprobadoCount());

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

    
    public List<DegreeWork> listByTeacher(String teacherEmail) {
        List<DegreeWork> list = new ArrayList<>();
        String sql = "SELECT * FROM degree_work WHERE director_proyecto = ? ORDER BY id";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, teacherEmail);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(buildFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error listando DegreeWork por docente: " + e.getMessage());
        }
        return list;
    }
    
    @Override
    public List<DegreeWork> listByStudentAndModalidad(String studentEmail, Modalidad modalidad) {
        List<DegreeWork> list = new ArrayList<>();
        String sql = "SELECT * FROM degree_work WHERE id_estudiante = ? AND modalidad = ? ORDER BY id";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentEmail);
            pstmt.setString(2, modalidad.name());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(buildFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error listando DegreeWork por estudiante y modalidad: " + e.getMessage());
        }
        return list;
    }



    @Override
    public boolean update(DegreeWork formato) {
        String sql = "UPDATE degree_work SET "
                + "id_estudiante=?, titulo_proyecto=?, modalidad=?, fecha_actual=?, "
                + "director_proyecto=?, codirector_proyecto=?, objetivo_general=?, objetivos_especificos=?, "
                + "archivo_pdf=?, carta_aceptacion_empresa=?, estado=?, correcciones=?, no_aprobado_count=? "
                + "WHERE id=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, formato.getEstudiante().getEmail());
            pstmt.setString(2, formato.getTituloProyecto());
            pstmt.setString(3, formato.getModalidad().name());
            pstmt.setString(4, formato.getFechaActual().toString());
            pstmt.setString(5, formato.getDirectorProyecto().getEmail());
            pstmt.setString(6, formato.getCodirectorProyecto() != null ? formato.getCodirectorProyecto().getEmail() : null);
            pstmt.setString(7, formato.getObjetivoGeneral());
            pstmt.setString(8, serializeObjetivos(formato.getObjetivosEspecificos()));
            pstmt.setString(9, formato.getArchivoPdf());
            pstmt.setString(10, formato.getCartaAceptacionEmpresa());
            pstmt.setString(11, formato.getEstado().name());
            pstmt.setString(12, formato.getCorrecciones());
            pstmt.setInt(13, formato.getNoAprobadoCount());
            pstmt.setInt(14, formato.getId());
            

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

    @Override
    public DegreeWork findLatestByStudent(String studentEmail) {
        String sql = "SELECT * FROM degree_work WHERE id_estudiante = ? ORDER BY id DESC LIMIT 1";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentEmail);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return buildFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error consultando última versión: " + e.getMessage());
        }
        return null;
    }

    private DegreeWork buildFromResultSet(ResultSet rs) throws SQLException {
        Student estudiante = new Student();
        estudiante.setEmail(rs.getString("id_estudiante"));

        Teacher director = new Teacher();
        director.setEmail(rs.getString("director_proyecto"));

        Teacher codirector = null;
        String codirectorEmail = rs.getString("codirector_proyecto");
        if (codirectorEmail != null && !codirectorEmail.isEmpty()) {
            codirector = new Teacher();
            codirector.setEmail(codirectorEmail);
        }

        DegreeWork f = new DegreeWork(
                estudiante,
                director,
                rs.getString("titulo_proyecto"),
                Modalidad.valueOf(rs.getString("modalidad")),
                LocalDate.parse(rs.getString("fecha_actual")),
                codirector,
                rs.getString("objetivo_general"),
                deserializeObjetivos(rs.getString("objetivos_especificos")),
                rs.getString("archivo_pdf")
        );
        f.setId(rs.getInt("id"));
        f.setEstado(EstadoFormatoA.valueOf(rs.getString("estado")));
        f.setCartaAceptacionEmpresa(rs.getString("carta_aceptacion_empresa"));
        f.setCorrecciones(rs.getString("correcciones"));
        f.setNoAprobadoCount(rs.getInt("no_aprobado_count"));
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