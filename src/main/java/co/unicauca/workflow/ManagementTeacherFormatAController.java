package co.unicauca.workflow;

import co.unicauca.workflow.access.Factory;
import co.unicauca.workflow.access.IDegreeWorkRepository;
import co.unicauca.workflow.domain.entities.*;
import co.unicauca.workflow.service.AdminService;
import co.unicauca.workflow.service.DegreeWorkService;
import co.unicauca.workflow.service.SessionManager;
import co.unicauca.workflow.service.UserService;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.application.HostServices;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ManagementTeacherFormatAController implements Initializable, Hostable {
    private HostServices hostServices;
    private List<DegreeWork> formatos; // Lista de formatos para mostrar

    @Override
    public void setHostServices(HostServices hs) {
        this.hostServices = hs;
    }

    // Botones principales
    @FXML
    private Button btnAdjuntarDocumento;
    @FXML
    private ToggleButton btnUsuario;

    // Campos de formulario
    @FXML
    private ComboBox<String> cbEstudiante;
    @FXML
    private ComboBox<String> cbDirector;
    @FXML
    private ComboBox<String> cbCodirector;

    @FXML
    private TextField txtTituloTrabajo;
    @FXML
    private ComboBox<String> cbModalidad;
    @FXML
    private DatePicker dpFechaActual;
    @FXML
    private TextArea txtObjetivoGeneral;
    @FXML
    private TextArea txtObjetivosEspecificos;
    @FXML
    private TextField txtArchivoAdjunto;

    // Para la carta de aceptación
    @FXML
    private Label lblCartaAceptacion;
    @FXML
    private HBox hbCartaAceptacion;
    @FXML
    private TextField txtCartaAceptacion;
    @FXML
    private Button btnAdjuntarCarta;
    @FXML
    private Button btnAbrirCarta;

    private User usuarioActual;
    private File archivoAdjunto;
    private DegreeWork formatoActual;
    private DegreeWorkService service;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbModalidad.getItems().setAll("INVESTIGACION", "PRACTICA_PROFESIONAL");

        IDegreeWorkRepository repo = Factory.getInstance().getDegreeWorkRepository("sqlite");
        service = new DegreeWorkService(repo);

        UserService userService = new UserService(Factory.getInstance().getUserRepository("sqlite"));
        List<User> estudiantes = userService.listarPorRol("STUDENT");
        List<User> profesores = userService.listarPorRol("TEACHER");

        cbEstudiante.getItems().setAll(estudiantes.stream().map(User::getEmail).toList());
        cbDirector.getItems().setAll(profesores.stream().map(User::getEmail).toList());
        cbCodirector.getItems().setAll(profesores.stream().map(User::getEmail).toList());


        cbModalidad.valueProperty().addListener((obs, oldVal, newVal) -> {
            if ("PRACTICA_PROFESIONAL".equals(newVal)) {
                lblCartaAceptacion.setVisible(true);
                hbCartaAceptacion.setVisible(true);
            } else {
                lblCartaAceptacion.setVisible(false);
                hbCartaAceptacion.setVisible(false);
                txtCartaAceptacion.clear();
            }
        });
    }

    public void setUsuario(User usuario) {
        this.usuarioActual = usuario;
        if (usuario.getRole().equalsIgnoreCase("estudiante")) {
            btnUsuario.setText("Estudiante: " + usuario.getFirstName());
        } else if (usuario.getRole().equalsIgnoreCase("docente")) {
            btnUsuario.setText("Docente: " + usuario.getFirstName());
        } else {
            btnUsuario.setText(usuario.getFirstName());
        }
    }

    public void setFormato(DegreeWork formato) {
        this.formatoActual = formato;
        if (formato != null) {
            cargarDatosFormato(formato);
        }
    }


    private void cargarDatosFormato(DegreeWork formato) {
        cbEstudiante.setValue(formato.getEstudiante().getEmail());
        cbDirector.setValue(formato.getDirectorProyecto().getEmail());
        if (formato.getCodirectorProyecto() != null) {
            cbCodirector.setValue(formato.getCodirectorProyecto().getEmail());
        }
        txtTituloTrabajo.setText(formato.getTituloProyecto());
        cbModalidad.setValue(formato.getModalidad().name());
        dpFechaActual.setValue(formato.getFechaActual());
        txtObjetivoGeneral.setText(formato.getObjetivoGeneral());
        txtObjetivosEspecificos.setText(String.join(";", formato.getObjetivosEspecificos()));
        txtArchivoAdjunto.setText(formato.getArchivoPdf());
        if ("PRACTICA_PROFESIONAL".equals(formato.getModalidad().name())) {
            txtCartaAceptacion.setText(formato.getCartaAceptacionEmpresa());
            lblCartaAceptacion.setVisible(true);
            hbCartaAceptacion.setVisible(true);
        }
    }

    @FXML
    private void onAdjuntarDocumento(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar documento");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
                new FileChooser.ExtensionFilter("Word Files", "*.docx"),
                new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
        );

        File archivoSeleccionado = fileChooser.showOpenDialog(null);

        if (archivoSeleccionado != null) {
            try {
                File carpetaDestino = new File("Documents/formatos");
                if (!carpetaDestino.exists()) {
                    carpetaDestino.mkdirs();
                }

                File destino = new File(carpetaDestino, archivoSeleccionado.getName());
                java.nio.file.Files.copy(
                        archivoSeleccionado.toPath(),
                        destino.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );

                String rutaRelativa = "formatos/" + archivoSeleccionado.getName();
                txtArchivoAdjunto.setText(rutaRelativa);

                mostrarAlerta("Documento cargado",
                        "El documento \"" + archivoSeleccionado.getName() + "\" se copió correctamente al proyecto.",
                        Alert.AlertType.INFORMATION);

            } catch (Exception e) {
                e.printStackTrace();
                mostrarAlerta("Error", "No se pudo copiar el archivo: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            mostrarAlerta("Carga cancelada",
                    "No seleccionaste ningún archivo. Intenta nuevamente.",
                    Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void onAbrirArchivo(ActionEvent event) {
        String ruta = txtArchivoAdjunto.getText();
        if (ruta == null || ruta.isEmpty()) {
            mostrarAlerta("Sin archivo", "No hay ningún archivo seleccionado.", Alert.AlertType.WARNING);
            return;
        }
        File archivo = new File(ruta);
        if (!archivo.exists()) {
            mostrarAlerta("Archivo no encontrado", "El archivo no existe en la ruta especificada: " + ruta, Alert.AlertType.ERROR);
            return;
        }
        hostServices.showDocument(archivo.toURI().toString());
    }

    @FXML
    private void onGuardarFormato(ActionEvent event) {
        if (cbEstudiante.getValue() == null
                || txtTituloTrabajo.getText().isEmpty()
                || cbModalidad.getValue() == null
                || dpFechaActual.getValue() == null
                || cbDirector.getValue() == null
                || txtObjetivoGeneral.getText().isEmpty()
                || txtObjetivosEspecificos.getText().isEmpty()
                || txtArchivoAdjunto.getText().isEmpty()) {
            mostrarAlerta("Campos incompletos", "Por favor llene todos los campos obligatorios (*)", Alert.AlertType.WARNING);
            return;
        }

        try {
            Student estudiante = new Student();
            estudiante.setEmail(cbEstudiante.getValue());

            Teacher director = new Teacher();
            director.setEmail(cbDirector.getValue());

            Teacher codirector = null;
            if (cbCodirector.getValue() != null && !cbCodirector.getValue().isEmpty()) {
                codirector = new Teacher();
                codirector.setEmail(cbCodirector.getValue());
            }

            DegreeWork formato = new DegreeWork(
                    estudiante,
                    director,
                    txtTituloTrabajo.getText(),
                    Modalidad.valueOf(cbModalidad.getValue()),
                    dpFechaActual.getValue(),
                    codirector,
                    txtObjetivoGeneral.getText(),
                    Arrays.asList(txtObjetivosEspecificos.getText().split(";")),
                    txtArchivoAdjunto.getText()
            );

            if ("PRACTICA_PROFESIONAL".equals(cbModalidad.getValue())) {
                formato.setCartaAceptacionEmpresa(txtCartaAceptacion.getText());
            }

            // Si es una re-subida, mantener el estado actual o resetear a PRIMERA_EVALUACION
            if (formatoActual != null) {
                // 🔹 Re-subida → mantiene ID y contador
                formato.setId(formatoActual.getId());

                // Acumular intentos si ya había NO_APROBADO
                if (formatoActual.getEstado() == EstadoFormatoA.NO_ACEPTADO) {
                    formato.setNoAprobadoCount(formatoActual.getNoAprobadoCount() + 1);

                    // Si ya alcanzó 3 intentos fallidos → RECHAZADO
                    if (formato.getNoAprobadoCount() >= 3) {
                        formato.setEstado(EstadoFormatoA.RECHAZADO);
                    } else {
                        formato.setEstado(EstadoFormatoA.NO_ACEPTADO); // sigue en no aprobado
                    }
                } else {
                    // Si es una nueva evaluación, resetear estado pero conservar contador
                    formato.setEstado(EstadoFormatoA.PRIMERA_EVALUACION);
                    formato.setNoAprobadoCount(formatoActual.getNoAprobadoCount());
                }

            } else {
                // 🔹 Nuevo formato
                formato.setEstado(EstadoFormatoA.PRIMERA_EVALUACION);
                formato.setNoAprobadoCount(0);
            }

            boolean creado = service.actualizarFormato(formato); // Usar actualizar para re-subida

            if (creado) {
                mostrarAlerta("Éxito", "Formato A " + (formatoActual != null ? "actualizado" : "registrado") + " correctamente", Alert.AlertType.CONFIRMATION);
                limpiarCampos();
            } else {
                mostrarAlerta("Error", "No se pudo " + (formatoActual != null ? "actualizar" : "registrar") + " el Formato A", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error inesperado", "Ocurrió un error: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onAdjuntarCarta(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar carta de aceptación");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
                new FileChooser.ExtensionFilter("Word Files", "*.docx"),
                new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
        );

        File archivoSeleccionado = fileChooser.showOpenDialog(null);

        if (archivoSeleccionado != null) {
            try {
                File carpetaDestino = new File("Documents/cartas");
                if (!carpetaDestino.exists()) {
                    carpetaDestino.mkdirs();
                }

                File archivoDestino = new File(carpetaDestino, archivoSeleccionado.getName());
                java.nio.file.Files.copy(
                        archivoSeleccionado.toPath(),
                        archivoDestino.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );

                String rutaRelativa = "cartas/" + archivoSeleccionado.getName();
                txtCartaAceptacion.setText(rutaRelativa);

                mostrarAlerta("Documento cargado",
                        "La carta \"" + archivoSeleccionado.getName() + "\" se copió a la carpeta del proyecto.",
                        Alert.AlertType.INFORMATION);

            } catch (Exception e) {
                e.printStackTrace();
                mostrarAlerta("Error", "No se pudo copiar el archivo: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            mostrarAlerta("Carga cancelada",
                    "No seleccionaste ningún archivo. Intenta nuevamente.",
                    Alert.AlertType.WARNING);
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
            stage.setTitle("Información del Usuario");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la vista de Rol: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onAbrirCarta(ActionEvent event) {
        String ruta = txtCartaAceptacion.getText();
        if (ruta == null || ruta.isEmpty()) {
            mostrarAlerta("Sin archivo", "No hay ninguna carta seleccionada.", Alert.AlertType.WARNING);
            return;
        }
        File archivo = new File(ruta);
        if (!archivo.exists()) {
            mostrarAlerta("Archivo no encontrado", "El archivo no existe en la ruta especificada.", Alert.AlertType.ERROR);
            return;
        }
        hostServices.showDocument(archivo.toURI().toString());
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
    private void onBtnFormatoDocenteClicked() {
        if (!(usuarioActual instanceof Teacher)) {
            mostrarAlerta("Acceso denegado", "Solo los docentes pueden acceder a esta funcionalidad.", Alert.AlertType.WARNING);
            return;
        }
        cargarVistaConUsuario("/co/unicauca/workflow/GestionPropuestaDocente.fxml", "Gestión de Propuestas Docente");
    }

    private void limpiarCampos() {
        cbEstudiante.getSelectionModel().clearSelection();
        txtTituloTrabajo.clear();
        cbModalidad.getSelectionModel().clearSelection();
        dpFechaActual.setValue(LocalDate.now());
        cbDirector.getSelectionModel().clearSelection();
        cbCodirector.getSelectionModel().clearSelection();
        txtObjetivoGeneral.clear();
        txtObjetivosEspecificos.clear();
        txtArchivoAdjunto.clear();
        archivoAdjunto = null;
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

    private void cargarVistaConUsuario(String fxml, String tituloVentana) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller != null) {
                try {
                    controller.getClass().getMethod("setUsuario", User.class).invoke(controller, usuarioActual);
                } catch (Exception e) {
                    System.out.println("El controlador no tiene setUsuario(User): " + controller.getClass().getSimpleName());
                }
            }

            Stage stage = (Stage) btnUsuario.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(tituloVentana);

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Error al cargar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}