package co.unicauca.workflow.infra;

import java.util.ArrayList;
import java.util.List;

public abstract class Subject {
    private final List<Observer> observers = new ArrayList<>();

    /**
     * Agrega un observador
     *
     * @param obs observador a registrar
     */
    public void addObserver(Observer obs) {
        if (obs != null && !observers.contains(obs)) {
            observers.add(obs);
        }
    }

    /**
     * Notifica a todos los observadores que hubo un cambio en el modelo
     */
    public void notifyAllObserves() {
        for (Observer each : observers) {
            each.update(this);
        }
    }
}
