package co.unicauca.workflow;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert.AlertType;
import javafx.geometry.Insets;

/**
 * FXML Controller class
 *
 * @author julia
 */
public class RegisterController implements Initializable {

    // Campos del formulario
    @FXML
    private TextField txt_nombre;
    @FXML
    private TextField txt_apellido;
    @FXML
    private TextField txt_programa;
    @FXML
    private TextField txt_email;
    @FXML
    private ComboBox<String> cbx_rol;
    @FXML
    private PasswordField txt_password;
    @FXML
    private PasswordField txt_confirmPassword;
    @FXML
    private Button btn_register;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }   

    @FXML
    private void onRegister(ActionEvent event) {
        String nombre = txt_nombre.getText();
        String apellido = txt_apellido.getText();
        String programa = txt_programa.getText();
        String correo = txt_email.getText();
        String rol = cbx_rol.getValue();
        String pass = txt_password.getText();
        String confirmPass = txt_confirmPassword.getText();

        // 1. Validaciones básicas
        if (nombre.isEmpty() || apellido.isEmpty() || programa.isEmpty() || 
            correo.isEmpty() || rol == null || pass.isEmpty() || confirmPass.isEmpty()) {
            mostrarAlerta("Error de registro", "Por favor complete todos los campos", AlertType.WARNING);
            return;
        }

        // 2. Validación de contraseñas
        if (!pass.equals(confirmPass)) {
            mostrarAlerta("Error de registro", "Las contraseñas no coinciden", AlertType.ERROR);
            return;
        }

        // 3. Aquí vamos a invocar el método de la capa de servicio/repositorio
        //    (esto lo ajustamos en cuanto me digas de dónde tomar los métodos)
        // Ejemplo:
        // UserService service = new UserService(new SQLiteRepository());
        // boolean creado = service.register(new User(...));
        // if (creado) { ... } else { ... }

        mostrarAlerta("Registro", "Usuario registrado correctamente (lógica pendiente)", AlertType.INFORMATION);
    }

    private void mostrarAlerta(String titulo, String mensaje, AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);

        Label etiqueta = new Label(mensaje);
        etiqueta.setWrapText(true);
        etiqueta.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-text-fill: #2c3e50;");

        VBox contenedor = new VBox(etiqueta);
        contenedor.setSpacing(10);
        contenedor.setPadding(new Insets(10));

        alerta.getDialogPane().setContent(contenedor);

        alerta.showAndWait();
    }
}
