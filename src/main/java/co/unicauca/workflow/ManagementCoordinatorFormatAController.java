package co.unicauca.workflow;

import co.unicauca.workflow.access.Factory;
import co.unicauca.workflow.access.IDegreeWorkRepository;
import co.unicauca.workflow.domain.entities.DegreeWork;
import co.unicauca.workflow.service.DegreeWorkService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class ManagementCoordinatorFormatAController implements Initializable {

    @FXML
    private ListView<DegreeWork> listFormatos;

    @FXML
    private ComboBox<String> comboClasificar;

    @FXML
    private Button btnClasificar;

    private DegreeWorkService service;

    private List<DegreeWork> todosLosFormatos;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        IDegreeWorkRepository repo = Factory.getInstance().getDegreeWorkRepository("sqlite");
        service = new DegreeWorkService(repo);

        cargarFormatos();
        inicializarComboBox();
        configurarBotonClasificar();
    }

    private void cargarFormatos() {
        try {
            todosLosFormatos = service.listarDegreeWorks();
            listFormatos.getItems().setAll(todosLosFormatos);

            // Mostrar datos básicos en cada fila
            listFormatos.setCellFactory(list -> new ListCell<>() {
                @Override
                protected void updateItem(DegreeWork formato, boolean empty) {
                    super.updateItem(formato, empty);
                    if (empty || formato == null) {
                        setText(null);
                    } else {
                        setText("Título: " + formato.getTituloProyecto() +
                                " | Estudiante: " + formato.getIdEstudiante() +
                                " | Modalidad: " + formato.getModalidad() +
                                " | Fecha: " + formato.getFechaActual());
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void inicializarComboBox() {
        comboClasificar.getItems().addAll(
                "Todos",
                "Programa académico",   // si luego agregas programa en DegreeWork, se podrá usar
                "Fecha más reciente",
                "Fecha más antigua"
        );
        comboClasificar.getSelectionModel().selectFirst(); // por defecto "Todos"
    }

    private void configurarBotonClasificar() {
        btnClasificar.setOnAction(event -> {
            String opcion = comboClasificar.getValue();
            if (opcion == null) return;

            switch (opcion) {
                case "Todos":
                    listFormatos.getItems().setAll(todosLosFormatos);
                    break;

                case "Programa académico":
                    // ⚠️ tu entidad no tiene "programa", así que por ahora ordeno por idEstudiante
                    List<DegreeWork> porPrograma = todosLosFormatos.stream()
                            .sorted(Comparator.comparing(DegreeWork::getIdEstudiante))
                            .collect(Collectors.toList());
                    listFormatos.getItems().setAll(porPrograma);
                    break;

                case "Fecha más reciente":
                    List<DegreeWork> masRecientes = todosLosFormatos.stream()
                            .sorted(Comparator.comparing(DegreeWork::getFechaActual).reversed())
                            .collect(Collectors.toList());
                    listFormatos.getItems().setAll(masRecientes);
                    break;

                case "Fecha más antigua":
                    List<DegreeWork> masAntiguos = todosLosFormatos.stream()
                            .sorted(Comparator.comparing(DegreeWork::getFechaActual))
                            .collect(Collectors.toList());
                    listFormatos.getItems().setAll(masAntiguos);
                    break;
            }
        });
    }
}
