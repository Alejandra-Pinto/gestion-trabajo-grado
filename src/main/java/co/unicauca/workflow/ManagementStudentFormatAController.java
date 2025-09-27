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

            DegreeWork formato = formatos.stream()
                    .filter(f -> f.getIdEstudiante().equalsIgnoreCase(usuarioActual.getEmail()))
                    .findFirst()
                    .orElse(null);

            if (formato != null) {
                lblTituloValor.setText(formato.getTituloProyecto());
                lblModalidadValor.setText(formato.getModalidad().toString());
                lblFechaValor.setText(formato.getFechaActual().toString());
                lblDirectorValor.setText(formato.getDirectorProyecto());
                lblCodirectorValor.setText(formato.getCodirectorProyecto() != null ? formato.getCodirectorProyecto() : "-");
                txtObjGeneral.setText(formato.getObjetivoGeneral());
                txtObjEspecificos.setText(String.join("; ", formato.getObjetivosEspecificos()));
                lblEstadoValor.setText(formato.getEstado().toString());
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
            controller.setUsuario(usuarioActual);

            Stage stage = (Stage) btnVerCorrecciones.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
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

}
