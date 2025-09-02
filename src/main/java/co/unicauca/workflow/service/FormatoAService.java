package co.unicauca.workflow.service;

import co.unicauca.workflow.domain.entities.*;
import co.unicauca.workflow.infra.Subject;

public class FormatoAService extends Subject {
    private FormatoA formatoA;

    // Constructor
    public FormatoAService(FormatoA formatoA) {
        this.formatoA = formatoA;
    }

    // Obtener el Formato A
    public FormatoA getFormatoA() {
        return formatoA;
    }

    // Actualizar datos generales del formato
    public void updateFormato(FormatoA nuevoFormato) {
        this.formatoA = nuevoFormato;
        this.notifyAllObserves(); // notificar a todos los observadores
    }

    // Cambiar el estado del Formato A
    public void cambiarEstado(EstadoFormatoA nuevoEstado) {
        this.formatoA.setEstado(nuevoEstado);
        this.notifyAllObserves(); // 🔔 notificar a todas las vistas
    }

    // Método de ayuda para avanzar a la siguiente evaluación
    public void avanzarEvaluacion() {
        switch (formatoA.getEstado()) {
            case PRIMERA_EVALUACION:
                cambiarEstado(EstadoFormatoA.SEGUNDA_EVALUACION);
                break;
            case SEGUNDA_EVALUACION:
                cambiarEstado(EstadoFormatoA.TERCERA_EVALUACION);
                break;
            case TERCERA_EVALUACION:
                cambiarEstado(EstadoFormatoA.ACEPTADO); // por defecto acepta
                break;
            default:
                System.out.println("El formato ya fue evaluado: " + formatoA.getEstado());
        }
    }

    // Rechazar el Formato A
    public void rechazar() {
        cambiarEstado(EstadoFormatoA.RECHAZADO);
    }
}
