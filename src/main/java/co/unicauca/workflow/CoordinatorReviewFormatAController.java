package co.unicauca.workflow;

import co.unicauca.workflow.domain.entities.DegreeWork;
import co.unicauca.workflow.service.DegreeWorkService;
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
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextArea;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
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
    private Button btnEnviar;
    
    @FXML
    private ComboBox<String> cmbEstado;


    @Override
    public void setHostServices(HostServices hs) {
        this.hostServices = hs;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtArchivoAdjunto.setEditable(false);
        txtCartaEmpresa.setEditable(false);

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

        // Guardar correcciones solo si es NO ACEPTADO
        if ("NO ACEPTADO".equals(estadoSeleccionado)) {
            String correcciones = txtCorrecciones.getText();
            if (correcciones == null || correcciones.trim().isEmpty()) {
                mostrarAlerta("Advertencia", "Debes escribir correcciones para un NO ACEPTADO.", Alert.AlertType.WARNING);
                return;
            }
            formato.setCorrecciones(correcciones); // 🔥 actualiza en el mismo objeto
            formato.incrementNoAprobadoCount();
        }

        // Mapear a EstadoFormatoA real
        switch (estadoSeleccionado) {
            case "ACEPTADO":
                formato.setEstado(co.unicauca.workflow.domain.entities.EstadoFormatoA.ACEPTADO);
                break;
            case "NO ACEPTADO":
                formato.setEstado(co.unicauca.workflow.domain.entities.EstadoFormatoA.NO_ACEPTADO);
                break;
            case "RECHAZADO":
                formato.setEstado(co.unicauca.workflow.domain.entities.EstadoFormatoA.RECHAZADO);
                break;
        }

        
        boolean exito = degreeWorkService.actualizarFormato(formato);
        if (exito) {
            // Mostrar mensaje
            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Éxito");
            alerta.setHeaderText(null);
            alerta.setContentText("Correcciones enviadas y estado actualizado.");
            alerta.showAndWait();

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/unicauca/workflow/ManagementCoordinatorFormatA.fxml"));
                Parent root = loader.load();

                // Recuperar el controller para recargar la tabla
                ManagementCoordinatorFormatAController controller = loader.getController();
                controller.initialize(null, null); // fuerza recarga de la tabla

                // Redirigir
                txtCorrecciones.getScene().setRoot(root);

            } catch (Exception e) {
                e.printStackTrace();
                mostrarAlerta("Error", "No se pudo volver a la vista de gestión.", Alert.AlertType.ERROR);
            }
        } else {
            mostrarAlerta("Error", "No se pudo actualizar el estado.", Alert.AlertType.ERROR);
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
}
