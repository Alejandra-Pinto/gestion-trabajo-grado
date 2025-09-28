package co.unicauca.workflow;

import co.unicauca.workflow.domain.entities.User;
import co.unicauca.workflow.domain.entities.Teacher;
import co.unicauca.workflow.domain.entities.Coordinator;
import co.unicauca.workflow.access.Factory;
import co.unicauca.workflow.access.IDegreeWorkRepository;
import co.unicauca.workflow.service.DegreeWorkService;
import co.unicauca.workflow.domain.entities.DegreeWork;
import co.unicauca.workflow.domain.entities.EstadoFormatoA;
import java.io.IOException;
import java.net.URL;
import java.util.List;
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
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Dana Isabella
 */
public class GestionPropuestaDocenteController implements Initializable {

    @FXML
    private AnchorPane mainAnchorPane; // Añadido para cambiar el contenido

    @FXML
    private TableView<DegreeWork> tblEstadosFormato;

    @FXML
    private TableColumn<DegreeWork, String> colNumeroFormato;

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

        // Configurar visibilidad inicial (se ajustará en setUsuario)
        if (usuario != null) {
            setUsuario(usuario); // Asegurar visibilidad inicial
        }

        // Configurar ComboBox
        comboClasificar.getItems().addAll("Todos", "Pendiente", "Aprobado", "No aprobado", "Rechazado");
        comboClasificar.setValue("Todos");

        // Configurar columnas
        colNumeroFormato.setCellValueFactory(new PropertyValueFactory<>("tituloProyecto"));
        colEstado.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEstado().toString()));
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

        // Cargar estados con valor por defecto
        cargarEstados("Todos");
    }

    private void cargarEstados(String filtro) {
        try {
            todosLosFormatos = FXCollections.observableArrayList(service.listarDegreeWorks());
            ObservableList<DegreeWork> filteredList = todosLosFormatos.stream()
                    .filter(formato -> filtro.equals("Todos") || formato.getEstado().toString().equalsIgnoreCase(filtro))
                    .collect(Collectors.collectingAndThen(Collectors.toList(), FXCollections::observableArrayList));

            tblEstadosFormato.setRowFactory(tv -> new javafx.scene.control.TableRow<DegreeWork>() {
                @Override
                protected void updateItem(DegreeWork item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setStyle("");
                    } else {
                        String color;
                        switch (item.getEstado()) {
                            case ACEPTADO:
                                color = "#4CAF50";
                                break;
                            case RECHAZADO:
                                color = "#F44336";
                                break;
                            case NO_ACEPTADO:
                            case PRIMERA_EVALUACION:
                                color = "#e0e0e0";
                                break;
                            default:
                                color = "#e0e0e0";
                                break;
                        }
                        setStyle("-fx-background-color: " + color + "; " +
                                "-fx-padding: 10; " +
                                "-fx-font-size: 14px; " +
                                "-fx-text-fill: white; " +
                                "-fx-border-radius: 5; " +
                                "-fx-background-radius: 5;");
                    }
                }
            });

            tblEstadosFormato.setItems(filteredList);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al cargar estados: " + e.getMessage());
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

    public void setUsuario(User usuario) {
        this.usuario = usuario;
        if (btnRol != null && btnFormatoDocente != null && btnAnteproyectoDocente != null) {
            boolean esDocente = usuario instanceof Teacher;
            boolean esCoordinador = usuario instanceof Coordinator; // Ajusta según tu modelo (ejemplo)
            btnRol.setVisible(esDocente || esCoordinador);
            btnFormatoDocente.setVisible(esDocente);
            btnAnteproyectoDocente.setVisible(esDocente);
            btnFormatoEstudiante.setVisible(!esDocente && !esCoordinador);
            btnAnteproyectoEstudiante.setVisible(!esDocente && !esCoordinador);
            btnEvaluarPropuestas.setVisible(esCoordinador); // Solo visible para coordinador
            btnEvaluarAnteproyectos.setVisible(esCoordinador); // Solo visible para coordinador
            System.out.println("setUsuario: esDocente=" + esDocente + ", esCoordinador=" + esCoordinador);
            cargarEstados("Todos"); // Recargar con valor por defecto
        }
    }

    @FXML
    private void onBtnRolClicked() {
        System.out.println("Botón Rol clicked");
        // Lógica para Rol (puedes implementarla)
    }

    @FXML
    private void onBtnFormatoDocenteClicked() {
        System.out.println("Botón Formato Docente clicked");
        // Lógica para Formato Docente (puedes implementarla)
    }

    @FXML
    private void onBtnAnteproyectoDocenteClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/unicauca/workflow/ManagementTeacherFormatA.fxml"));
            Parent root = loader.load();
            ManagementTeacherFormatAController controller = loader.getController();
            controller.setUsuario(usuario);
            Stage stage = (Stage) btnAnteproyectoDocente.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestión de Anteproyecto");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cargar ManagementTeacherFormatA.fxml: " + e.getMessage());
        }
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
        System.out.println("Cerrando sesión");
        Stage stage = (Stage) btnAgregarPropuesta.getScene().getWindow();
        stage.close();
    }
}