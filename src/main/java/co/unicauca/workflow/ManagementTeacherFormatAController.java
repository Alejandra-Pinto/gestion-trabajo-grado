package co.unicauca.workflow;

import co.unicauca.workflow.domain.entities.User;
import java.io.File;
import java.net.URL;
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

    // Campos de formulario
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
            txtArchivoAdjunto.setText(archivoAdjunto.getName());
            lblEstado.setText("Enviado");

            // Alerta de éxito
            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Documento cargado");
            alerta.setHeaderText(null);
            alerta.setContentText("El documento \"" + archivoAdjunto.getName() + "\" se cargó correctamente.");
            alerta.showAndWait();
        } else {
            lblEstado.setText("No enviado");

            // Alerta de advertencia si no selecciona archivo
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Carga cancelada");
            alerta.setHeaderText(null);
            alerta.setContentText("No seleccionaste ningún archivo. Intenta nuevamente.");
            alerta.showAndWait();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblEstado.setText("No enviado");

        // Inicializamos opciones de modalidad
        cbModalidad.getItems().setAll(
            "INVESTIGACION",
            "PRACTICA_PROFESIONAL"
        );
    }
}
