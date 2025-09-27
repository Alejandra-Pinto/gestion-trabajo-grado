package co.unicauca.workflow;

import co.unicauca.workflow.access.Factory;
import co.unicauca.workflow.access.UserSQLiteRepository;
import co.unicauca.workflow.domain.entities.Coordinator;
import co.unicauca.workflow.domain.entities.SuperAdmin;
import co.unicauca.workflow.domain.entities.User;
import co.unicauca.workflow.service.AdminService;
import co.unicauca.workflow.service.SessionManager;
import co.unicauca.workflow.service.UserService;
import java.io.IOException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class ManagementAdminController {

    @FXML
    private TableView<Coordinator> tblCoordinadores;

    @FXML
    private TableColumn<Coordinator, String> colCorreo;

    @FXML
    private TableColumn<Coordinator, String> colNombre;

    @FXML
    private TableColumn<Coordinator, String> colEstado;

    @FXML
    private Button btnModificarEstado;
    
    @FXML
    private ToggleButton btnUsuario;

    private UserSQLiteRepository repo;
    private ObservableList<Coordinator> coordinadores;
    private SuperAdmin usuario;

    @FXML
    public void initialize() {
        repo = new UserSQLiteRepository();

        // ✅ Tomar el usuario desde la sesión
        SuperAdmin current = (SuperAdmin)SessionManager.getCurrentUser();
        if (current instanceof SuperAdmin) {
            usuario = (SuperAdmin) current;
        }

        // Configurar columnas
        colCorreo.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));
        colNombre.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getFirstName() + " " + data.getValue().getLastName()
        ));

        // Columna de estado con ComboBox
        colEstado.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus()));
        colEstado.setCellFactory(col -> {
            return new TableCell<Coordinator, String>() {
                private final ComboBox<String> combo = new ComboBox<>();

                {
                    combo.getItems().addAll("PENDIENTE", "ACEPTADO", "RECHAZADO");
                    combo.setOnAction(e -> {
                        Coordinator coord = getTableView().getItems().get(getIndex());
                        coord.setStatus(combo.getValue()); // actualizar objeto
                    });
                }

                @Override
                protected void updateItem(String estado, boolean empty) {
                    super.updateItem(estado, empty);
                    if (empty || estado == null) {
                        setGraphic(null);
                    } else {
                        combo.setValue(estado);
                        setGraphic(combo);
                    }
                }
            };
        });

        cargarCoordinadores();
    }

    private void cargarCoordinadores() {
        List<User> users = repo.listAll();
        coordinadores = FXCollections.observableArrayList();

        for (User u : users) {
            if (u instanceof Coordinator) {
                coordinadores.add((Coordinator) u);
            }
        }
        tblCoordinadores.setItems(coordinadores);
        tblCoordinadores.refresh();
    }

    @FXML
    private void onGuardarCambios() {
        boolean huboCambios = false;
        for (Coordinator c : coordinadores) {
            if (repo.updateCoordinatorStatus(c.getEmail(), c.getStatus())) {
                huboCambios = true;
            }
        }

        if (huboCambios) {
            mostrarAlerta("Éxito", "Estados actualizados con éxito.", Alert.AlertType.INFORMATION);
        } else {
            mostrarAlerta("Aviso", "No se realizaron cambios en los estados.", Alert.AlertType.WARNING);
        }

        // 🔄 Recargar tabla desde BD
        cargarCoordinadores();
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
            if (usuario != null) {
                rolController.setAdmin(usuario, userService, adminService);
            }

            Stage stage = (Stage) btnUsuario.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Información del Usuario");

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
