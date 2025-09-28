package co.unicauca.workflow;

import co.unicauca.workflow.access.Factory;
import co.unicauca.workflow.domain.entities.Coordinator;
import co.unicauca.workflow.domain.entities.DegreeWork;
import co.unicauca.workflow.domain.entities.User;
import co.unicauca.workflow.service.AdminService;
import co.unicauca.workflow.service.DegreeWorkService;
import co.unicauca.workflow.service.SessionManager;
import co.unicauca.workflow.service.UserService;
import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;
import javafx.util.Callback;


public class CoordinatorReviewFormatAController implements Initializable, Hostable {

    private HostServices hostServices;
    private DegreeWork formato; // guardamos el seleccionado
    private static final String BASE_PATH = "Documents";


    @FXML
    private Label lblEstudiante;

    @FXML
    private TextField txtArchivoAdjunto;

    @FXML
    private Button btnAbrirArchivo;
    
    @FXML
    private Label lblCartaEmpresa;

    @FXML
    private TextField txtCartaEmpresa;

    @FXML
    private Button btnAbrirCartaEmpresa;
    
    @FXML
    private TextArea txtCorrecciones;
    
    @FXML 
    private Label lblCargarTitulo;
    
    @FXML 
    private Label lblCargarModalidad;
    
    @FXML 
    private Label lblCargarFecha;
    
    @FXML 
    private Label lblCargarDirector;
    
    @FXML 
    private Label lblCargarCodirector;
    
    @FXML 
    private Label lblCargarObjetivoGeneral;
    
    @FXML 
    private Label lblCargarObjetivosEspecificos;

    @FXML
    private Button btnEnviar;
    
    @FXML
    private ComboBox<String> cmbEstado;
    @FXML private ToggleButton btnUsuario;
    
    private User usuarioActual;

    public void setUsuarioActual(User usuario) {
        this.usuarioActual = usuario;
    }


    @Override
    public void setHostServices(HostServices hs) {
        this.hostServices = hs;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtArchivoAdjunto.setEditable(false);
        txtCartaEmpresa.setEditable(false);
        usuarioActual = (User) SessionManager.getCurrentUser();

        btnAbrirArchivo.setOnAction(e -> onAbrirArchivo());
        btnAbrirCartaEmpresa.setOnAction(e -> onAbrirCartaEmpresa());
        
        cmbEstado.getItems().clear();
        cmbEstado.getItems().addAll("ACEPTADO", "NO ACEPTADO");

        // Si ya tiene 3 intentos fallidos, habilitamos RECHAZADO
        if (formato != null && formato.getNoAprobadoCount() >= 3) {
            cmbEstado.getItems().add("RECHAZADO");
        }

        // Personalizar estilos de cada opción
        cmbEstado.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(item);
                            switch (item) {
                                case "ACEPTADO":
                                    setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                                    break;
                                case "NO ACEPTADO":
                                    setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                                    break;
                                case "RECHAZADO":
                                    setStyle("-fx-text-fill: darkred; -fx-font-weight: bold;");
                                    break;
                                default:
                                    setStyle("");
                            }
                        }
                    }
                };
            }
        });

        // Para que se vea igual en el área seleccionada
        cmbEstado.setButtonCell(cmbEstado.getCellFactory().call(null));

        // Por defecto, nada seleccionado
        cmbEstado.getSelectionModel().clearSelection();
        
        txtCorrecciones.setDisable(true);

        cmbEstado.setOnAction(e -> {
            String selected = cmbEstado.getValue();
            if ("NO ACEPTADO".equals(selected)) {
                txtCorrecciones.setDisable(false);
            } else {
                txtCorrecciones.setDisable(true);
            }
        });
  
        // Abrir la ventana de estadísticas
        javafx.application.Platform.runLater(() -> mostrarVentanaEstadisticas());
    }

    
    private DegreeWorkService degreeWorkService;

    public void setDegreeWorkService(DegreeWorkService service) {
        this.degreeWorkService = service;
    }

    
    
    public void setFormato(DegreeWork formato) {
        this.formato = formato;
        if (formato != null) {
            String estudiante = formato.getEstudiante() != null ? formato.getEstudiante().getEmail() : "";

            String modalidad = formato.getModalidad() != null ? formato.getModalidad().name() : "";
            lblEstudiante.setText(estudiante + (modalidad.isEmpty() ? "" : " - " + modalidad));

            // Archivo principal (Formato A)
            String ruta = formato.getArchivoPdf() != null ? formato.getArchivoPdf() : "";
            txtArchivoAdjunto.setText(ruta);

            // Si modalidad es PRACTICA_PROFESIONAL -> mostrar carta
            if ("PRACTICA_PROFESIONAL".equalsIgnoreCase(modalidad)) {
                String carta = formato.getCartaAceptacionEmpresa() != null ? formato.getCartaAceptacionEmpresa() : "";

                lblCartaEmpresa.setVisible(true);
                txtCartaEmpresa.setVisible(true);
                btnAbrirCartaEmpresa.setVisible(true);

                txtCartaEmpresa.setText(carta);
                System.out.println("DEBUG: ruta carta empresa -> " + carta);
            } else {
                // ocultar si no es práctica
                lblCartaEmpresa.setVisible(false);
                txtCartaEmpresa.setVisible(false);
                btnAbrirCartaEmpresa.setVisible(false);
            }
          
            lblCargarTitulo.setText(formato.getTituloProyecto() != null ? formato.getTituloProyecto() : "No hay trabajo registrado");
            lblCargarModalidad.setText(formato.getModalidad() != null ? formato.getModalidad().toString() : "No disponible");
            lblCargarFecha.setText(formato.getFechaActual() != null ? formato.getFechaActual().toString() : "No registrada");
            lblCargarDirector.setText(formato.getDirectorProyecto() != null ? formato.getDirectorProyecto().getEmail() : "No definido");
            lblCargarCodirector.setText(formato.getCodirectorProyecto() != null ? formato.getCodirectorProyecto().getEmail() : "No definido");
            lblCargarObjetivoGeneral.setText(formato.getObjetivoGeneral() != null ? formato.getObjetivoGeneral() : "No definido");
            lblCargarObjetivosEspecificos.setText((formato.getObjetivosEspecificos() != null && !formato.getObjetivosEspecificos().isEmpty())
            ? String.join("; ", formato.getObjetivosEspecificos()): "No disponibles");
        }
    }
    
    private void mostrarVentanaEstadisticas() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/unicauca/workflow/Statistics.fxml"));
            Parent root = loader.load();

            Stage estadisticasStage = new Stage();
            estadisticasStage.setScene(new Scene(root));
            estadisticasStage.setTitle("Estadísticas");

            // Que no bloquee la interacción
            estadisticasStage.initOwner(btnUsuario.getScene().getWindow());
            estadisticasStage.setResizable(false);

            // Obtener posición de la ventana principal
            Stage mainStage = (Stage) btnUsuario.getScene().getWindow();
            // Mostrar estadísticas "superpuestas" un poco a la derecha de la principal
            estadisticasStage.setX(mainStage.getX() + mainStage.getWidth() * 0.65);
            estadisticasStage.setY(mainStage.getY() + 50);

            estadisticasStage.show();
            
            StatisticsController statsController = loader.getController();
            statsController.setService(degreeWorkService); // 👈 pasa el servicio al observer

            // Hacer que se mueva junto con la principal
            mainStage.xProperty().addListener((obs, oldVal, newVal)
                    -> estadisticasStage.setX(newVal.doubleValue() + mainStage.getWidth() + 10)
            );
            mainStage.yProperty().addListener((obs, oldVal, newVal)
                    -> estadisticasStage.setY(newVal.doubleValue())
            );
            mainStage.widthProperty().addListener((obs, oldVal, newVal)
                    -> estadisticasStage.setX(mainStage.getX() + newVal.doubleValue() + 10)
            );

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la ventana de estadísticas: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onAbrirArchivo() {
        String ruta = txtArchivoAdjunto.getText();
        System.out.println("DEBUG: onAbrirArchivo ruta = " + ruta);

        if (ruta == null || ruta.isEmpty()) {
            mostrarAlerta("Sin archivo", "No hay ningún archivo adjunto para abrir.", Alert.AlertType.WARNING);
            return;
        }

        File archivo = new File("Documents", ruta); // ✅ usar carpeta del proyecto
        if (!archivo.exists()) {
            mostrarAlerta("Archivo no encontrado", "El archivo no existe en: " + archivo.getAbsolutePath(), Alert.AlertType.ERROR);
            return;
        }

        if (hostServices != null) {
            hostServices.showDocument(archivo.toURI().toString());
        } else {
            mostrarAlerta("Error", "No se pudo abrir el archivo (HostServices no inicializado).", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onAbrirCartaEmpresa() {
        String ruta = txtCartaEmpresa.getText();
        System.out.println("DEBUG: onAbrirCartaEmpresa ruta = " + ruta);

        if (ruta == null || ruta.isEmpty()) {
            mostrarAlerta("Sin archivo", "No hay carta de aceptación adjunta.", Alert.AlertType.WARNING);
            return;
        }

        File archivo = new File("Documents", ruta); // ✅ usar carpeta del proyecto
        if (!archivo.exists()) {
            mostrarAlerta("Archivo no encontrado", "El archivo no existe en: " + archivo.getAbsolutePath(), Alert.AlertType.ERROR);
            return;
        }

        if (hostServices != null) {
            hostServices.showDocument(archivo.toURI().toString());
        } else {
            mostrarAlerta("Error", "No se pudo abrir el archivo (HostServices no inicializado).", Alert.AlertType.ERROR);
        }
    }

    
    @FXML
    private void onEnviarCorrecciones() {
        if (formato == null) {
            mostrarAlerta("Error", "No hay un formato cargado.", Alert.AlertType.ERROR);
            return;
        }

        String estadoSeleccionado = cmbEstado.getValue();
        if (estadoSeleccionado == null || estadoSeleccionado.isEmpty()) {
            mostrarAlerta("Advertencia", "Debes seleccionar un estado.", Alert.AlertType.WARNING);
            return;
        }

        boolean exito = false;

        switch (estadoSeleccionado) {
            case "ACEPTADO":
                exito = degreeWorkService.cambiarEstado(formato.getId(),
                        co.unicauca.workflow.domain.entities.EstadoFormatoA.ACEPTADO);
                break;

            case "NO ACEPTADO":
                String correcciones = txtCorrecciones.getText();
                if (correcciones == null || correcciones.trim().isEmpty()) {
                    mostrarAlerta("Advertencia", "Debes escribir correcciones para un NO ACEPTADO.", Alert.AlertType.WARNING);
                    return;
                }
                formato.incrementNoAprobadoCount();
                exito = degreeWorkService.guardarCorrecciones(formato.getId(), correcciones);
                // Además se marca como NO ACEPTADO
                degreeWorkService.cambiarEstado(formato.getId(),
                        co.unicauca.workflow.domain.entities.EstadoFormatoA.NO_ACEPTADO);
                break;

            case "RECHAZADO":
                exito = degreeWorkService.rechazar(formato.getId());
                break;
        }

        if (exito) {
            mostrarAlerta("Éxito", "Correcciones enviadas y estado actualizado.", Alert.AlertType.INFORMATION);
        } else {
            mostrarAlerta("Error", "No se pudo actualizar el estado.", Alert.AlertType.ERROR);
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
    private void onBtnEvaluarPropuestasClicked() {
        if (!(usuarioActual instanceof Coordinator)) {
            mostrarAlerta("Acceso denegado", "Solo los coordinadores pueden acceder a esta funcionalidad.", Alert.AlertType.WARNING);
            return;
        }
        cargarVistaConUsuario("/co/unicauca/workflow/ManagementCoordinatorFormatA.fxml", "Gestión de Propuestas - Coordinador");
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

            // Pasar usuario al nuevo controlador si existe setUsuario
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
