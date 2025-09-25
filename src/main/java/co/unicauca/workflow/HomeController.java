package co.unicauca.workflow;

import co.unicauca.workflow.domain.entities.Coordinator;
import co.unicauca.workflow.domain.entities.User;
import co.unicauca.workflow.domain.entities.Teacher;
import co.unicauca.workflow.domain.entities.Student;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class HomeController implements Initializable {

    @FXML
    private SplitPane splitPane;

    @FXML
    private AnchorPane contentPane;

    @FXML
    private ToggleButton btnRol;

    @FXML
    private ToggleButton btnAnteproyectoDocente;
    @FXML
    private ToggleButton btnFormatoDocente;
    @FXML
    private ToggleButton btnFormatoEstudiante;
    @FXML
    private ToggleButton btnAnteproyectoEstudiante;
    @FXML
    private ToggleButton btnEvaluarPropuestas;
    @FXML
    private ToggleButton btnEvaluarAnteproyectos;

    private User usuario;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("=== HomeController.initialize() ===");
        configurarBotones();
        System.out.println("HomeController inicializado - Botones configurados");
    }

    public void setUsuario(User usuario) {
        System.out.println("=== HomeController.setUsuario() llamado ===");
        System.out.println("Usuario recibido: " + (usuario != null ? usuario.getClass().getSimpleName() : "null"));
        System.out.println("Rol del usuario: " + (usuario != null ? usuario.getRole() : "null"));

        this.usuario = usuario;
        cargarUsuario();
    }

    private void cargarUsuario() {
        System.out.println("=== HomeController.cargarUsuario() ===");

        if (usuario == null) {
            System.out.println("ERROR: usuario es null");
            return;
        }

        String programa = usuario.getProgram() != null ? usuario.getProgram().toString() : "Sin programa";
        System.out.println("Cargando usuario: " + usuario.getClass().getSimpleName() + ", Programa: " + programa);
        System.out.println("Rol: " + usuario.getRole());

        // Verificar instancia con instanceof
        System.out.println("Es Teacher?: " + (usuario instanceof Teacher));
        System.out.println("Es Student?: " + (usuario instanceof Student));
        System.out.println("Es Coordinator?: " + (usuario instanceof Coordinator));

        if (usuario instanceof Teacher) {
            System.out.println("Configurando botones para DOCENTE");
            btnRol.setText("Docente\n(" + programa + ")");
            btnAnteproyectoDocente.setVisible(true);
            btnFormatoDocente.setVisible(true);

            // Verificar visibilidad de botones
            System.out.println("btnAnteproyectoDocente visible: " + btnAnteproyectoDocente.isVisible());
            System.out.println("btnFormatoDocente visible: " + btnFormatoDocente.isVisible());

        } else if (usuario instanceof Student) {
            System.out.println("Configurando botones para ESTUDIANTE");
            btnRol.setText("Estudiante\n(" + programa + ")");
            btnFormatoEstudiante.setVisible(true);
            btnAnteproyectoEstudiante.setVisible(true);

        } else if (usuario instanceof Coordinator || "coordinador".equalsIgnoreCase(usuario.getRole())) {
            System.out.println("Configurando botones para COORDINADOR");
            btnRol.setText("Coordinador\n(" + programa + ")");
            btnEvaluarPropuestas.setVisible(true);
            btnEvaluarAnteproyectos.setVisible(true);
        } else {
            System.out.println("Tipo de usuario no reconocido: " + usuario.getClass().getSimpleName());
        }

        System.out.println("=== Fin cargarUsuario() ===");
    }

    /**
     * MÉTODO DE LOGOUT INTEGRADO
     */
    @FXML
    private void handleLogout() {
        try {
            co.unicauca.workflow.service.SessionManager.clearSession();

            Stage stage = (Stage) btnRol.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/unicauca/workflow/Login.fxml"));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.setTitle("Login - Workflow");

            System.out.println("Sesión cerrada exitosamente");
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cerrar sesión: " + e.getMessage(), AlertType.ERROR);
        }
    }

    @FXML
    private void onBtnFormatoDocenteClicked() {
        System.out.println("Clic en btnFormatoDocente");

        if (!(usuario instanceof Teacher)) {
            mostrarAlerta("Acceso denegado", "Solo los docentes pueden acceder a esta funcionalidad.", AlertType.WARNING);
            return;
        }

        try {
            // Cargar la interfaz completa
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GestionPropuestaDocente.fxml"));
            Parent root = loader.load();

            // Pasar el usuario
            Object controller = loader.getController();
            if (controller != null) {
                try {
                    controller.getClass().getMethod("setUsuario", User.class).invoke(controller, usuario);
                } catch (Exception e) {
                    // Ignorar si no tiene el método
                }
            }

            // Cambiar la escena actual
            Stage stage = (Stage) btnFormatoDocente.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestión de Propuestas Docente");

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Error al cargar: " + e.getMessage(), AlertType.ERROR);
        }
    }

    // Métodos para otros botones (debes implementarlos similarmente)
    @FXML
    private void onBtnAnteproyectoDocenteClicked() {
        // Implementar similar a onBtnFormatoDocenteClicked
    }

    @FXML
    private void onBtnFormatoEstudianteClicked() {
        // Implementar similar a onBtnFormatoDocenteClicked
    }

    @FXML
    private void onBtnAnteproyectoEstudianteClicked() {
        // Implementar similar a onBtnFormatoDocenteClicked
    }

    @FXML
    private void onBtnEvaluarPropuestasClicked() {
        // Implementar similar a onBtnFormatoDocenteClicked
    }

    @FXML
    private void onBtnEvaluarAnteproyectosClicked() {
        // Implementar similar a onBtnFormatoDocenteClicked
    }

    private void configurarBotones() {
        btnRol.setVisible(true);
        btnAnteproyectoDocente.setVisible(false);
        btnFormatoDocente.setVisible(false);
        btnFormatoEstudiante.setVisible(false);
        btnAnteproyectoEstudiante.setVisible(false);
        btnEvaluarPropuestas.setVisible(false);
        btnEvaluarAnteproyectos.setVisible(false);
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
