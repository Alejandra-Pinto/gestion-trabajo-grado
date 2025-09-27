package co.unicauca.workflow;

import co.unicauca.workflow.access.Factory;
import co.unicauca.workflow.access.IDegreeWorkRepository;
import co.unicauca.workflow.domain.entities.DegreeWork;
import co.unicauca.workflow.domain.entities.Student;
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
        
        colTitulo.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                Optional.ofNullable(data.getValue().getTituloProyecto()).orElse("")));
        colEstudiante.setCellValueFactory(data -> new SimpleStringProperty(
                Optional.ofNullable(data.getValue().getEstudiante())
                        .map(Student::getEmail)
                        .orElse("")
        ));

        // Convertimos el enum Modalidad a String (name() o toString())
        colModalidad.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getModalidad() != null ? data.getValue().getModalidad().name() : ""));
        
        colFecha.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getFechaActual() != null ? data.getValue().getFechaActual().toString() : ""));

        // Columna con botón
        colAccion.setCellFactory(param -> new TableCell<DegreeWork, Void>() {
            private final Button btn = new Button("Revisar");

            {
                btn.setOnAction(event -> {
                    
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/unicauca/workflow/CoordinatorReviewFormatA.fxml"));
            Parent root = loader.load();

            CoordinatorReviewFormatAController controller = loader.getController();
            controller.setHostServices(App.getHostServicesInstance());
            controller.setFormato(formato);
            controller.setDegreeWorkService(service); // 🔥 aquí inyectas el service

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
                    
                    List<DegreeWork> porPrograma = todosLosFormatos.stream()
                            .sorted(Comparator.comparing(f -> f.getEstudiante() != null ? f.getEstudiante().getEmail() : ""))
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
