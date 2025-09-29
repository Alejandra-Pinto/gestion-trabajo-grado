package co.unicauca.workflow;

import co.unicauca.workflow.domain.entities.*;
import co.unicauca.workflow.service.UserService;
import co.unicauca.workflow.service.AdminService;
import co.unicauca.workflow.service.SessionManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

import java.io.IOException;

public class RolController {

    @FXML
    private Label lblTipo;
    @FXML
    private Label lblNombre;
    @FXML
    private Label lblEmail;
    @FXML
    private Label lblPrograma;
    @FXML
    private Label lblRol;

    // Botón rol principal
    @FXML
    private ToggleButton btnRol;

    // Botones Docente
    @FXML
    private ToggleButton btnFormatoDocente;
    @FXML
    private ToggleButton btnAnteproyectoDocente;

    // Botones Estudiante
    @FXML
    private ToggleButton btnFormatoEstudiante;
    @FXML
    private ToggleButton btnAnteproyectoEstudiante;

    // Botones Coordinador
    @FXML
    private ToggleButton btnEvaluarPropuestas;
    @FXML
    private ToggleButton btnEvaluarAnteproyectos;

    // Botón SuperAdmin
    @FXML
    private ToggleButton btnCoordinadores;

    private Object usuario; // Puede ser User o SuperAdmin
    private UserService userService;
    private AdminService adminService;

    /**
     * Método para setear usuarios normales (Student, Teacher, Coordinator).
     */
    public void setUsuario(User usuario, UserService userService, AdminService adminService) {
        this.usuario = usuario;
        this.userService = userService;
        this.adminService = adminService;
        cargarInformacionUsuario();
        configurarBotonesPorRol();
    }

    /**
     * Método para setear un SuperAdmin.
     */
    public void setAdmin(SuperAdmin admin, UserService userService, AdminService adminService) {
        this.usuario = admin;
        this.userService = userService;
        this.adminService = adminService;
        cargarInformacionAdmin();
        configurarBotonesPorRol();
    }

    /**
     * Carga la información de usuarios normales.
     */
    private void cargarInformacionUsuario() {
        if (usuario instanceof Student student) {
            btnRol.setText("Estudiante");
            lblTipo.setText("Estudiante");
            lblNombre.setText(student.getFirstName() + " " + student.getLastName());
            lblEmail.setText(student.getEmail());
            lblPrograma.setText(student.getProgram());
            lblRol.setText(student.getRole());

        } else if (usuario instanceof Teacher teacher) {
            btnRol.setText("Docente");
            lblTipo.setText("Docente");
            lblNombre.setText(teacher.getFirstName() + " " + teacher.getLastName());
            lblEmail.setText(teacher.getEmail());
            lblPrograma.setText(teacher.getProgram());
            lblRol.setText(teacher.getRole());

        } else if (usuario instanceof Coordinator coord) {
            btnRol.setText("Coordinador");
            lblTipo.setText("Coordinador");
            lblNombre.setText(coord.getFirstName() + " " + coord.getLastName());
            lblEmail.setText(coord.getEmail());
            lblPrograma.setText(coord.getProgram());
            lblRol.setText(coord.getRole());
        }
    }

    /**
     * Carga la información del administrador.
     */
    private void cargarInformacionAdmin() {
        if (usuario instanceof SuperAdmin admin) {
            btnRol.setText("Administrador");
            lblTipo.setText("Administrador");
            lblNombre.setText(admin.getFirstName() + " " + admin.getLastName());
            lblEmail.setText(admin.getEmail());
            lblPrograma.setText("N/A");
            lblRol.setText("SuperAdmin");
        }
    }

    /**
     * Configura qué botones se muestran según el rol del usuario.
     */
    private void configurarBotonesPorRol() {
        // Ocultar todo inicialmente
        btnFormatoDocente.setVisible(false);
        btnAnteproyectoDocente.setVisible(false);
        btnFormatoEstudiante.setVisible(false);
        btnAnteproyectoEstudiante.setVisible(false);
        btnEvaluarPropuestas.setVisible(false);
        btnEvaluarAnteproyectos.setVisible(false);
        btnCoordinadores.setVisible(false);

        if (usuario instanceof Teacher) {
            btnFormatoDocente.setVisible(true);
            btnAnteproyectoDocente.setVisible(true);
        } else if (usuario instanceof Student) {
            btnFormatoEstudiante.setVisible(true);
            btnAnteproyectoEstudiante.setVisible(true);
        } else if (usuario instanceof Coordinator) {
            btnEvaluarPropuestas.setVisible(true);
            btnEvaluarAnteproyectos.setVisible(true);
        } else if (usuario instanceof SuperAdmin) {
            btnCoordinadores.setVisible(true);
        }
    }

    /**
     * Acción del botón "Volver" Regresa a Home.fxml
     */
    @FXML
    private void handleVolver() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/unicauca/workflow/Home.fxml"));
            Parent root = loader.load();

            HomeController homeController = loader.getController();
            if (usuario instanceof User u) {
                homeController.setUsuario(u);
            }

            Stage stage = (Stage) lblTipo.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Home - Workflow");

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo volver al Home", Alert.AlertType.ERROR);
        }
    }

    /**
     * Acción del botón "Cerrar Sesión" Regresa al Login.fxml
     */
    @FXML
    private void handleLogout() {
        try {
            SessionManager.clearSession();
            Stage stage = (Stage) lblTipo.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/unicauca/workflow/Login.fxml"));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.setTitle("Login - Workflow");
            System.out.println("Sesión cerrada exitosamente");
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cerrar sesión: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Acción del botón "Rol"
     */
    @FXML
    private void onBtnRolClicked() {
        if (usuario instanceof SuperAdmin) {
            cargarInformacionAdmin();
        } else {
            cargarInformacionUsuario();
        }
    }

    @FXML
    private void onBtnFormatoDocenteClicked() {
        if (!(usuario instanceof Teacher)) {
            mostrarAlerta("Acceso denegado", "Solo los docentes pueden acceder a esta funcionalidad.", Alert.AlertType.WARNING);
            return;
        }
        cargarVistaConUsuario("/co/unicauca/workflow/GestionPropuestaDocente.fxml", "Gestión de Propuestas - Docente");
    }

    @FXML
    private void onBtnFormatoEstudianteClicked() {
        if (!(usuario instanceof Student)) {
            mostrarAlerta("Acceso denegado", "Solo los estudiantes pueden acceder a esta funcionalidad.", Alert.AlertType.WARNING);
            return;
        }
        cargarVistaConUsuario("/co/unicauca/workflow/ManagementStudentFormatA.fxml", "Gestión de Formatos - Estudiante");
    }

    @FXML
    private void onBtnEvaluarPropuestasClicked() {
        if (!(usuario instanceof Coordinator)) {
            mostrarAlerta("Acceso denegado", "Solo los coordinadores pueden acceder a esta funcionalidad.", Alert.AlertType.WARNING);
            return;
        }
        cargarVistaConUsuario("/co/unicauca/workflow/ManagementCoordinatorFormatA.fxml", "Gestión de Propuestas - Coordinador");
    }

    @FXML
    private void onBtnEvaluarAnteproyectosClicked() {
        if (!(usuario instanceof Coordinator)) {
            mostrarAlerta("Acceso denegado", "Solo los coordinadores pueden acceder a esta funcionalidad.", Alert.AlertType.WARNING);
            return;
        }
        cargarVistaConUsuario("/co/unicauca/workflow/ManagementCoordinatorFormatB.fxml", "Gestión de Anteproyectos - Coordinador");
    }

    @FXML
    private void onBtnCoordinadoresClicked() {
        if (!(usuario instanceof SuperAdmin)) {
            mostrarAlerta("Acceso denegado", "Solo el SuperAdministrador puede acceder a la gestión de administradores.", Alert.AlertType.WARNING);
            return;
        }
        cargarVistaConUsuario("/co/unicauca/workflow/ManagementAdmin.fxml", "Gestión de Administradores");
    }

    /**
     * Método genérico para cargar una vista con el usuario
     */
    private void cargarVistaConUsuario(String fxml, String tituloVentana) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        Parent root = loader.load();

        Object controller = loader.getController();
        if (controller != null) {
            try {
                if (usuario instanceof User u) {
                    controller.getClass().getMethod("setUsuario", User.class).invoke(controller, u);
                } else if (usuario instanceof SuperAdmin admin) {
                    controller.getClass().getMethod("setAdmin", SuperAdmin.class).invoke(controller, admin);
                }
            } catch (Exception e) {
                System.out.println("El controlador no tiene método compatible: " + controller.getClass().getSimpleName());
            }
        }

        Stage stage = (Stage) btnRol.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle(tituloVentana);

    } catch (IOException e) {
        e.printStackTrace();
        mostrarAlerta("Error", "Error al cargar: " + e.getMessage(), Alert.AlertType.ERROR);
    }
}


    /**
     * Mostrar alertas
     */
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
