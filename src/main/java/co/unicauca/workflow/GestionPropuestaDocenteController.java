/*
 * Click nbproject://nbproject/nbproject.properties to edit this template
 */
package co.unicauca.workflow;

import co.unicauca.workflow.domain.entities.User;
import co.unicauca.workflow.domain.entities.Teacher;
import co.unicauca.workflow.access.Factory;
import co.unicauca.workflow.access.IDegreeWorkRepository;
import co.unicauca.workflow.service.DegreeWorkService;
import co.unicauca.workflow.domain.entities.DegreeWork;
import co.unicauca.workflow.domain.entities.EstadoFormatoA;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Dana Isabella
 */
public class GestionPropuestaDocenteController implements Initializable {

    @FXML
    private TableView<DegreeWork> tblEstadosFormato;

    @FXML
    private TableColumn<DegreeWork, String> colNumeroFormato;

    @FXML
    private TableColumn<DegreeWork, String> colEstado;

    @FXML
    private TableColumn<DegreeWork, Void> colAcciones; // Nueva columna para botones

    @FXML
    private ComboBox<String> comboClasificar;

    @FXML
    private ToggleButton btnRol;

    @FXML
    private ToggleButton btnFormatoDocente;

    @FXML
    private ToggleButton btnAnteproyectoDocente;

    private User usuario;
    private DegreeWorkService service;
    private ObservableList<DegreeWork> todosLosFormatos;

    // Constructor que recibe el usuario
    public GestionPropuestaDocenteController(User usuario) {
        this.usuario = usuario;
    }

    // Constructor por defecto necesario para FXML (aunque no se usará)
    public GestionPropuestaDocenteController() {
        this.usuario = null;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("Inicializando GestionPropuestaDocenteController");
        // Configurar visibilidad de botones según el rol
        if (usuario instanceof Teacher) {
            btnRol.setVisible(true);
            btnFormatoDocente.setVisible(true);
            btnAnteproyectoDocente.setVisible(true);
            System.out.println("Usuario es Docente, mostrando botones");
        } else {
            System.out.println("Usuario no es Docente, botones ocultos");
        }

        // Inicializar el servicio
        IDegreeWorkRepository repo = Factory.getInstance().getDegreeWorkRepository("sqlite");
        service = new DegreeWorkService(repo);

        // Configurar el ComboBox con opciones de clasificación mapeadas al enum
        comboClasificar.getItems().addAll("Todos", "Pendiente", "Aprobado", "No aprobado", "Rechazado");
        comboClasificar.setValue("Todos");

        // Configurar las columnas de la TableView
        colNumeroFormato.setCellValueFactory(new PropertyValueFactory<>("tituloProyecto"));
        colEstado.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEstado().toString()));
        colAcciones.setCellFactory(col -> new TableCell<DegreeWork, Void>() {
            private final Button btnCorrections = new Button("Ver Correcciones");

            {
                btnCorrections.setStyle("-fx-background-color: #111F63; -fx-text-fill: white; -fx-padding: 5;");
                btnCorrections.setOnAction(event -> {
                    DegreeWork formato = getTableRow().getItem();
                    if (formato != null && (formato.getEstado() == EstadoFormatoA.NO_ACEPTADO || formato.getEstado() == EstadoFormatoA.RECHAZADO)) {
                        mostrarCorrecciones(formato, (Node) event.getSource());
                    } else {
                        Alert alert = new Alert(AlertType.WARNING);
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

        // Cargar los estados al iniciar
        cargarEstados();

        // Listener para el ComboBox
        comboClasificar.setOnAction(event -> {
            String filtro = comboClasificar.getValue();
            cargarEstados(filtro);
        });
    }

    private void cargarEstados() {
        cargarEstados("Todos");
    }

    private void cargarEstados(String filtro) {
        try {
            // Obtener todos los formatos de la base de datos
            todosLosFormatos = FXCollections.observableArrayList(service.listarDegreeWorks());

            // Actualizar estados basados en la lógica: Rechazado si No aprobado 3 veces
            for (DegreeWork formato : todosLosFormatos) {
                if (formato.getNoAprobadoCount() >= 3 && formato.getEstado() != EstadoFormatoA.RECHAZADO) {
                    formato.setEstado(EstadoFormatoA.RECHAZADO);
                } else if (formato.getEstado() == EstadoFormatoA.PRIMERA_EVALUACION && formato.getNoAprobadoCount() > 0) {
                    formato.setEstado(EstadoFormatoA.NO_ACEPTADO);
                }
                // Si está en PRIMERA_EVALUACION y no tiene intentos, se mantiene como está
            }

            // Filtrar según el ComboBox
            ObservableList<DegreeWork> filteredList = todosLosFormatos.stream()
                    .filter(formato -> filtro.equals("Todos") || 
                            (filtro.equals("Pendiente") && formato.getEstado() == EstadoFormatoA.PRIMERA_EVALUACION) ||
                            (filtro.equals("Aprobado") && formato.getEstado() == EstadoFormatoA.ACEPTADO) ||
                            (filtro.equals("No aprobado") && formato.getEstado() == EstadoFormatoA.NO_ACEPTADO) ||
                            (filtro.equals("Rechazado") && formato.getEstado() == EstadoFormatoA.RECHAZADO))
                    .collect(Collectors.collectingAndThen(Collectors.toList(), FXCollections::observableArrayList));

            // Aplicar el estilo a las filas según el estado
            tblEstadosFormato.setRowFactory(tv -> new javafx.scene.control.TableRow<DegreeWork>() {
                @Override
                protected void updateItem(DegreeWork item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setStyle("");
                    } else {
                        String color;
                        if (item.getEstado() == EstadoFormatoA.ACEPTADO) {
                            color = "#4CAF50";
                        } else if (item.getEstado() == EstadoFormatoA.RECHAZADO) {
                            color = "#F44336";
                        } else { // Incluye PRIMERA_EVALUACION y NO_APROBADO
                            color = "#e0e0e0";
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

            // Actualizar la TableView con los datos filtrados
            tblEstadosFormato.setItems(filteredList);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al cargar estados desde la base de datos: " + e.getMessage());
        }
    }

    private void mostrarCorrecciones(DegreeWork formato, Node source) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/unicauca/workflow/TeacherReviewFormatA.fxml"));
            Parent root = loader.load();

            TeacherReviewFormatAController controller = loader.getController();
            controller.setUsuarioYFormato(usuario, formato); // Pasar el usuario y el formato

            Stage stage = (Stage) source.getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("Correcciones para " + formato.getTituloProyecto());
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cargar TeacherReviewFormatA.fxml: " + e.getMessage());
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo cargar la vista de correcciones.");
            alert.showAndWait();
        }
    }

    // Método para establecer el usuario desde HomeController (opcional, ya que se usa el constructor)
    public void setUsuario(User usuario) {
        this.usuario = usuario;
        // Actualizar visibilidad de botones si ya está inicializado
        if (btnRol != null && btnFormatoDocente != null && btnAnteproyectoDocente != null) {
            boolean esDocente = usuario instanceof Teacher;
            btnRol.setVisible(esDocente);
            btnFormatoDocente.setVisible(esDocente);
            btnAnteproyectoDocente.setVisible(esDocente);
            System.out.println("setUsuario: esDocente=" + esDocente);
        }
    }

    public void onAgregarPropuesta(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/unicauca/workflow/ManagementTeacherFormatA.fxml"));
            Parent root = loader.load();

            // Cambiar toda la escena
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            ManagementTeacherFormatAController teacherController = loader.getController();
            teacherController.setHostServices(App.getHostServicesInstance());
            stage.setTitle("Gestión de propuestas");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}