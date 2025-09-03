package co.unicauca.workflow;

import co.unicauca.workflow.access.Factory;
import co.unicauca.workflow.access.IUsersRepository;
import co.unicauca.workflow.access.SQLiteRepository;
import co.unicauca.workflow.domain.entities.User;
import co.unicauca.workflow.service.UserService;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginController implements Initializable {

    @FXML
    private TextField txt_email;
    @FXML
    private PasswordField txt_password;
    @FXML
    private Button btn_login;
    @FXML
    private Hyperlink hpl_register;

    @FXML
    private void evenBtnIngresar(ActionEvent event) {
        String usuario = txt_email.getText().trim();
        String contrasenia = txt_password.getText().trim();

        if (usuario.isEmpty() || contrasenia.isEmpty()) {
            mostrarAlerta("Error de login", "Por favor llene todos los campos.", Alert.AlertType.WARNING);
            return;
        }

        //SQLiteRepository repo = new SQLiteRepository();
        IUsersRepository repo = Factory.getInstance().getUserRepository("sqlite");
        UserService service = new UserService(repo);

        User valido = service.login(usuario, contrasenia);

        if (valido != null) {
            mostrarAlerta("Login exitoso", "Bienvenido " + valido.getFirstName(), Alert.AlertType.INFORMATION);

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/unicauca/workflow/Home.fxml"));
                Parent root = loader.load();
                HomeController homeController = loader.getController();
                homeController.setUsuario(valido);
                Stage stage = (Stage) btn_login.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Inicio - Workflow");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                mostrarAlerta("Error", "No se pudo abrir la ventana principal.", Alert.AlertType.ERROR);
            }

        } else {
            mostrarAlerta("Error de login", "Usuario o contraseña incorrectos.", Alert.AlertType.ERROR);
        }
    }

    
    @FXML
    private void evenBtnRegister(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/unicauca/workflow/Register.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) hpl_register.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Registro de Usuario");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la ventana de registro.", Alert.AlertType.ERROR);
        }
    }
    
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);

        Label etiqueta = new Label(mensaje);
        etiqueta.setWrapText(true);
        VBox contenedor = new VBox(etiqueta);
        contenedor.setSpacing(10);
        contenedor.setPadding(new Insets(10));
        alerta.getDialogPane().setContent(contenedor);

        alerta.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicializaciones si hacen falta
    }
}
