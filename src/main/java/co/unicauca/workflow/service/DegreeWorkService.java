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

    // Actualizar un formato
    public boolean actualizarFormato(DegreeWork formato) {
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
            boolean updated = repository.update(formato);
            if (updated) {
                this.notifyAllObserves(); // 🔔 notificar
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

        boolean updated = repository.update(formato);
        if (updated) {
            this.notifyAllObserves(); // 🔔 notificar
        }
        return updated;
    }

    // Rechazar
    public boolean rechazar(int id) {
        boolean updated = cambiarEstado(id, EstadoFormatoA.RECHAZADO);
        if (updated) {
            this.notifyAllObserves(); // 🔔 notificar
        }
        return updated;
    }

    
    // Guardar correcciones de un formato específico
    public boolean guardarCorrecciones(int id, String correcciones) {
        DegreeWork formato = repository.findById(id);
        if (formato == null) {
            return false;
        }

        formato.setCorrecciones(correcciones);

        boolean updated = repository.update(formato);
        if (updated) {
            this.notifyAllObserves(); // notificar cambios a observadores
        }
        return updated;
    }

}
