package co.unicauca.workflow;

import co.unicauca.workflow.domain.entities.*;
import co.unicauca.workflow.service.UserService;
import co.unicauca.workflow.service.AdminService;
import co.unicauca.workflow.service.SessionManager;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ToggleButton;

public class RolController {

    @FXML
    private Label lblTipo;
    @FXML
    private Label lblNombre;
    @FXML
    private Label lblEmail;
    @FXML
    private Label lblPrograma;
    @FXML
    private Label lblRol;

    @FXML
    private ToggleButton btnRol; // Referencia al botón "Rol" del panel izquierdo

    private Object usuario; // Puede ser User o SuperAdmin
    private UserService userService;
    private AdminService adminService;

    /**
     * Método para setear usuarios normales (Student, Teacher, Coordinator).
     */
    public void setUsuario(User usuario, UserService userService, AdminService adminService) {
        this.usuario = usuario;
        this.userService = userService;
        this.adminService = adminService;
        cargarInformacionUsuario();
    }

    /**
     * Método para setear un SuperAdmin.
     */
    public void setAdmin(SuperAdmin admin, UserService userService, AdminService adminService) {
        this.usuario = admin;
        this.userService = userService;
        this.adminService = adminService;
        cargarInformacionAdmin();
    }

    /**
     * Carga la información de usuarios normales.
     */
    private void cargarInformacionUsuario() {
        if (usuario instanceof Student student) {
            lblTipo.setText("Estudiante");
            lblNombre.setText(student.getFirstName() + " " + student.getLastName());
            lblEmail.setText(student.getEmail());
            lblPrograma.setText(student.getProgram());
            lblRol.setText(student.getRole());

        } else if (usuario instanceof Teacher teacher) {
            lblTipo.setText("Docente");
            lblNombre.setText(teacher.getFirstName() + " " + teacher.getLastName());
            lblEmail.setText(teacher.getEmail());
            lblPrograma.setText(teacher.getProgram());
            lblRol.setText(teacher.getRole());

        } else if (usuario instanceof Coordinator coord) {
            lblTipo.setText("Coordinador");
            lblNombre.setText(coord.getFirstName() + " " + coord.getLastName());
            lblEmail.setText(coord.getEmail());
            lblPrograma.setText(coord.getProgram());
            lblRol.setText(coord.getRole());
        }
    }

    /**
     * Carga la información del administrador.
     */
    private void cargarInformacionAdmin() {
        if (usuario instanceof SuperAdmin admin) {
            lblTipo.setText("Administrador");
            lblNombre.setText(admin.getFirstName() + " " + admin.getLastName());
            lblEmail.setText(admin.getEmail());
            lblPrograma.setText("N/A");
            lblRol.setText("SuperAdmin");
        }
    }

    /**
     * Acción del botón "Volver" → Regresa a Home.fxml
     */
    @FXML
    private void handleVolver() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/unicauca/workflow/Home.fxml"));
            Parent root = loader.load();

            HomeController homeController = loader.getController();
            if (usuario instanceof User u) {
                homeController.setUsuario(u);
            }

            Stage stage = (Stage) lblTipo.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Home - Workflow");

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo volver al Home", Alert.AlertType.ERROR);
        }
    }

    /**
     * Acción del botón "Cerrar Sesión" → Regresa al Login.fxml
     */
    @FXML
    private void handleLogout() {
        try {
            // Limpiar sesión
            SessionManager.clearSession();

            Stage stage = (Stage) lblTipo.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/unicauca/workflow/Login.fxml"));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.setTitle("Login - Workflow");

            System.out.println("Sesión cerrada exitosamente");
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cerrar sesión: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Acción del botón "Rol" → Vuelve a mostrar la información del usuario/admin
     */
    @FXML
    private void onBtnRolClicked() {
        if (usuario instanceof SuperAdmin) {
            cargarInformacionAdmin();
        } else {
            cargarInformacionUsuario();
        }
    }

    /**
     * Mostrar alertas
     */
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
