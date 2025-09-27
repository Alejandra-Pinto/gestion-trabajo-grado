package co.unicauca.workflow.domain.entities;

import java.time.LocalDate;
import java.util.List;

public class DegreeWork {
    private String idEstudiante;
    private String idProfesor;
    private String tituloProyecto;
    private Modalidad modalidad; // INVESTIGACION o PRACTICA_PROFESIONAL
    private LocalDate fechaActual;
    private String directorProyecto;
    private String codirectorProyecto; // puede ser opcional
    private String objetivoGeneral;
    private List<String> objetivosEspecificos;
    private String archivoPdf; // ruta del archivo PDF
    private String cartaAceptacionEmpresa; // solo si es práctica profesional
    private EstadoFormatoA estado;
    private int id;
    private int noAprobadoCount; // Nuevo campo para contar intentos fallidos

    // Constructor
    public DegreeWork(String idEstudiante, String idProfesor, String tituloProyecto, Modalidad modalidad,
                    LocalDate fechaActual, String directorProyecto, String codirectorProyecto,
                    String objetivoGeneral, List<String> objetivosEspecificos, String archivoPdf) {
        this.idEstudiante = idEstudiante;
        this.idProfesor = idProfesor;
        this.tituloProyecto = tituloProyecto;
        this.modalidad = modalidad;
        this.fechaActual = fechaActual;
        this.directorProyecto = directorProyecto;
        this.codirectorProyecto = codirectorProyecto;
        this.objetivoGeneral = objetivoGeneral;
        this.objetivosEspecificos = objetivosEspecificos;
        this.archivoPdf = archivoPdf;
        this.estado = EstadoFormatoA.PRIMERA_EVALUACION; // estado inicial
        this.noAprobadoCount = 0; // Inicializar conteo en 0
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getIdEstudiante() { return idEstudiante; }
    public void setIdEstudiante(String idEstudiante) { this.idEstudiante = idEstudiante; }

    public String getIdProfesor() { return idProfesor; }
    public void setIdProfesor(String idProfesor) { this.idProfesor = idProfesor; }

    public String getTituloProyecto() { return tituloProyecto; }
    public void setTituloProyecto(String tituloProyecto) { this.tituloProyecto = tituloProyecto; }

    public Modalidad getModalidad() { return modalidad; }
    public void setModalidad(Modalidad modalidad) { this.modalidad = modalidad; }

    public LocalDate getFechaActual() { return fechaActual; }
    public void setFechaActual(LocalDate fechaActual) { this.fechaActual = fechaActual; }

    public String getDirectorProyecto() { return directorProyecto; }
    public void setDirectorProyecto(String directorProyecto) { this.directorProyecto = directorProyecto; }

    public String getCodirectorProyecto() { return codirectorProyecto; }
    public void setCodirectorProyecto(String codirectorProyecto) { this.codirectorProyecto = codirectorProyecto; }

    public String getObjetivoGeneral() { return objetivoGeneral; }
    public void setObjetivoGeneral(String objetivoGeneral) { this.objetivoGeneral = objetivoGeneral; }

    public List<String> getObjetivosEspecificos() { return objetivosEspecificos; }
    public void setObjetivosEspecificos(List<String> objetivosEspecificos) { this.objetivosEspecificos = objetivosEspecificos; }

    public String getArchivoPdf() { return archivoPdf; }
    public void setArchivoPdf(String archivoPdf) { this.archivoPdf = archivoPdf; }

    public String getCartaAceptacionEmpresa() { return cartaAceptacionEmpresa; }
    public void setCartaAceptacionEmpresa(String cartaAceptacionEmpresa) { 
        if (this.modalidad == Modalidad.PRACTICA_PROFESIONAL) {
            this.cartaAceptacionEmpresa = cartaAceptacionEmpresa;
        }
    }

    public EstadoFormatoA getEstado() { return estado; }
    public void setEstado(EstadoFormatoA estado) { this.estado = estado; }

    public int getNoAprobadoCount() { return noAprobadoCount; }
    public void setNoAprobadoCount(int noAprobadoCount) { this.noAprobadoCount = noAprobadoCount; }
    public void incrementNoAprobadoCount() { this.noAprobadoCount++; } // Método para incrementar el conteo

    @Override
    public String toString() {
        return "FormatoA{" +
                "idEstudiante='" + idEstudiante + '\'' +
                ", idProfesor='" + idProfesor + '\'' +
                ", tituloProyecto='" + tituloProyecto + '\'' +
                ", modalidad=" + modalidad +
                ", fechaActual=" + fechaActual +
                ", directorProyecto='" + directorProyecto + '\'' +
                ", codirectorProyecto='" + codirectorProyecto + '\'' +
                ", objetivoGeneral='" + objetivoGeneral + '\'' +
                ", objetivosEspecificos=" + objetivosEspecificos +
                ", archivoPdf='" + archivoPdf + '\'' +
                ", cartaAceptacionEmpresa='" + cartaAceptacionEmpresa + '\'' +
                ", estado=" + estado +
                ", noAprobadoCount=" + noAprobadoCount +
                '}';
    }
}