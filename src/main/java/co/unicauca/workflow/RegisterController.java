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
public class RegisterController implements Initializable {

    private App mainApp;

    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtApellidos;
    @FXML
    private TextField txtPrograma;
    @FXML
    private TextField txtCorreo;
    @FXML
    private PasswordField txtContrasena;
    @FXML
    private PasswordField txtConfirmar;
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
     * Maneja el evento de registro de usuario
     */
    @FXML
    private void handleRegistro() {
        String nombre = txtNombre.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String programa = txtPrograma.getText().trim();
        String correo = txtCorreo.getText().trim();
        String contrasena = txtContrasena.getText().trim();
        String confirmar = txtConfirmar.getText().trim();
        String rol = rbEstudiante.isSelected() ? "Estudiante" : "Docente";

        if (nombre.isEmpty() || apellidos.isEmpty() || programa.isEmpty() || 
            correo.isEmpty() || contrasena.isEmpty() || confirmar.isEmpty()) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }

        if (!contrasena.equals(confirmar)) {
            mostrarAlerta("Error", "Las contraseñas no coinciden.");
            return;
        }

        if (!validarFormatoCorreo(correo)) {
            mostrarAlerta("Error", "Por favor ingrese un correo institucional válido (@unicauca.edu.co)");
            return;
        }

        if (contrasena.length() < 8) {
            mostrarAlerta("Error", "La contraseña debe tener al menos 8 caracteres.");
            return;
        }

        // Aquí iría la conexión al servicio/DAO para registrar el usuario
        boolean registrado = registrarUsuario(nombre, apellidos, programa, correo, contrasena, rol);
        
        if (registrado) {
            mostrarAlerta("Éxito", "Usuario registrado exitosamente como " + rol);
            handleVolverALogin();
        } else {
            mostrarAlerta("Error", "No se pudo completar el registro. Intente nuevamente.");
        }
    }

    /**
     * Navega de vuelta a la pantalla de login
     */
    @FXML
    private void handleVolverALogin() {
        try {
            mainApp.mostrarLogin();
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo cargar la pantalla de login: " + e.getMessage());
        }
    }

    /**
     * Valida el formato del correo institucional
     */
    private boolean validarFormatoCorreo(String correo) {
        return correo.matches("^[A-Za-z0-9._%+-]+@unicauca\\.edu\\.co$");
    }

    /**
     * Simula el registro del usuario (debes implementar la real)
     */
    private boolean registrarUsuario(String nombre, String apellidos, String programa, 
                                   String correo, String contrasena, String rol) {
        // TODO: Conectar con el servicio real de registro
        // Esto es solo un ejemplo temporal
        return !nombre.isEmpty() && !apellidos.isEmpty() && !correo.isEmpty();
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
