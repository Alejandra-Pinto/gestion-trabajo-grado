package co.unicauca.workflow.service;

import co.unicauca.workflow.domain.entities.*;
import co.unicauca.workflow.infra.Subject;
import java.util.List;
import co.unicauca.workflow.access.IDegreeWorkRepository;

public class DegreeWorkService extends Subject {
    private final IDegreeWorkRepository repository;

    public DegreeWorkService(IDegreeWorkRepository repository) {
        this.repository = repository;
    }

    // Registrar un nuevo FormatoA
    public boolean registrarFormato(DegreeWork formato) {
        return repository.save(formato);
    }

    // Obtener un FormatoA por id
    public DegreeWork obtenerFormato(int id) {
        return repository.findById(id);
    }

    // Listar todos los formatos
    public List<DegreeWork> listarDegreeWorks() { 
        return repository.listAllDegreeWork(); 
    }

    // método para listar por docente
    public List<DegreeWork> listarDegreeWorksPorDocente(String teacherEmail) {
        return repository.listByTeacher(teacherEmail);
    }
    
    public DegreeWork obtenerUltimoPorEstudianteYModalidad(String studentEmail, Modalidad modalidad) {
        return repository.findLatestByStudentAndModalidad(studentEmail, modalidad);
    }


    // Actualizar un formato
    public boolean actualizarFormato(DegreeWork formato) {
        
        aplicarLogicaEstados(formato);
        boolean updated = repository.update(formato);
        if (updated) {
            this.notifyAllObserves();
        }
        return updated;
    }

    // Eliminar un formato
    public boolean eliminarFormato(int id) {
        return repository.delete(id);
    }

    // Cambiar estado
    public boolean cambiarEstado(int id, EstadoFormatoA nuevoEstado) {
        DegreeWork formato = repository.findById(id);
        if (formato != null) {
            formato.setEstado(nuevoEstado);
            // Incrementar noAprobadoCount si es NO_APROBADO
            if (nuevoEstado == EstadoFormatoA.NO_ACEPTADO) {
                formato.incrementNoAprobadoCount();
            }
            aplicarLogicaEstados(formato); // Verificar si pasa a rechazado
            boolean updated = repository.update(formato);
            if (updated) {
                this.notifyAllObserves(); 
            }
            return updated;
        }
        return false;
    }

    // Avanzar evaluación
    public boolean avanzarEvaluacion(int id) {
        DegreeWork formato = repository.findById(id);
        if (formato == null) {
            return false;
        }

        switch (formato.getEstado()) {
            case PRIMERA_EVALUACION:
                formato.setEstado(EstadoFormatoA.SEGUNDA_EVALUACION);
                break;
            case SEGUNDA_EVALUACION:
                formato.setEstado(EstadoFormatoA.TERCERA_EVALUACION);
                break;
            case TERCERA_EVALUACION:
                formato.setEstado(EstadoFormatoA.ACEPTADO);
                break;
            default:
                System.out.println("El formato ya fue evaluado: " + formato.getEstado());
                return false;
        }

        aplicarLogicaEstados(formato); // Verificar si pasa a RECHAZADO
        boolean updated = repository.update(formato);
        if (updated) {
            this.notifyAllObserves(); // notificar
        }
        return updated;
    }

    // Rechazar
    public boolean rechazar(int id) {
        DegreeWork formato = repository.findById(id);
        if (formato != null) {
            formato.setEstado(EstadoFormatoA.RECHAZADO);
            aplicarLogicaEstados(formato); 
            boolean updated = repository.update(formato);
            if (updated) {
                this.notifyAllObserves();
            }
            return updated;
        }
        return false;
    }

    // Guardar correcciones de un formato específico
    public boolean guardarCorrecciones(int id, String correcciones) {
        DegreeWork formato = repository.findById(id);
        if (formato == null) {
            return false;
        }

        formato.setCorrecciones(correcciones);
        aplicarLogicaEstados(formato); 
        boolean updated = repository.update(formato);
        if (updated) {
            this.notifyAllObserves(); 
        }
        return updated;
    }

    
    public void aplicarLogicaEstados(DegreeWork formato) {
        if (formato.getEstado() == EstadoFormatoA.NO_ACEPTADO || 
            formato.getEstado() == EstadoFormatoA.PRIMERA_EVALUACION || 
            formato.getEstado() == EstadoFormatoA.SEGUNDA_EVALUACION || 
            formato.getEstado() == EstadoFormatoA.TERCERA_EVALUACION) {
            if (formato.getNoAprobadoCount() > 3) {
                formato.setEstado(EstadoFormatoA.RECHAZADO);
            }
        }
    }
    
    public List<DegreeWork> listarPorEstudianteYModalidad(String studentEmail, Modalidad modalidad) {
        return repository.listByStudentAndModalidad(studentEmail, modalidad);
    }

}