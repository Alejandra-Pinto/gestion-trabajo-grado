package co.unicauca.workflow.domain.entities;

import java.time.LocalDate;
import java.util.List;

public class DegreeWork {
    private Student estudiante;
    private Teacher directorProyecto;
    private Teacher codirectorProyecto; // opcional
    private String tituloProyecto;
    private Modalidad modalidad; // INVESTIGACION o PRACTICA_PROFESIONAL
    private LocalDate fechaActual;
    private String objetivoGeneral;
    private List<String> objetivosEspecificos;
    private String archivoPdf; // ruta del archivo PDF
    private String cartaAceptacionEmpresa; // solo si es práctica profesional
    private EstadoFormatoA estado;
    private String correcciones;
    private int id;
    private int noAprobadoCount; // Nuevo campo para contar intentos fallidos

    // Constructor
    public DegreeWork(Student estudiante,
            Teacher directorProyecto,
            String tituloProyecto,
            Modalidad modalidad,
            LocalDate fechaActual,
            Teacher codirectorProyecto,
            String objetivoGeneral,
            List<String> objetivosEspecificos,
            String archivoPdf) {
        this.estudiante = estudiante;
        this.directorProyecto = directorProyecto;
        this.tituloProyecto = tituloProyecto;
        this.modalidad = modalidad;
        this.fechaActual = fechaActual;
        this.codirectorProyecto = codirectorProyecto;
        this.objetivoGeneral = objetivoGeneral;
        this.objetivosEspecificos = objetivosEspecificos;
        this.archivoPdf = archivoPdf;

        // Valores por defecto
        this.estado = EstadoFormatoA.PRIMERA_EVALUACION;
        this.correcciones = "";
    }


    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Student getEstudiante() { return estudiante; }
    public void setEstudiante(Student estudiante) { this.estudiante = estudiante; }

    public Teacher getDirectorProyecto() { return directorProyecto; }
    public void setDirectorProyecto(Teacher directorProyecto) { this.directorProyecto = directorProyecto; }

    public Teacher getCodirectorProyecto() { return codirectorProyecto; }
    public void setCodirectorProyecto(Teacher codirectorProyecto) { this.codirectorProyecto = codirectorProyecto; }

    public String getTituloProyecto() { return tituloProyecto; }
    public void setTituloProyecto(String tituloProyecto) { this.tituloProyecto = tituloProyecto; }

    public Modalidad getModalidad() { return modalidad; }
    public void setModalidad(Modalidad modalidad) { this.modalidad = modalidad; }

    public LocalDate getFechaActual() { return fechaActual; }
    public void setFechaActual(LocalDate fechaActual) { this.fechaActual = fechaActual; }

    public String getObjetivoGeneral() { return objetivoGeneral; }
    public void setObjetivoGeneral(String objetivoGeneral) { this.objetivoGeneral = objetivoGeneral; }

    public List<String> getObjetivosEspecificos() { return objetivosEspecificos; }
    public void setObjetivosEspecificos(List<String> objetivosEspecificos) { this.objetivosEspecificos = objetivosEspecificos; }

    public String getArchivoPdf() { return archivoPdf; }
    public void setArchivoPdf(String archivoPdf) { this.archivoPdf = archivoPdf; }

    public String getCorrecciones() { return correcciones; }
    public void setCorrecciones(String correcciones) { this.correcciones = correcciones; }

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
    public void incrementNoAprobadoCount() { this.noAprobadoCount++; }

    @Override
    public String toString() {
        return "DegreeWork{" +
                "id=" + id +
                ", estudiante=" + (estudiante != null ? estudiante.getFirstName() + " " +  estudiante.getLastName() : "null") +
                ", director=" + (directorProyecto != null ? directorProyecto.getFirstName() + " " +  directorProyecto.getLastName()  : "null") +
                ", codirector=" + (codirectorProyecto != null ? codirectorProyecto.getFirstName() + " " +  codirectorProyecto.getLastName()  : "null") +
                ", tituloProyecto='" + tituloProyecto + '\'' +
                ", modalidad=" + modalidad +
                ", fechaActual=" + fechaActual +
                ", objetivoGeneral='" + objetivoGeneral + '\'' +
                ", archivoPdf='" + archivoPdf + '\'' +
                ", cartaAceptacionEmpresa='" + cartaAceptacionEmpresa + '\'' +
                ", estado=" + estado +
                ", noAprobadoCount=" + noAprobadoCount +
                ", correcciones='" + correcciones + '\'' +
                '}';
    }
}
