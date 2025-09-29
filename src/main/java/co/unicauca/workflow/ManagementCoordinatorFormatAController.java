package co.unicauca.workflow;

import co.unicauca.workflow.access.Factory;
import co.unicauca.workflow.access.IDegreeWorkRepository;
import co.unicauca.workflow.domain.entities.DegreeWork;
import co.unicauca.workflow.domain.entities.EstadoFormatoA;
import co.unicauca.workflow.domain.entities.Student;
import co.unicauca.workflow.domain.entities.User;
import co.unicauca.workflow.service.AdminService;
import co.unicauca.workflow.service.DegreeWorkService;
import co.unicauca.workflow.service.SessionManager;
import co.unicauca.workflow.service.UserService;
import java.io.IOException;
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

    @FXML private Button btnClasificar;
    @FXML private ToggleButton btnUsuario;

    private DegreeWorkService service;

    private List<DegreeWork> todosLosFormatos;
    private User usuarioActual;

    public void setUsuarioActual(User usuario) {
        this.usuarioActual = usuario;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        IDegreeWorkRepository repo = Factory.getInstance().getDegreeWorkRepository("sqlite");
        service = new DegreeWorkService(repo);
        usuarioActual = (User) SessionManager.getCurrentUser();

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
            List<DegreeWork> crudos = service.listarDegreeWorks();

            Map<String, DegreeWork> ultimosPorEstudiante = crudos.stream()
                    .collect(Collectors.toMap(
                            f -> f.getEstudiante().getEmail(),
                            f -> f,
                            (f1, f2) -> f1.getId() > f2.getId() ? f1 : f2
                    ));

            todosLosFormatos = new ArrayList<>(ultimosPorEstudiante.values());

            for (DegreeWork formato : todosLosFormatos) {
                service.aplicarLogicaEstados(formato);
                service.actualizarFormato(formato);
            }

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
                "Aceptados",
                "No aceptados",
                "Primera evaluación",
                "Segunda evaluación",
                "Tercera evaluación",
                "Fecha más reciente",
                "Fecha más antigua"
        );
        comboClasificar.getSelectionModel().selectFirst(); // por defecto "Todos"

        
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

            default:
                // Por si llega un valor inesperado
                tableFormatos.getItems().setAll(base);
                break;
        }
    }


    
    @FXML
    private void onBtnUsuarioClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/unicauca/workflow/RolView.fxml"));
            Parent root = loader.load();

            RolController rolController = loader.getController();

            // Crear servicios
            UserService userService = new UserService(Factory.getInstance().getUserRepository("sqlite"));
            AdminService adminService = new AdminService(Factory.getInstance().getAdminRepository("sqlite"));

            // Pasar usuario + servicios al controller
            if (usuarioActual != null) {
                rolController.setUsuario(usuarioActual, userService, adminService);
            }

            Stage stage = (Stage) btnUsuario.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Información del Usuario");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la vista de Rol: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void handleLogout() {
        try {
            SessionManager.clearSession();

            Stage stage = (Stage) btnUsuario.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/unicauca/workflow/Login.fxml"));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.setTitle("Login - Workflow");

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cerrar sesión: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}