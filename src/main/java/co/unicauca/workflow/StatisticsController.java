package co.unicauca.workflow;

import co.unicauca.workflow.domain.entities.DegreeWork;
import co.unicauca.workflow.domain.entities.EstadoFormatoA;
import co.unicauca.workflow.infra.Observer;
import co.unicauca.workflow.service.DegreeWorkService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.List;

public class StatisticsController implements Observer {

    @FXML private Label lblAceptados;
    @FXML private Label lblNoAceptados;
    @FXML private Label lblRechazados;
    @FXML private Label lblPrimera;
    @FXML private Label lblSegunda;
    @FXML private Label lblTercera;

    private DegreeWorkService service;

    public void setService(DegreeWorkService service) {
        this.service = service;
        service.addObserver(this); // 👈 Se registra como observador
        actualizarEstadisticas();
    }

    private void actualizarEstadisticas() {
        if (service == null) return;

        List<DegreeWork> formatos = service.listarDegreeWorks();

        long aceptados   = formatos.stream().filter(f -> f.getEstado() == EstadoFormatoA.ACEPTADO).count();
        long noAceptados = formatos.stream().filter(f -> f.getEstado() == EstadoFormatoA.NO_ACEPTADO).count();
        long rechazados  = formatos.stream().filter(f -> f.getEstado() == EstadoFormatoA.RECHAZADO).count();
        long primera     = formatos.stream().filter(f -> f.getEstado() == EstadoFormatoA.PRIMERA_EVALUACION).count();
        long segunda     = formatos.stream().filter(f -> f.getEstado() == EstadoFormatoA.SEGUNDA_EVALUACION).count();
        long tercera     = formatos.stream().filter(f -> f.getEstado() == EstadoFormatoA.TERCERA_EVALUACION).count();

        lblAceptados.setText("ACEPTADOS: " + aceptados);
        lblNoAceptados.setText("NO ACEPTADOS: " + noAceptados);
        lblRechazados.setText("RECHAZADOS: " + rechazados);
        lblPrimera.setText("PRIMERA EVALUACION: " + primera);
        lblSegunda.setText("SEGUNDA EVALUACION: " + segunda);
        lblTercera.setText("TERCERA EVALUACION: " + tercera);
    }

    @Override
    public void update(Object o) {
        // cuando DegreeWorkService notifica a los observers
        actualizarEstadisticas();
    }
}
