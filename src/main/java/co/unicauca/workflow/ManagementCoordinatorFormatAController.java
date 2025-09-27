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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.Button;
import java.util.Optional;


public class ManagementCoordinatorFormatAController implements Initializable {

    @FXML
    private TableView<DegreeWork> tableFormatos;

    @FXML
    private TableColumn<DegreeWork, String> colTitulo;

    @FXML
    private TableColumn<DegreeWork, String> colEstudiante;

    @FXML
    private TableColumn<DegreeWork, String> colModalidad;

    @FXML
    private TableColumn<DegreeWork, String> colFecha;

    @FXML
    private TableColumn<DegreeWork, Void> colAccion; // para el botón

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

        configurarColumnas();
        cargarFormatos();
        inicializarComboBox();
        configurarBotonClasificar();
    }
    
    private void configurarColumnas() {
        // Importante: usa SimpleStringProperty (import javafx.beans.property.SimpleStringProperty)
        colTitulo.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                Optional.ofNullable(data.getValue().getTituloProyecto()).orElse("")));
        colEstudiante.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                Optional.ofNullable(data.getValue().getIdEstudiante()).orElse("")));
        // Convertimos el enum Modalidad a String (name() o toString())
        colModalidad.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getModalidad() != null ? data.getValue().getModalidad().name() : ""));
        // Fecha (LocalDate) -> String, con null-check
        colFecha.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getFechaActual() != null ? data.getValue().getFechaActual().toString() : ""));

        // Columna con botón (uso TableCell<DegreeWork, Void> explícito)
        colAccion.setCellFactory(param -> new TableCell<DegreeWork, Void>() {
            private final Button btn = new Button("Revisar");

            {
                btn.setOnAction(event -> {
                    // Obtener el item directamente desde la TableRow: más robusto que getIndex()
                    DegreeWork seleccionado = (DegreeWork) getTableRow().getItem();
                    if (seleccionado != null) {
                        abrirVentanaRevision(seleccionado);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    
    private void cargarFormatos() {
        try {
            todosLosFormatos = service.listarDegreeWorks();
            tableFormatos.getItems().setAll(todosLosFormatos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    
    private void abrirVentanaRevision(DegreeWork formato) {
        try {
            // carga la vista
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/unicauca/workflow/CoordinatorReviewFormatA.fxml"));
            Parent root = loader.load();

            // obtiene el controller y le pasa HostServices y el DegreeWork
            CoordinatorReviewFormatAController controller = loader.getController();
            controller.setHostServices(App.getHostServicesInstance()); // importante
            controller.setFormato(formato); // importante: este método debe existir en el controller

            // REEMPLAZAR la escena de la ventana actual (evita abrir una nueva)
            Stage currentStage = (Stage) tableFormatos.getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.show();

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
            if (opcion == null) {
                return;
            }

            switch (opcion) {
                case "Todos":
                    tableFormatos.getItems().setAll(todosLosFormatos);
                    break;

                case "Programa académico":
                    // ⚠️ tu entidad no tiene "programa", así que por ahora ordeno por idEstudiante
                    List<DegreeWork> porPrograma = todosLosFormatos.stream()
                            .sorted(Comparator.comparing(DegreeWork::getIdEstudiante))
                            .collect(Collectors.toList());
                    tableFormatos.getItems().setAll(porPrograma);
                    break;

                case "Fecha más reciente":
                    List<DegreeWork> masRecientes = todosLosFormatos.stream()
                            .sorted(Comparator.comparing(DegreeWork::getFechaActual).reversed())
                            .collect(Collectors.toList());
                    tableFormatos.getItems().setAll(masRecientes);
                    break;

                case "Fecha más antigua":
                    List<DegreeWork> masAntiguos = todosLosFormatos.stream()
                            .sorted(Comparator.comparing(DegreeWork::getFechaActual))
                            .collect(Collectors.toList());
                    tableFormatos.getItems().setAll(masAntiguos);
                    break;
            }
        });
    }

}
