package co.unicauca.workflow;

import co.unicauca.workflow.access.Factory;
import co.unicauca.workflow.domain.entities.SuperAdmin;
import co.unicauca.workflow.service.AdminService;
import co.unicauca.workflow.service.UserService;
import co.unicauca.workflow.service.SessionManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.control.Alert;

import java.io.IOException;

public class HomeAdminController {

    @FXML
    private ToggleButton btnUsuario;

    @FXML
    private ToggleButton btnCoordinadores;

    private SuperAdmin usuario; // el admin logueado

    /**
     * Inicializa los eventos de botones
     */
    @FXML
    private void initialize() {
        btnCoordinadores.setOnAction(this::handleCoordinadores);
    }

    /**
     * Permite inyectar el admin desde el LoginController
     */
    public void setUsuario(SuperAdmin usuario) {
        this.usuario = usuario;
    }

    /**
     * MÉTODO DE LOGOUT
     */
    @FXML
    private void handleLogout() {
        try {
            // Limpiar la sesión
            SessionManager.clearSession();

            // Volver al Login
            Stage stage = (Stage) btnUsuario.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/unicauca/workflow/Login.fxml"));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.setTitle("Login - Workflow");

            System.out.println("Admin - Sesión cerrada exitosamente");

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cerrar sesión: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Acción del botón "Usuario" → abre la vista de Rol
     */
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

    /**
     * Acción del botón "Gestión de coordinadores"
     */
    private void handleCoordinadores(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/unicauca/workflow/ManagementAdmin.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestión de coordinadores");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar Gestión de Coordinadores", Alert.AlertType.ERROR);
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
