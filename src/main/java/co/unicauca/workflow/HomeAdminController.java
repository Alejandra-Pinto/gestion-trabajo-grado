package co.unicauca.workflow;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.io.IOException;

public class HomeAdminController {

    @FXML
    private ToggleButton btnUsuario;

    @FXML
    private ToggleButton btnCoordinadores;

    @FXML
    private void initialize() {
        // Acción al presionar "Gestión de coordinadores"
        btnCoordinadores.setOnAction(this::handleCoordinadores);
    }

    /**
     * MÉTODO DE LOGOUT
     */
    @FXML
    private void handleLogout() {
        try {
            // Limpiar la sesión
            co.unicauca.workflow.service.SessionManager.clearSession();
            
            // Volver al Login
            Stage stage = (Stage) btnUsuario.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/unicauca/workflow/Login.fxml"));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.setTitle("Login - Workflow");
            
            System.out.println("Admin - Sesión cerrada exitosamente");
            
        } catch (IOException e) {
            e.printStackTrace();
            // Manejar error
            System.err.println("Error al cerrar sesión: " + e.getMessage());
        }
    }

    private void handleCoordinadores(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/unicauca/workflow/ManagementAdmin.fxml"));
            Parent root = loader.load();

            // Cambiar toda la escena
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestión de coordinadores");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}