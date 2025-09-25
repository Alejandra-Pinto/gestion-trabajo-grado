package co.unicauca.workflow;

import co.unicauca.workflow.access.Factory;
import co.unicauca.workflow.access.IAdminRepository;
import co.unicauca.workflow.access.IUsersRepository;
import co.unicauca.workflow.domain.entities.User;
import co.unicauca.workflow.domain.entities.Coordinator;
import co.unicauca.workflow.domain.entities.SuperAdmin;
import co.unicauca.workflow.service.AdminService;
import co.unicauca.workflow.service.SessionManager;
import co.unicauca.workflow.service.UserService;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
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

        // Repositorios
        IUsersRepository userRepo = Factory.getInstance().getUserRepository("sqlite");
        UserService userService = new UserService(userRepo);

        IAdminRepository adminRepo = Factory.getInstance().getAdminRepository("sqlite");
        AdminService adminService = new AdminService(adminRepo);

        // 1. Primero probar con SuperAdmin
        SuperAdmin adminValido = adminService.login(usuario, contrasenia);
        if (adminValido != null) {
            SessionManager.setCurrentUser(adminValido, "Admin");
            try {
                // Cargar HomeAdmin y pasar el usuario
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/unicauca/workflow/HomeAdmin.fxml"));
                Parent root = loader.load();

                // Pasar el usuario al controlador si existe el método setUsuario
                Object controller = loader.getController();
                if (controller != null) {
                    try {
                        controller.getClass().getMethod("setUsuario", Object.class).invoke(controller, adminValido);
                    } catch (Exception e) {
                        System.out.println("El controlador no tiene método setUsuario: " + e.getMessage());
                    }
                }

                Stage stage = (Stage) btn_login.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Inicio - Super Admin");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                mostrarAlerta("Error", "No se pudo cargar la interfaz.", Alert.AlertType.ERROR);
            }
            return;
        }

        // 2. Si no, probar como usuario normal
        User valido = userService.login(usuario, contrasenia);
        if (valido != null) {
            if (valido instanceof Coordinator) {
                Coordinator coord = (Coordinator) valido;
                if ("PENDIENTE".equals(coord.getStatus())) {
                    mostrarAlerta("Solicitud en espera",
                            "Su solicitud de registro como coordinador aún está en revisión.",
                            Alert.AlertType.INFORMATION);
                    return;
                } else if ("RECHAZADO".equals(coord.getStatus())) {
                    mostrarAlerta("Solicitud rechazada",
                            "Su solicitud de registro como coordinador fue rechazada.",
                            Alert.AlertType.ERROR);
                    return;
                }
            }

            SessionManager.setCurrentUser(valido, valido.getClass().getSimpleName());
            try {
                // Cargar Home y pasar el usuario CORRECTAMENTE
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/unicauca/workflow/Home.fxml"));
                Parent root = loader.load();

                // Pasar el usuario al controlador del Home
                HomeController homeController = loader.getController();
                homeController.setUsuario(valido); // ¡ESTA LÍNEA ES CLAVE!

                Stage stage = (Stage) btn_login.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Inicio - Workflow");
                stage.show();

                System.out.println("Usuario pasado al HomeController: " + valido.getClass().getSimpleName());
            } catch (IOException e) {
                e.printStackTrace();
                mostrarAlerta("Error", "No se pudo cargar la interfaz.", Alert.AlertType.ERROR);
            }
            return;
        }

        // 3. Ninguno válido
        mostrarAlerta("Error de login", "Usuario o contraseña incorrectos.", Alert.AlertType.ERROR);
    }

    @FXML
    private void evenBtnRegister(ActionEvent event) {
        try {
            App.setRoot("Register");
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