package co.unicauca.workflow;

import co.unicauca.workflow.access.Factory;
import co.unicauca.workflow.access.IDegreeWorkRepository;
import co.unicauca.workflow.domain.entities.DegreeWork;
import co.unicauca.workflow.domain.entities.EstadoFormatoA;
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
    private TableColumn<DegreeWork, String> colEstado;    

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
        inicializarComboBox(); // aquí ya queda conectado el evento setOnAction
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
        
        colEstado.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getEstado() != null ? data.getValue().getEstado().name() : ""
        ));

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

            // Agrupar por estudiante y quedarnos solo con el de mayor id
            Map<String, DegreeWork> ultimosPorEstudiante = todosLosFormatos.stream()
                    .collect(Collectors.toMap(
                            f -> f.getEstudiante().getEmail(),
                            f -> f,
                            (f1, f2) -> f1.getId() > f2.getId() ? f1 : f2
                    ));

            List<DegreeWork> ultimos = new ArrayList<>(ultimosPorEstudiante.values());

            tableFormatos.getItems().setAll(ultimos);

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
                "Aceptados",
                "No aceptados",
                "Primera evaluación",
                "Segunda evaluación",
                "Tercera evaluación",
                "Fecha más reciente",
                "Fecha más antigua"
        );
        comboClasificar.getSelectionModel().selectFirst(); // por defecto "Todos"

        // 🔥 Evento que se dispara al cambiar de opción
        comboClasificar.setOnAction(event -> {
            aplicarFiltro(comboClasificar.getValue());
        });
    }



    private void aplicarFiltro(String opcion) {
        if (opcion == null) {
            return;
        }

        List<DegreeWork> base = new ArrayList<>(todosLosFormatos);

        switch (opcion) {
            case "Todos":
                tableFormatos.getItems().setAll(base);
                break;

            case "Aceptados":
                tableFormatos.getItems().setAll(
                        base.stream()
                                .filter(f -> f.getEstado() == EstadoFormatoA.ACEPTADO)
                                .collect(Collectors.toList())
                );
                break;

            case "No aceptados":
                tableFormatos.getItems().setAll(
                        base.stream()
                                .filter(f -> f.getEstado() == EstadoFormatoA.NO_ACEPTADO)
                                .collect(Collectors.toList())
                );
                break;

            case "Primera evaluación":
                tableFormatos.getItems().setAll(
                        base.stream()
                                .filter(f -> f.getEstado() == EstadoFormatoA.PRIMERA_EVALUACION)
                                .collect(Collectors.toList())
                );
                break;

            case "Segunda evaluación":
                tableFormatos.getItems().setAll(
                        base.stream()
                                .filter(f -> f.getEstado() == EstadoFormatoA.SEGUNDA_EVALUACION)
                                .collect(Collectors.toList())
                );
                break;

            case "Tercera evaluación":
                tableFormatos.getItems().setAll(
                        base.stream()
                                .filter(f -> f.getEstado() == EstadoFormatoA.TERCERA_EVALUACION)
                                .collect(Collectors.toList())
                );
                break;

            case "Fecha más reciente":
                tableFormatos.getItems().setAll(
                        base.stream()
                                .sorted(Comparator.comparing(DegreeWork::getFechaActual).reversed())
                                .collect(Collectors.toList())
                );
                break;

            case "Fecha más antigua":
                tableFormatos.getItems().setAll(
                        base.stream()
                                .sorted(Comparator.comparing(DegreeWork::getFechaActual))
                                .collect(Collectors.toList())
                );
                break;
        }
    }



}
