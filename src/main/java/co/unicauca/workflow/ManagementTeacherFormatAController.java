package co.unicauca.workflow;

import co.unicauca.workflow.access.Factory;
import co.unicauca.workflow.access.IDegreeWorkRepository;
import co.unicauca.workflow.domain.entities.*;
import co.unicauca.workflow.service.DegreeWorkService;
import co.unicauca.workflow.service.UserService;
import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.application.HostServices;

public class ManagementTeacherFormatAController implements Initializable, Hostable {
    private HostServices hostServices;

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

    

    private User usuarioActual;
    
    private File archivoAdjunto;

    private DegreeWorkService service;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        

        cbModalidad.getItems().setAll("INVESTIGACION", "PRACTICA_PROFESIONAL");

        IDegreeWorkRepository repo = Factory.getInstance().getDegreeWorkRepository("sqlite");
        service = new DegreeWorkService(repo);

        // 🔹 Cargar estudiantes y profesores
        UserService userService = new UserService(Factory.getInstance().getUserRepository("sqlite"));

        List<User> estudiantes = userService.listarPorRol("STUDENT");
        List<User> profesores = userService.listarPorRol("TEACHER");

        cbEstudiante.getItems().setAll(estudiantes.stream().map(User::getEmail).toList());
        cbDirector.getItems().setAll(profesores.stream().map(User::getEmail).toList());
        cbCodirector.getItems().setAll(profesores.stream().map(User::getEmail).toList());
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

        File archivoSeleccionado = fileChooser.showOpenDialog(null);

        if (archivoSeleccionado != null) {
            // ✅ Guardamos solo la ruta en el TextField
            txtArchivoAdjunto.setText(archivoSeleccionado.getAbsolutePath());
            

            mostrarAlerta("Documento cargado",
                    "El documento \"" + archivoSeleccionado.getName() + "\" se cargó correctamente.",
                    Alert.AlertType.INFORMATION);
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
            mostrarAlerta("Archivo no encontrado", "El archivo no existe en la ruta especificada.", Alert.AlertType.ERROR);
            return;
        }
        // abre en navegador por defecto
        hostServices.showDocument(archivo.toURI().toString());
    }
    
    @FXML
    private void onGuardarFormato(ActionEvent event) {
        // Validaciones
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
            // Construir objeto DegreeWork
            DegreeWork formato = new DegreeWork(
                    cbEstudiante.getValue(), // estudiante (correo)
                    usuarioActual != null ? usuarioActual.getEmail() : "docente@default.com",
                    txtTituloTrabajo.getText(),
                    Modalidad.valueOf(cbModalidad.getValue()),
                    dpFechaActual.getValue(),
                    cbDirector.getValue(), // director (correo)
                    cbCodirector.getValue(), // codirector (correo o null)
                    txtObjetivoGeneral.getText(),
                    Arrays.asList(txtObjetivosEspecificos.getText().split(";")),
                    txtArchivoAdjunto.getText()
            );


            formato.setEstado(EstadoFormatoA.PRIMERA_EVALUACION);

            boolean creado = service.registrarFormato(formato);

            if (creado) {
                mostrarAlerta("Éxito", "Formato A registrado correctamente", Alert.AlertType.CONFIRMATION);
                limpiarCampos();

                // 🔹 Aquí agregamos la consulta a la BD para verificar
                List<DegreeWork> lista = service.listarDegreeWorks();
                System.out.println("📋 Formatos guardados en la base de datos:");
                for (DegreeWork dw : lista) {
                    System.out.println("ID: " + dw.getId()
                            + " | Estudiante: " + dw.getIdEstudiante()
                            + " | Título: " + dw.getTituloProyecto()
                            + " | Director: " + dw.getDirectorProyecto());
                }
            } else {
                mostrarAlerta("Error", "No se pudo registrar el Formato A", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error inesperado", "Ocurrió un error: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    private void limpiarCampos() {
        cbEstudiante.getSelectionModel().clearSelection(); // ✅ ComboBox en vez de TextField
        txtTituloTrabajo.clear();
        cbModalidad.getSelectionModel().clearSelection();
        dpFechaActual.setValue(LocalDate.now());
        cbDirector.getSelectionModel().clearSelection();
        cbCodirector.getSelectionModel().clearSelection();;
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
}
