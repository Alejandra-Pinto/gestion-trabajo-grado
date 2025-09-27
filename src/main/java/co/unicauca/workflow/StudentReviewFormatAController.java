package co.unicauca.workflow;

import co.unicauca.workflow.access.Factory;
import co.unicauca.workflow.domain.entities.DegreeWork;
import co.unicauca.workflow.domain.entities.Student;
import co.unicauca.workflow.domain.entities.User;
import co.unicauca.workflow.service.AdminService;
import co.unicauca.workflow.service.SessionManager;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

public class StudentReviewFormatAController implements Initializable {

    private User usuarioActual;
    private DegreeWork formatoActual;

    @FXML private Label lblTitulo;
    @FXML private Label lblEstadoValor;
    @FXML private TextArea txtCorrecciones;
    @FXML private ToggleButton btnUsuario;
    @FXML private ToggleButton btnFormatoEstudiante;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicialización si hace falta
    }

    /**
     * Método para recibir el usuario y el formato desde la ventana anterior
     */
    public void setUsuarioYFormato(User usuario, DegreeWork formato) {
        this.usuarioActual = usuario;
        this.formatoActual = formato;
        cargarCorrecciones();
    }

    private void cargarCorrecciones() {
        if (formatoActual == null) {
            txtCorrecciones.setText("No se encontró un formato para este estudiante.");
            return;
        }

        lblTitulo.setText(formatoActual.getTituloProyecto());
        // Aquí deberías extraer las correcciones desde la entidad o desde el repositorio
        if (formatoActual.getCorrecciones() != null && !formatoActual.getCorrecciones().isEmpty()) {
            txtCorrecciones.setText(String.join("\n\n", formatoActual.getCorrecciones()));
            lblEstadoValor.setText("Con correcciones");
        } else {
            txtCorrecciones.setText("Este trabajo no tiene correcciones registradas.");
            lblEstadoValor.setText("Sin correcciones");
        }

        System.out.println("Cargando correcciones para: " + usuarioActual.getEmail());
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
    private void onBtnFormatoEstudianteClicked() {
        if (!(usuarioActual instanceof Student)) {
            mostrarAlerta("Acceso denegado", "Solo los estudiantes pueden acceder a esta funcionalidad.", Alert.AlertType.WARNING);
            return;
        }
        cargarVistaConUsuario("/co/unicauca/workflow/ManagementStudentFormatA.fxml", "Gestión de Formatos - Estudiante");
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
    private void cargarVistaConUsuario(String fxml, String tituloVentana) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            // Pasar usuario al nuevo controlador si existe setUsuario
            Object controller = loader.getController();
            if (controller != null) {
                try {
                    controller.getClass().getMethod("setUsuario", User.class).invoke(controller, usuarioActual);
                } catch (Exception e) {
                    System.out.println("El controlador no tiene setUsuario(User): " + controller.getClass().getSimpleName());
                }
            }

            Stage stage = (Stage) btnUsuario.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(tituloVentana);

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Error al cargar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
