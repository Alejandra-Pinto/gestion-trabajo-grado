package co.unicauca.workflow.domain.entities;

import java.time.LocalDate;
import java.util.List;

public class DegreeWork {
    private Student estudiante;
    private Teacher directorProyecto;
    private Teacher codirectorProyecto; // opcional
    private String tituloProyecto;
    private Modalidad modalidad;
    private LocalDate fechaActual;
    private String objetivoGeneral;
    private List<String> objetivosEspecificos;
    private String archivoPdf;
    private String cartaAceptacionEmpresa; // solo si es práctica profesional
    private EstadoFormatoA estado;
    private String correcciones;
    private int id;
    private int noAprobadoCount;

    // Constructor con validaciones (usa setters para reutilizar lógica)
    public DegreeWork(Student estudiante,
                      Teacher directorProyecto,
                      String tituloProyecto,
                      Modalidad modalidad,
                      LocalDate fechaActual,
                      Teacher codirectorProyecto,
                      String objetivoGeneral,
                      List<String> objetivosEspecificos,
                      String archivoPdf) {

        setEstudiante(estudiante);
        setDirectorProyecto(directorProyecto);
        setTituloProyecto(tituloProyecto);
        setModalidad(modalidad);
        setFechaActual(fechaActual);
        setCodirectorProyecto(codirectorProyecto); // opcional
        setObjetivoGeneral(objetivoGeneral);
        setObjetivosEspecificos(objetivosEspecificos);
        setArchivoPdf(archivoPdf);

        // Valores por defecto
        this.estado = EstadoFormatoA.PRIMERA_EVALUACION;
        this.correcciones = "";
    }

    // Getters y Setters con validación
    public int getId() { return id; }
    public void setId(int id) {
        if (id < 0) throw new IllegalArgumentException("El id no puede ser negativo.");
        this.id = id;
    }

    public Student getEstudiante() { return estudiante; }
    public void setEstudiante(Student estudiante) {
        if (estudiante == null) throw new IllegalArgumentException("El estudiante no puede ser nulo.");
        this.estudiante = estudiante;
    }

    public Teacher getDirectorProyecto() { return directorProyecto; }
    public void setDirectorProyecto(Teacher directorProyecto) {
        if (directorProyecto == null) throw new IllegalArgumentException("El director no puede ser nulo.");
        this.directorProyecto = directorProyecto;
    }

    public Teacher getCodirectorProyecto() { return codirectorProyecto; }
    public void setCodirectorProyecto(Teacher codirectorProyecto) {
        this.codirectorProyecto = codirectorProyecto; // opcional
    }

    public String getTituloProyecto() { return tituloProyecto; }
    public void setTituloProyecto(String tituloProyecto) {
        if (tituloProyecto == null || tituloProyecto.trim().isEmpty())
            throw new IllegalArgumentException("El título del proyecto no puede estar vacío.");
        this.tituloProyecto = tituloProyecto;
    }

    public Modalidad getModalidad() { return modalidad; }
    public void setModalidad(Modalidad modalidad) {
        if (modalidad == null) throw new IllegalArgumentException("La modalidad no puede ser nula.");
        this.modalidad = modalidad;
    }

    public LocalDate getFechaActual() { return fechaActual; }
    public void setFechaActual(LocalDate fechaActual) {
        if (fechaActual == null || fechaActual.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("La fecha no puede ser futura ni nula.");
        this.fechaActual = fechaActual;
    }

    public String getObjetivoGeneral() { return objetivoGeneral; }
    public void setObjetivoGeneral(String objetivoGeneral) {
        if (objetivoGeneral == null || objetivoGeneral.trim().isEmpty())
            throw new IllegalArgumentException("El objetivo general no puede estar vacío.");
        this.objetivoGeneral = objetivoGeneral;
    }

    public List<String> getObjetivosEspecificos() { return objetivosEspecificos; }
    public void setObjetivosEspecificos(List<String> objetivosEspecificos) {
        if (objetivosEspecificos == null || objetivosEspecificos.isEmpty())
            throw new IllegalArgumentException("Debe haber al menos un objetivo específico.");
        this.objetivosEspecificos = objetivosEspecificos;
    }

    public String getArchivoPdf() { return archivoPdf; }
    public void setArchivoPdf(String archivoPdf) {
        if (archivoPdf == null || !archivoPdf.toLowerCase().endsWith(".pdf"))
            throw new IllegalArgumentException("El archivo debe ser un PDF válido.");
        this.archivoPdf = archivoPdf;
    }

    public String getCorrecciones() { return correcciones; }
    public void setCorrecciones(String correcciones) { this.correcciones = correcciones; }

    public String getCartaAceptacionEmpresa() { return cartaAceptacionEmpresa; }
    public void setCartaAceptacionEmpresa(String cartaAceptacionEmpresa) {
        if (this.modalidad == Modalidad.PRACTICA_PROFESIONAL) {
            if (cartaAceptacionEmpresa == null || cartaAceptacionEmpresa.trim().isEmpty())
                throw new IllegalArgumentException("La carta de aceptación es obligatoria en modalidad práctica profesional.");
            this.cartaAceptacionEmpresa = cartaAceptacionEmpresa;
        } else {
            this.cartaAceptacionEmpresa = null; // en investigación no aplica
        }
    }

    public EstadoFormatoA getEstado() { return estado; }
    public void setEstado(EstadoFormatoA estado) {
        if (estado == null) throw new IllegalArgumentException("El estado no puede ser nulo.");
        this.estado = estado;
    }

    public int getNoAprobadoCount() { return noAprobadoCount; }
    public void setNoAprobadoCount(int noAprobadoCount) {
        if (noAprobadoCount < 0) throw new IllegalArgumentException("El contador de no aprobados no puede ser negativo.");
        this.noAprobadoCount = noAprobadoCount;
    }
    public void incrementNoAprobadoCount() { this.noAprobadoCount++; }

    @Override
    public String toString() {
        return "DegreeWork{" +
                "id=" + id +
                ", estudiante=" + (estudiante != null ? estudiante.getFirstName() + " " + estudiante.getLastName() : "null") +
                ", director=" + (directorProyecto != null ? directorProyecto.getFirstName() + " " + directorProyecto.getLastName() : "null") +
                ", codirector=" + (codirectorProyecto != null ? codirectorProyecto.getFirstName() + " " + codirectorProyecto.getLastName() : "null") +
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
