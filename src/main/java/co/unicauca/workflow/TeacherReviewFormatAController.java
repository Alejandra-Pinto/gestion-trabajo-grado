package co.unicauca.workflow;

import co.unicauca.workflow.access.Factory;
import co.unicauca.workflow.access.IDegreeWorkRepository;
import co.unicauca.workflow.domain.entities.DegreeWork;
import co.unicauca.workflow.domain.entities.Teacher;
import co.unicauca.workflow.domain.entities.User;
import co.unicauca.workflow.service.AdminService;
import co.unicauca.workflow.service.DegreeWorkService;
import co.unicauca.workflow.service.SessionManager;
import co.unicauca.workflow.service.UserService;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

public class TeacherReviewFormatAController implements Initializable {

    private User usuarioActual;
    private DegreeWork formatoActual;

    @FXML private Label lblTitulo;
    @FXML private Label lblEstadoValor;
    @FXML private TextArea txtCorrecciones;
    @FXML private ToggleButton btnUsuario;
    @FXML private ToggleButton btnFormatoPropuesta;
    @FXML private ToggleButton btnAnteproyecto;
    @FXML private Button btnResubir;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtCorrecciones.setEditable(false); // No permitir editar
        txtCorrecciones.setWrapText(true);// verlo mejor
    }

    public void setUsuarioYFormato(User usuario, DegreeWork formato) {
        this.usuarioActual = usuario;
        this.formatoActual = formato;
        cargarCorrecciones();
        if (usuario instanceof Teacher) {
            cargarFormatosA(((Teacher) usuario).getEmail());
        }
    }

    public void setFormato(DegreeWork formato) {
        this.formatoActual = formato;
        cargarCorrecciones();
    }

    private void cargarCorrecciones() {
        if (formatoActual == null) {
            txtCorrecciones.setText("No se encontró un formato para este docente.");
            return;
        }

        lblTitulo.setText("Correcciones de: " + formatoActual.getTituloProyecto());
        if (formatoActual.getCorrecciones() != null && !formatoActual.getCorrecciones().isEmpty()) {
            txtCorrecciones.setText(String.join("\n\n", formatoActual.getCorrecciones()));
            lblEstadoValor.setText(formatoActual.getEstado().toString());
            switch (formatoActual.getEstado()) {
                case ACEPTADO:
                    lblEstadoValor.setTextFill(javafx.scene.paint.Color.web("#4CAF50"));
                    break;
                case NO_ACEPTADO:
                case RECHAZADO:
                    lblEstadoValor.setTextFill(javafx.scene.paint.Color.web("#F44336"));
                    break;
                default:
                    lblEstadoValor.setTextFill(javafx.scene.paint.Color.web("#e0e0e0"));
                    break;
            }
        } else {
            txtCorrecciones.setText("Este trabajo no tiene correcciones registradas.");
            lblEstadoValor.setText("Sin estado definido");
            lblEstadoValor.setTextFill(javafx.scene.paint.Color.web("#e0e0e0"));
        }

        System.out.println("Cargando correcciones para: " + (usuarioActual != null ? usuarioActual.getEmail() : "Usuario no definido"));
    }

    private void cargarFormatosA(String teacherEmail) {
        try {
            IDegreeWorkRepository repo = Factory.getInstance().getDegreeWorkRepository("sqlite");
            DegreeWorkService service = new DegreeWorkService(repo);
            List<DegreeWork> formatos = service.listarDegreeWorksPorDocente(teacherEmail);
            System.out.println("Formatos A cargados: " + formatos.size());
            // Aquí podrías actualizar una TableView o lista en la UI si la tienes
        } catch (Exception e) {
            System.err.println("Error cargando formatos A: " + e.getMessage());
            mostrarAlerta("Error", "No se pudieron cargar los formatos A: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onBtnUsuarioClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/unicauca/workflow/RolView.fxml"));
            Parent root = loader.load();

            RolController rolController = loader.getController();
            UserService userService = new UserService(Factory.getInstance().getUserRepository("sqlite"));
            AdminService adminService = new AdminService(Factory.getInstance().getAdminRepository("sqlite"));

            if (usuarioActual != null) {
                rolController.setUsuario(usuarioActual, userService, adminService);
            }

            Stage stage = (Stage) btnUsuario.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Información del Docente");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la vista de Rol: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onBtnFormatoDocenteClicked() {
        if (!(usuarioActual instanceof Teacher)) {
            mostrarAlerta("Acceso denegado", "Solo los docentes pueden acceder a esta funcionalidad.", Alert.AlertType.WARNING);
            return;
        }
        cargarVistaConUsuario("/co/unicauca/workflow/GestionPropuestaDocente.fxml", "Gestión de Formatos - Docente");
    }

    @FXML
    private void onBtnAnteproyectoDocenteClicked() {
        if (!(usuarioActual instanceof Teacher)) {
            mostrarAlerta("Acceso denegado", "Solo los docentes pueden acceder a esta funcionalidad.", Alert.AlertType.WARNING);
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/unicauca/workflow/ManagementTeacherFormatA.fxml"));
            Parent root = loader.load();

            ManagementTeacherFormatAController controller = loader.getController();
            if (controller != null) {
                controller.setUsuario(usuarioActual);
            }

            Stage stage = (Stage) btnAnteproyecto.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestión de Anteproyectos - Docente");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la vista de anteproyecto: " + e.getMessage(), Alert.AlertType.ERROR);
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

    @FXML
    private void onResubirFormato() {
        if (formatoActual == null) {
            mostrarAlerta("Error", "No hay un formato seleccionado para re-subir.", Alert.AlertType.WARNING);
            return;
        }
        if (!(usuarioActual instanceof Teacher)) {
            mostrarAlerta("Acceso denegado", "Solo los docentes pueden re-subir formatos.", Alert.AlertType.WARNING);
            return;
        }
        cargarVistaConUsuarioYFormato("/co/unicauca/workflow/ManagementTeacherFormatA.fxml", "Re-subir Formato A", formatoActual);
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void cargarVistaConUsuario(String fxml, String tituloVentana) {
        try {
            System.out.println("Cargando vista con usuario: " + fxml);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            Object controller = loader.getController();
            
            

            if (controller != null) {
                try {
                    System.out.println("Intentando setUsuario en: " + controller.getClass().getSimpleName());
                    controller.getClass().getMethod("setUsuario", User.class).invoke(controller, usuarioActual);
                } catch (Exception e) {
                    System.out.println("El controlador no tiene setUsuario(User): " + controller.getClass().getSimpleName());
                    e.printStackTrace();
                }
            } else {
                System.out.println("Controlador es null en cargarVistaConUsuario");
            }

            Stage stage = (Stage) btnUsuario.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(tituloVentana);

        } catch (IOException e) {
            System.out.println("Error al cargar el FXML en cargarVistaConUsuario: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "Error al cargar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cargarVistaConUsuarioYFormato(String fxml, String tituloVentana, DegreeWork formato) {
        try {
            System.out.println("Cargando vista con usuario y formato: " + fxml);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            Object controller = loader.getController();
            
            if (controller instanceof ManagementTeacherFormatAController) {
                ((ManagementTeacherFormatAController) controller).setFormato(formatoActual);
                ((ManagementTeacherFormatAController) controller).deshabilitarCamposFijos(); // Deshabilitar campos
            }
            
            if (controller != null) {
                try {
                    System.out.println("Intentando setUsuario en: " + controller.getClass().getSimpleName());
                    controller.getClass().getMethod("setUsuario", User.class).invoke(controller, usuarioActual);
                    if (controller instanceof ManagementTeacherFormatAController) {
                        System.out.println("Casting exitoso a ManagementTeacherFormatAController");
                        ((ManagementTeacherFormatAController) controller).setFormato(formato);
                    } else {
                        System.out.println("Controlador no es ManagementTeacherFormatAController: " + controller.getClass().getSimpleName());
                    }
                } catch (NoSuchMethodException e) {
                    System.out.println("El controlador no tiene setUsuario(User): " + controller.getClass().getSimpleName());
                    e.printStackTrace();
                } catch (Exception e) {
                    System.out.println("Error al pasar datos al controlador: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("Controlador es null en cargarVistaConUsuarioYFormato");
            }

            Stage stage = (Stage) btnUsuario.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(tituloVentana);

        } catch (IOException e) {
            System.out.println("Error al cargar el FXML en cargarVistaConUsuarioYFormato: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "Error al cargar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}