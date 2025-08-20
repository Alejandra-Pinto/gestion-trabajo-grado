
package co.unicauca.workflow;

import co.unicauca.workflow.access.*;
import co.unicauca.workflow.domain.entities.User;
import co.unicauca.workflow.service.*;
import java.io.IOException;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author admin
 */
public class LoginController implements Initializable {

    //inicializamos los componentes
    @FXML
    private TextField txt_email;
    @FXML
    private PasswordField txt_password;
    @FXML
    private Button btn_login;
    @FXML
    private Hyperlink hpl_register;
    
    @FXML
    private void evenBtnIngresar(ActionEvent event){
        String usuario = txt_email.getText();
        String contrasenia = txt_password.getText();

        SQLiteRepository repo = new SQLiteRepository();
        UserService service = new UserService(repo);

        User valido = service.login(usuario, contrasenia);

        if (usuario == null || usuario.isEmpty() || contrasenia == null || contrasenia.isEmpty()) {
            mostrarAlerta("Error de login", "Por favor llene todos los campos requeridos para iniciar sesión", Alert.AlertType.INFORMATION);
        } else if (valido != null) {
            mostrarAlerta("Login exitoso", "Bienvenido " + usuario, Alert.AlertType.CONFIRMATION);
            // abrir otra ventana
        } else {
            mostrarAlerta("Error de login", "Usuario o contraseña incorrectos", Alert.AlertType.ERROR);
        }

        
    }
    
    @FXML
    private void evenBtnRegister(ActionEvent event) {
        try {
            // Cargar la vista de Register.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/unicauca/workflow/Register.fxml"));
            Parent root = loader.load();

            // Obtener la ventana actual (Stage) desde el Hyperlink
            Stage stage = (Stage) hpl_register.getScene().getWindow();

            // Cambiar la escena
            stage.setScene(new Scene(root));
            stage.setTitle("Registro de Usuario"); // Título de la ventana
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la ventana de registro.", Alert.AlertType.ERROR);
        }
    }

    
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);

        // Cambiar título e ícono de ventana
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);

        // Crear un Label personalizado para el mensaje
        Label etiqueta = new Label(mensaje);
        etiqueta.setWrapText(true);
        etiqueta.setStyle("-fx-font-Tebuchet MS: 14px; -fx-font-family: 'Segoe UI'; -fx-text-fill: #2c3e50;");

        // Meter el Label en un contenedor para darle padding
        VBox contenedor = new VBox(etiqueta);
        contenedor.setSpacing(10);
        contenedor.setPadding(new Insets(10));

        alerta.getDialogPane().setContent(contenedor);

        // Aplicar estilo al cuadro de diálogo completo
        alerta.getDialogPane().setStyle(
            "-fx-background-color: #f9f9f9; " +
            "-fx-border-color: #ABBEF6; " +
            "-fx-border-width: 1px; " +
            "-fx-border-radius: 5px; " +
            "-fx-background-radius: 5px;"
        );

        // Cambiar estilo de los botones
        alerta.getDialogPane().lookupButton(ButtonType.OK)
              .setStyle("-fx-background-color: #1E2C9E; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 7px;");

        alerta.showAndWait();
    }
     
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
