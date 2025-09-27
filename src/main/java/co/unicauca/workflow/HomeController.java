package co.unicauca.workflow;

import co.unicauca.workflow.access.Factory;
import co.unicauca.workflow.domain.entities.Coordinator;
import co.unicauca.workflow.domain.entities.User;
import co.unicauca.workflow.domain.entities.Teacher;
import co.unicauca.workflow.domain.entities.Student;
import co.unicauca.workflow.service.AdminService;
import co.unicauca.workflow.service.UserService;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class HomeController implements Initializable {

    @FXML
    private SplitPane splitPane;

    @FXML
    private AnchorPane contentPane;

    @FXML
    private ToggleButton btnRol;

    @FXML
    private ToggleButton btnAnteproyectoDocente;
    @FXML
    private ToggleButton btnFormatoDocente;
    @FXML
    private ToggleButton btnFormatoEstudiante;
    @FXML
    private ToggleButton btnAnteproyectoEstudiante;
    @FXML
    private ToggleButton btnEvaluarPropuestas;
    @FXML
    private ToggleButton btnEvaluarAnteproyectos;

    private User usuario;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarBotones();
    }

    public void setUsuario(User usuario) {
        this.usuario = usuario;
        cargarUsuario();
    }

    private void cargarUsuario() {
        if (usuario == null) return;

        String programa = usuario.getProgram() != null ? usuario.getProgram().toString() : "Sin programa";

        if (usuario instanceof Teacher) {
            btnRol.setText("Docente\n(" + programa + ")");
            btnAnteproyectoDocente.setVisible(true);
            btnFormatoDocente.setVisible(true);

        } else if (usuario instanceof Student) {
            btnRol.setText("Estudiante\n(" + programa + ")");
            btnFormatoEstudiante.setVisible(true);
            btnAnteproyectoEstudiante.setVisible(true);

        } else if (usuario instanceof Coordinator || "coordinador".equalsIgnoreCase(usuario.getRole())) {
            btnRol.setText("Coordinador\n(" + programa + ")");
            btnEvaluarPropuestas.setVisible(true);
            btnEvaluarAnteproyectos.setVisible(true);
        }
    }
    
    @FXML
    private void onBtnRolClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/unicauca/workflow/RolView.fxml"));
            Parent root = loader.load();

            RolController rolController = loader.getController();

            // Crear servicios (usando las implementaciones reales de tus repositorios)
            UserService userService = new UserService(Factory.getInstance().getUserRepository("sqlite"));
            AdminService adminService = new AdminService(Factory.getInstance().getAdminRepository("sqlite"));

            // Pasar usuario + servicios
            if (usuario != null) {
                rolController.setUsuario(usuario, userService, adminService);
            }

            Stage stage = (Stage) btnRol.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Información del Usuario");

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la vista de Rol: " + e.getMessage(), AlertType.ERROR);
        }
    }

    /**
     * LOGOUT
     */
    @FXML
    private void handleLogout() {
        try {
            co.unicauca.workflow.service.SessionManager.clearSession();

            Stage stage = (Stage) btnRol.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/unicauca/workflow/Login.fxml"));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.setTitle("Login - Workflow");

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cerrar sesión: " + e.getMessage(), AlertType.ERROR);
        }
    }

    // ===== BOTONES =====

    @FXML
    private void onBtnFormatoDocenteClicked() {
        if (!(usuario instanceof Teacher)) {
            mostrarAlerta("Acceso denegado", "Solo los docentes pueden acceder a esta funcionalidad.", AlertType.WARNING);
            return;
        }
        cargarVistaConUsuario("/co/unicauca/workflow/GestionPropuestaDocente.fxml", "Gestión de Propuestas Docente");
    }

    @FXML
    private void onBtnFormatoEstudianteClicked() {
        if (!(usuario instanceof Student)) {
            mostrarAlerta("Acceso denegado", "Solo los estudiantes pueden acceder a esta funcionalidad.", AlertType.WARNING);
            return;
        }
        cargarVistaConUsuario("/co/unicauca/workflow/ManagementStudentFormatA.fxml", "Gestión de Formatos - Estudiante");
    }

    @FXML
    private void onBtnEvaluarPropuestasClicked() {
        if (!(usuario instanceof Coordinator)) {
            mostrarAlerta("Acceso denegado", "Solo los coordinadores pueden acceder a esta funcionalidad.", AlertType.WARNING);
            return;
        }
        cargarVistaConUsuario("/co/unicauca/workflow/ManagementCoordinatorFormatA.fxml", "Gestión de Propuestas - Coordinador");
    }

    @FXML
    private void onBtnEvaluarAnteproyectosClicked() {
        if (!(usuario instanceof Coordinator)) {
            mostrarAlerta("Acceso denegado", "Solo los coordinadores pueden acceder a esta funcionalidad.", AlertType.WARNING);
            return;
        }
        cargarVistaConUsuario("/co/unicauca/workflow/ManagementCoordinatorFormatA.fxml", "Gestión de Anteproyectos - Coordinador");
    }

    // ==== MÉTODOS AUXILIARES ====

    private void cargarVistaConUsuario(String fxml, String tituloVentana) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            // Pasar usuario al nuevo controlador si existe setUsuario
            Object controller = loader.getController();
            if (controller != null) {
                try {
                    controller.getClass().getMethod("setUsuario", User.class).invoke(controller, usuario);
                } catch (Exception e) {
                    System.out.println("El controlador no tiene setUsuario(User): " + controller.getClass().getSimpleName());
                }
            }

            Stage stage = (Stage) btnRol.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(tituloVentana);

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Error al cargar: " + e.getMessage(), AlertType.ERROR);
        }
    }

    private void configurarBotones() {
        btnRol.setVisible(true);
        btnAnteproyectoDocente.setVisible(false);
        btnFormatoDocente.setVisible(false);
        btnFormatoEstudiante.setVisible(false);
        btnAnteproyectoEstudiante.setVisible(false);
        btnEvaluarPropuestas.setVisible(false);
        btnEvaluarAnteproyectos.setVisible(false);
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
