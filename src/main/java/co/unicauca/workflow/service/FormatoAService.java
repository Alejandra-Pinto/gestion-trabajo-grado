package co.unicauca.workflow.service;

import co.unicauca.workflow.access.IFormatoARepository;
import co.unicauca.workflow.domain.entities.*;
import co.unicauca.workflow.infra.Subject;
import java.util.List;

public class FormatoAService extends Subject {
    private final IFormatoARepository repository;

    public FormatoAService(IFormatoARepository repository) {
        this.repository = repository;
    }

    // Registrar un nuevo FormatoA
    public boolean registrarFormato(FormatoA formato) {
        return repository.save(formato);
    }

    // Obtener un FormatoA por id
    public FormatoA obtenerFormato(int id) {
        return repository.findById(id);
    }

    // Listar todos los formatos
    public List<FormatoA> listarFormatos() {
        return repository.listAllFormatoA();
    }

    // Actualizar un formato
    public boolean actualizarFormato(FormatoA formato) {
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
        FormatoA formato = repository.findById(id);
        if (formato != null) {
            formato.setEstado(nuevoEstado);
            return repository.update(formato);
        }
        return false;
    }

    // Avanzar evaluación
    public boolean avanzarEvaluacion(int id) {
        FormatoA formato = repository.findById(id);
        if (formato == null) return false;

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
        return repository.update(formato);
    }

    // Rechazar
    public boolean rechazar(int id) {
        return cambiarEstado(id, EstadoFormatoA.RECHAZADO);
    }
}
