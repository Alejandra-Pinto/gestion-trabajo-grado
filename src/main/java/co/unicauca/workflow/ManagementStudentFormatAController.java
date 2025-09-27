package co.unicauca.workflow;

import co.unicauca.workflow.access.Factory;
import co.unicauca.workflow.access.IDegreeWorkRepository;
import co.unicauca.workflow.domain.entities.DegreeWork;
import co.unicauca.workflow.domain.entities.User;
import co.unicauca.workflow.service.AdminService;
import co.unicauca.workflow.service.DegreeWorkService;
import co.unicauca.workflow.service.SessionManager;
import co.unicauca.workflow.service.UserService;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ManagementStudentFormatAController implements Initializable {

    @FXML private Label lblTituloValor;
    @FXML private Label lblModalidadValor;
    @FXML private Label lblFechaValor;
    @FXML private Label lblDirectorValor;
    @FXML private Label lblCodirectorValor;
    @FXML private TextArea txtObjGeneral;
    @FXML private TextArea txtObjEspecificos;
    @FXML private Label lblEstadoValor;
    @FXML private Button btnVerCorrecciones; 
    @FXML private ToggleButton btnUsuario;
    
    private DegreeWorkService service;
    private User usuarioActual;
    private DegreeWork formatoActual; // 🔹 Guardamos el formato encontrado

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        IDegreeWorkRepository repo = Factory.getInstance().getDegreeWorkRepository("sqlite");
        service = new DegreeWorkService(repo);

        // ✅ Tomar usuario de sesión
        usuarioActual = (User) SessionManager.getCurrentUser();
        cargarFormatoA();
    }

    private void cargarFormatoA() {
        if (usuarioActual == null) {
            lblTituloValor.setText("Error: sin sesión activa");
            lblEstadoValor.setText("-");
            return;
        }

        try {
            List<DegreeWork> formatos = service.listarDegreeWorks();

            formatoActual = formatos.stream()
                    .filter(f -> f.getEstudiante() != null
                    && f.getEstudiante().getEmail().equalsIgnoreCase(usuarioActual.getEmail()))
                    .findFirst()
                    .orElse(null);

            if (formatoActual != null) {
                lblTituloValor.setText(formatoActual.getTituloProyecto());
                lblModalidadValor.setText(formatoActual.getModalidad().toString());
                lblFechaValor.setText(formatoActual.getFechaActual().toString());
                lblDirectorValor.setText(
                        formatoActual.getDirectorProyecto() != null ? formatoActual.getDirectorProyecto().getEmail() : "-"
                );
                lblCodirectorValor.setText(
                        formatoActual.getCodirectorProyecto()!= null ? formatoActual.getCodirectorProyecto().getEmail() : "-"
                );

                txtObjGeneral.setText(formatoActual.getObjetivoGeneral());
                txtObjEspecificos.setText(String.join("; ", formatoActual.getObjetivosEspecificos()));
                lblEstadoValor.setText(formatoActual.getEstado().toString());
            } else {
                lblTituloValor.setText("No hay trabajo registrado");
                lblModalidadValor.setText("-");
                lblFechaValor.setText("-");
                lblDirectorValor.setText("-");
                lblCodirectorValor.setText("-");
                txtObjGeneral.setText("El estudiante aún no ha subido el formato de grado.");
                txtObjEspecificos.setText("-");
                lblEstadoValor.setText("Pendiente");
            }

        } catch (Exception e) {
            e.printStackTrace();
            lblTituloValor.setText("Error cargando datos");
            lblEstadoValor.setText("-");
        }
    }

    @FXML
    private void onBtnVerCorreccionesClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("StudentReviewFormatA.fxml"));
            Parent root = loader.load();

            StudentReviewFormatAController controller = loader.getController();

            if (usuarioActual != null && formatoActual != null) {
                controller.setUsuarioYFormato(usuarioActual, formatoActual);
            }

            Stage stage = (Stage) btnVerCorrecciones.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Correcciones del Formato A");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la vista de correcciones: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
    
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
            if (usuarioActual != null) {
                rolController.setUsuario(usuarioActual, userService, adminService);
            }

            Stage stage = (Stage) btnUsuario.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Información del Usuario");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la vista de Rol: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    @FXML
    private void handleLogout() {
        try {
            SessionManager.clearSession();

            Stage stage = (Stage) btnUsuario.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/unicauca/workflow/Login.fxml"));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.setTitle("Login - Workflow");

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cerrar sesión: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

}
