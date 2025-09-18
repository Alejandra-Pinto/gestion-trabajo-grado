package co.unicauca.workflow;

import co.unicauca.workflow.access.Factory;
import co.unicauca.workflow.access.IUsersRepository;
import co.unicauca.workflow.access.SQLiteRepository;
import co.unicauca.workflow.domain.entities.Student;
import co.unicauca.workflow.domain.entities.Teacher;
import co.unicauca.workflow.domain.entities.User;
import co.unicauca.workflow.service.UserService;
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

public class RegisterController implements Initializable {

    @FXML
    private TextField txt_nombre;
    @FXML
    private TextField txt_apellido;
    @FXML
    private ComboBox<String> cbx_programa;
    @FXML
    private TextField txt_email;
    @FXML
    private PasswordField txt_password;
    @FXML
    private PasswordField txt_confirmPassword;
    @FXML
    private CheckBox chk_estudiante;
    @FXML
    private CheckBox chk_docente;
    @FXML
    private Button btn_register;
    @FXML
    private Hyperlink hpl_login;

    @FXML
    private void onRegister(ActionEvent event) {
        String nombre = txt_nombre.getText().trim();
        String apellido = txt_apellido.getText().trim();
        String programa = cbx_programa.getValue(); // ahora el programa viene del combo
        String correo = txt_email.getText().trim();
        String pass = txt_password.getText();
        String confirmPass = txt_confirmPassword.getText();

        // Validaciones básicas
        if (nombre.isEmpty() || apellido.isEmpty() || programa == null ||
            correo.isEmpty() || pass.isEmpty() || confirmPass.isEmpty() ||
            (!chk_estudiante.isSelected() && !chk_docente.isSelected())) {
            mostrarAlerta("Error de registro", "Por favor complete todos los campos y seleccione un rol", Alert.AlertType.WARNING);
            return;
        }

        // Validación de contraseñas
        if (!pass.equals(confirmPass)) {
            mostrarAlerta("Error de registro", "Las contraseñas no coinciden", Alert.AlertType.ERROR);
            return;
        }

        // Determinar rol
        User user;
        if (chk_estudiante.isSelected() && chk_docente.isSelected()) {
            mostrarAlerta("Error de registro", "Debe seleccionar solo un rol", Alert.AlertType.WARNING);
            return;
        } else if (chk_estudiante.isSelected()) {
            user = new Student(nombre, apellido, null, programa, correo, pass);
        } else {
            user = new Teacher(nombre, apellido, null, programa, correo, pass);
        }

        // Registrar usuario con el servicio
        //SQLiteRepository repo = new SQLiteRepository();
        IUsersRepository repo = Factory.getInstance().getUserRepository("sqlite");
        UserService service = new UserService(repo);

        boolean creado = service.register(user);

        if (creado) {
            mostrarAlerta("Registro exitoso", "Usuario registrado correctamente", Alert.AlertType.CONFIRMATION);
            limpiarCampos();

            // Regresar automáticamente al login
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/unicauca/workflow/Login.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) btn_register.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Iniciar Sesión");
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mostrarAlerta("Error de registro", "No se pudo registrar el usuario. Verifique si el correo ya está en uso o si la contraseña Cumple el formato.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onGoToLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/unicauca/workflow/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) hpl_login.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Iniciar Sesión");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo volver a la ventana de login.", Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
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

    private void limpiarCampos() {
        txt_nombre.clear();
        txt_apellido.clear();
        cbx_programa.getSelectionModel().clearSelection();
        txt_email.clear();
        txt_password.clear();
        txt_confirmPassword.clear();
        chk_estudiante.setSelected(false);
        chk_docente.setSelected(false);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Aquí puedes llenar el combo de programas desde BD o manualmente
        cbx_programa.getItems().addAll(
            "Ingeniería de Sistemas",
            "Ingeniería Automática Industrial",
            "Ingeniería Electrónica y Telecomunicaciones",
            "Técnologo en Telemática"
        );
    }
}
