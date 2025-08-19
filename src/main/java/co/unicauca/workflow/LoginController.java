/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package co.unicauca.workflow;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
//import co.unicauca.proyectotrabajogrado.App;

/**
 * FXML Controller class
 *
 * @author julia
 */
public class LoginController implements Initializable {

    private App mainApp;

    @FXML
    private TextField txtCorreo;
    @FXML
    private PasswordField txtContrasena;
    @FXML
    private RadioButton rbEstudiante;
    @FXML
    private RadioButton rbDocente;
    @FXML
    private ToggleGroup rolGroup;

    /**
     * Establece la referencia a la aplicación principal
     */
    public void setMainApp(App mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * Maneja el evento de inicio de sesión
     */
    @FXML
    private void handleLogin() {
        String correo = txtCorreo.getText().trim();
        String contrasena = txtContrasena.getText().trim();
        String rol = rbEstudiante.isSelected() ? "Estudiante" : "Docente";

        if (correo.isEmpty() || contrasena.isEmpty()) {
            mostrarAlerta("Error", "Correo y contraseña son obligatorios.");
            return;
        }

        if (!validarFormatoCorreo(correo)) {
            mostrarAlerta("Error", "Por favor ingrese un correo institucional válido (@unicauca.edu.co)");
            return;
        }

        // Aquí iría la lógica de autenticación real
        boolean autenticado = autenticarUsuario(correo, contrasena, rol);
        
        if (autenticado) {
            mostrarAlerta("Éxito", "Bienvenid@ " + rol + "!\nSesión iniciada correctamente.");
            // Aquí iría la navegación a la pantalla principal correspondiente
        } else {
            mostrarAlerta("Error", "Credenciales incorrectas o usuario no existe.");
        }
    }

    /**
     * Navega a la pantalla de registro
     */
    @FXML
    private void handleIrARegistro() {
        try {
            mainApp.mostrarRegistro();
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo cargar la pantalla de registro: " + e.getMessage());
        }
    }

    /**
     * Valida el formato del correo institucional
     */
    private boolean validarFormatoCorreo(String correo) {
        return correo.matches("^[A-Za-z0-9._%+-]+@unicauca\\.edu\\.co$");
    }

    /**
     * Simula la autenticación del usuario (debes implementar la real)
     */
    private boolean autenticarUsuario(String correo, String contrasena, String rol) {
        // TODO: Conectar con el servicio real de autenticación
        // Esto es solo un ejemplo temporal
        return !contrasena.isEmpty() && correo.contains("@unicauca.edu.co");
    }

    /**
     * Muestra una alerta al usuario
     */
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configurar el grupo de radio buttons
        rbEstudiante.setToggleGroup(rolGroup);
        rbDocente.setToggleGroup(rolGroup);
        rbEstudiante.setSelected(true);
    } 
}
