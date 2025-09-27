package co.unicauca.workflow;

import co.unicauca.workflow.domain.entities.DegreeWork;
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
    }


    public void setFormato(DegreeWork formato) {
        this.formato = formato;
        if (formato != null) {
            String estudiante = formato.getIdEstudiante() != null ? formato.getIdEstudiante() : "";
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
