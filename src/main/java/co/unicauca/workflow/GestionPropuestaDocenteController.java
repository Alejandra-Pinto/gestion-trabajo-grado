package co.unicauca.workflow;

import co.unicauca.workflow.domain.entities.User;
import co.unicauca.workflow.domain.entities.Teacher;
import co.unicauca.workflow.domain.entities.Coordinator;
import co.unicauca.workflow.access.Factory;
import co.unicauca.workflow.access.IDegreeWorkRepository;
import co.unicauca.workflow.service.DegreeWorkService;
import co.unicauca.workflow.domain.entities.DegreeWork;
import co.unicauca.workflow.domain.entities.EstadoFormatoA;
import co.unicauca.workflow.service.AdminService;
import co.unicauca.workflow.service.UserService;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class GestionPropuestaDocenteController implements Initializable {

    @FXML
    private AnchorPane mainAnchorPane; // Añadido para cambiar el contenido

    @FXML
    private TableView<DegreeWork> tblEstadosFormato;

    @FXML
    private TableColumn<DegreeWork, String> colNumeroFormato;
    
    @FXML
    private TableColumn<DegreeWork, String> colEmailEstudiante;

    @FXML
    private TableColumn<DegreeWork, String> colFechaActual;

    @FXML
    private TableColumn<DegreeWork, String> colEstado;

    @FXML
    private TableColumn<DegreeWork, Void> colAcciones;

    @FXML
    private ComboBox<String> comboClasificar;

    @FXML
    private ToggleButton btnRol;

    @FXML
    private ToggleButton btnFormatoDocente;

    @FXML
    private ToggleButton btnAnteproyectoDocente;

    @FXML
    private ToggleButton btnFormatoEstudiante;

    @FXML
    private ToggleButton btnAnteproyectoEstudiante;

    @FXML
    private ToggleButton btnEvaluarPropuestas;

    @FXML
    private ToggleButton btnEvaluarAnteproyectos;

    @FXML
    private Button btnAgregarPropuesta;

    private User usuario;
    private DegreeWorkService service;
    private ObservableList<DegreeWork> todosLosFormatos;
    
    private Teacher docenteActual;

    
    public void setUsuario(User usuario) {
        this.usuario = usuario;
        if (usuario instanceof Teacher) {
            this.docenteActual = (Teacher) usuario;
            
            btnRol.setVisible(true);
            btnFormatoDocente.setVisible(true);
            btnAnteproyectoDocente.setVisible(true);
            System.out.println("Usuario es Docente, mostrando botones");
            
            cargarEstados("Todos");
            
        }else {
            System.out.println("Usuario no es Docente, botones ocultos");
        }
        
    }


    public GestionPropuestaDocenteController(User usuario) {
        this.usuario = usuario;
    }

    public GestionPropuestaDocenteController() {
        this.usuario = null;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("Inicializando GestionPropuestaDocenteController");

        IDegreeWorkRepository repo = Factory.getInstance().getDegreeWorkRepository("sqlite");
        service = new DegreeWorkService(repo);

        comboClasificar.getItems().addAll(
                "Todos",
                "Aceptado",
                "No aceptado",
                "Primera evaluación",
                "Segunda evaluación",
                "Tercera evaluación",
                "Rechazado",
                "Fecha más reciente",
                "Fecha más antigua"
        );
        comboClasificar.setValue("Todos");

        comboClasificar.setOnAction(event -> aplicarFiltro(comboClasificar.getValue()));

        // Configurar columnas
        colNumeroFormato.setCellValueFactory(new PropertyValueFactory<>("tituloProyecto"));
        colEstado.setCellValueFactory(cellData
                -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEstado().toString()));
        
        // Email del estudiante
        colEmailEstudiante.setCellValueFactory(cellData -> {
            if (cellData.getValue().getEstudiante() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getEstudiante().getEmail()
                );
            } else {
                return new javafx.beans.property.SimpleStringProperty("Sin estudiante");
            }
        });

        // Fecha actual
        colFechaActual.setCellValueFactory(cellData -> {
            if (cellData.getValue().getFechaActual() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getFechaActual().toString()
                );
            } else {
                return new javafx.beans.property.SimpleStringProperty("N/A");
            }
        });


        colAcciones.setCellFactory(col -> new TableCell<DegreeWork, Void>() {
            private final Button btnCorrections = new Button("Ver Correcciones");

            {
                btnCorrections.setStyle("-fx-background-color: #111F63; -fx-text-fill: white; -fx-padding: 5;");
                btnCorrections.setOnAction(event -> {
                    DegreeWork formato = getTableRow().getItem();
                    if (formato != null && (formato.getEstado() == EstadoFormatoA.NO_ACEPTADO || formato.getEstado() == EstadoFormatoA.RECHAZADO)) {
                        mostrarCorrecciones(formato);
                    } else {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Acción no permitida");
                        alert.setHeaderText(null);
                        alert.setContentText("Solo se pueden ver correcciones para estados 'No aprobado' o 'Rechazado'.");
                        alert.showAndWait();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    DegreeWork formato = getTableRow().getItem();
                    if (formato.getEstado() == EstadoFormatoA.NO_ACEPTADO || formato.getEstado() == EstadoFormatoA.RECHAZADO) {
                        setGraphic(btnCorrections);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });

        
    }

    
    private void cargarEstados(String filtro) {
        if (docenteActual == null) {
            System.out.println("⚠️ El docente actual es null. No se pueden cargar los estados.");
            return;
        }

        // Traer todos los trabajos de grado del docente
        List<DegreeWork> todos = service.listarDegreeWorksPorDocente(docenteActual.getEmail());

        // Agrupar por estudiante y quedarnos solo con el de mayor id (último guardado)
        Map<String, DegreeWork> ultimosPorEstudiante = todos.stream()
                .filter(f -> f.getEstudiante() != null)
                .collect(Collectors.toMap(
                        f -> f.getEstudiante().getEmail(),
                        f -> f,
                        (f1, f2) -> f1.getId() > f2.getId() ? f1 : f2
                ));

        List<DegreeWork> ultimos = new ArrayList<>(ultimosPorEstudiante.values());

        // Guardar lista base para filtros posteriores
        todosLosFormatos = FXCollections.observableArrayList(ultimos);

        // Aplicar filtro inicial
        ObservableList<DegreeWork> filtrados = FXCollections.observableArrayList(
                ultimos.stream()
                        .filter(f -> filtro.equals("Todos")
                        || f.getEstado().toString().equalsIgnoreCase(filtro))
                        .collect(Collectors.toList())
        );

        // Cargar en la tabla
        tblEstadosFormato.setItems(filtrados);
    }



    
    private void aplicarFiltro(String opcion) {
        if (opcion == null) {
            return;
        }

        List<DegreeWork> base = new ArrayList<>(todosLosFormatos);

        switch (opcion) {
            case "Todos":
                tblEstadosFormato.getItems().setAll(base);
                break;

            case "Aceptado":
                tblEstadosFormato.getItems().setAll(
                        base.stream()
                                .filter(f -> f.getEstado() == EstadoFormatoA.ACEPTADO)
                                .collect(Collectors.toList())
                );
                break;

            case "No aceptado":
                tblEstadosFormato.getItems().setAll(
                        base.stream()
                                .filter(f -> f.getEstado() == EstadoFormatoA.NO_ACEPTADO)
                                .collect(Collectors.toList())
                );
                break;

            case "Primera evaluación":
                tblEstadosFormato.getItems().setAll(
                        base.stream()
                                .filter(f -> f.getEstado() == EstadoFormatoA.PRIMERA_EVALUACION)
                                .collect(Collectors.toList())
                );
                break;

            case "Segunda evaluación":
                tblEstadosFormato.getItems().setAll(
                        base.stream()
                                .filter(f -> f.getEstado() == EstadoFormatoA.SEGUNDA_EVALUACION)
                                .collect(Collectors.toList())
                );
                break;

            case "Tercera evaluación":
                tblEstadosFormato.getItems().setAll(
                        base.stream()
                                .filter(f -> f.getEstado() == EstadoFormatoA.TERCERA_EVALUACION)
                                .collect(Collectors.toList())
                );
                break;

            case "Rechazado":
                tblEstadosFormato.getItems().setAll(
                        base.stream()
                                .filter(f -> f.getEstado() == EstadoFormatoA.RECHAZADO)
                                .collect(Collectors.toList())
                );
                break;

            case "Fecha más reciente":
                tblEstadosFormato.getItems().setAll(
                        base.stream()
                                .sorted(Comparator.comparing(DegreeWork::getFechaActual).reversed())
                                .collect(Collectors.toList())
                );
                break;

            case "Fecha más antigua":
                tblEstadosFormato.getItems().setAll(
                        base.stream()
                                .sorted(Comparator.comparing(DegreeWork::getFechaActual))
                                .collect(Collectors.toList())
                );
                break;
        }
    }

    
    private void mostrarCorrecciones(DegreeWork formato) {
        try {
            // Cargar el FXML de correcciones
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/unicauca/workflow/TeacherReviewFormatA.fxml"));
            Parent reviewContent = loader.load();
            TeacherReviewFormatAController controller = loader.getController();
            controller.setUsuarioYFormato(usuario, formato);

            // Reemplazar el contenido del AnchorPane principal
            mainAnchorPane.getChildren().clear();
            mainAnchorPane.getChildren().add(reviewContent);
            AnchorPane.setTopAnchor(reviewContent, 0.0);
            AnchorPane.setLeftAnchor(reviewContent, 0.0);
            AnchorPane.setRightAnchor(reviewContent, 0.0);
            AnchorPane.setBottomAnchor(reviewContent, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cargar TeacherReviewFormatA.fxml: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo cargar la vista de correcciones.");
            alert.showAndWait();
        }
    }

    

    @FXML
    private void onBtnRolClicked() {
        System.out.println("Botón Rol clicked");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/unicauca/workflow/RolView.fxml"));
            Parent root = loader.load();

            RolController rolController = loader.getController();

            // Crear servicios
            UserService userService = new UserService(Factory.getInstance().getUserRepository("sqlite"));
            AdminService adminService = new AdminService(Factory.getInstance().getAdminRepository("sqlite"));

            // Pasar usuario + servicios al controller
            if (usuario != null) {
                rolController.setUsuario(usuario, userService, adminService);
            }

            Stage stage = (Stage) btnRol.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Información del Usuario");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la vista de Rol: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onBtnFormatoDocenteClicked() {
        System.out.println("Botón Formato Docente clicked");
        // Lógica para Formato Docente (puedes implementarla)
    }


    @FXML
    private void onBtnFormatoEstudianteClicked() {
        System.out.println("Botón Formato Estudiante clicked");
        // Lógica para estudiante (oculta para docentes)
    }

    @FXML
    private void onBtnAnteproyectoEstudianteClicked() {
        System.out.println("Botón Anteproyecto Estudiante clicked");
        // Lógica para estudiante (oculta para docentes)
    }

    @FXML
    private void onBtnEvaluarPropuestasClicked() {
        System.out.println("Botón Evaluar Propuestas clicked");
        // Lógica para evaluar propuestas (puedes implementarla)
    }

    @FXML
    private void onBtnEvaluarAnteproyectosClicked() {
        System.out.println("Botón Evaluar Anteproyectos clicked");
        // Lógica para evaluar anteproyectos (puedes implementarla)
    }

    @FXML
    private void onAgregarPropuesta(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/unicauca/workflow/ManagementTeacherFormatA.fxml"));
            Parent root = loader.load();
            ManagementTeacherFormatAController controller = loader.getController();
            controller.setUsuario(usuario);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Agregar Propuesta");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cargar ManagementTeacherFormatA.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/unicauca/workflow/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnAgregarPropuesta.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();

            System.out.println("Sesión cerrada, volviendo al login");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cargar Login.fxml: " + e.getMessage());
        }
    }
    
    
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);

        Label etiqueta = new Label(mensaje);
        etiqueta.setWrapText(true);
        etiqueta.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-text-fill: #2c3e50;");

        VBox contenedor = new VBox(etiqueta);
        contenedor.setSpacing(10);
        contenedor.setPadding(new Insets(10));

        alerta.getDialogPane().setContent(contenedor);
        alerta.showAndWait();
    }

}