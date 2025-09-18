package co.unicauca.workflow;

import co.unicauca.workflow.domain.entities.*;
import co.unicauca.workflow.service.DegreeWorkService;
import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

public class ManagementTeacherFormatAController implements Initializable {

    // Botones principales
    @FXML
    private Button btnAdjuntarDocumento;
    @FXML
    private Button btnUsuario; 
    @FXML
    private Button btnGuardar; // 🔹 necesitas agregar este botón en el FXML

    // Campos de formulario
    @FXML
    private TextField txtCodEstudiante;
    @FXML
    private TextField txtTituloTrabajo;
    @FXML
    private ComboBox<String> cbModalidad;
    @FXML
    private DatePicker dpFechaActual;
    @FXML
    private TextField txtDirector;
    @FXML
    private TextField txtCodirector;
    @FXML
    private TextField txtObjetivoGeneral;
    @FXML
    private TextField txtObjetivosEspecificos;
    @FXML
    private TextField txtArchivoAdjunto;

    // Estado del documento
    @FXML
    private Label lblEstado;

    private User usuarioActual;
    private File archivoAdjunto;

    // Servicio
    private DegreeWorkService service;

    public void setService(DegreeWorkService service) {
        this.service = service;
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

    @FXML
    private void onAdjuntarDocumento(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar documento");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
            new FileChooser.ExtensionFilter("Word Files", "*.docx"),
            new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
        );

        archivoAdjunto = fileChooser.showOpenDialog(null);

        if (archivoAdjunto != null) {
            txtArchivoAdjunto.setText(archivoAdjunto.getAbsolutePath());
            lblEstado.setText("Documento cargado");
        } else {
            lblEstado.setText("No enviado");
        }
    }

    @FXML
    private void onGuardarFormato(ActionEvent event) {
        try {
            // Validaciones básicas
            if (txtCodEstudiante.getText().isEmpty() ||
                txtTituloTrabajo.getText().isEmpty() ||
                cbModalidad.getValue() == null ||
                dpFechaActual.getValue() == null ||
                txtDirector.getText().isEmpty() ||
                txtObjetivoGeneral.getText().isEmpty() ||
                archivoAdjunto == null) {
                
                Alert alerta = new Alert(Alert.AlertType.WARNING, "Por favor completa todos los campos obligatorios (*)", ButtonType.OK);
                alerta.showAndWait();
                return;
            }

            // Construir objeto DegreeWork
            DegreeWork formato = new DegreeWork(
                txtCodEstudiante.getText(), // estudiante
                usuarioActual.getEmail(),   // profesor (este usuario logueado es el docente)
                txtTituloTrabajo.getText(),
                Modalidad.valueOf(cbModalidad.getValue().toUpperCase()),
                dpFechaActual.getValue(),
                txtDirector.getText(),
                txtCodirector.getText(),
                txtObjetivoGeneral.getText(),
                Arrays.asList(txtObjetivosEspecificos.getText().split(";")), // separados por ;
                archivoAdjunto.getAbsolutePath()
            );

            formato.setEstado(EstadoFormatoA.PRIMERA_EVALUACION);

            // Guardar en BD
            boolean ok = service.registrarFormato(formato);

            if (ok) {
                lblEstado.setText("Guardado");
                Alert alerta = new Alert(Alert.AlertType.INFORMATION, "Formato A registrado con éxito", ButtonType.OK);
                alerta.showAndWait();
                limpiarFormulario();
            } else {
                Alert alerta = new Alert(Alert.AlertType.ERROR, "Error al guardar el Formato A", ButtonType.OK);
                alerta.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alerta = new Alert(Alert.AlertType.ERROR, "Ocurrió un error: " + e.getMessage(), ButtonType.OK);
            alerta.showAndWait();
        }
    }

    private void limpiarFormulario() {
        txtCodEstudiante.clear();
        txtTituloTrabajo.clear();
        cbModalidad.setValue(null);
        dpFechaActual.setValue(LocalDate.now());
        txtDirector.clear();
        txtCodirector.clear();
        txtObjetivoGeneral.clear();
        txtObjetivosEspecificos.clear();
        txtArchivoAdjunto.clear();
        archivoAdjunto = null;
        lblEstado.setText("No enviado");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblEstado.setText("No enviado");

        cbModalidad.getItems().setAll(
            "INVESTIGACION",
            "PRACTICA_PROFESIONAL"
        );

        dpFechaActual.setValue(LocalDate.now());
    }
}
