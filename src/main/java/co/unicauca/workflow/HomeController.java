/*
 * Click nbproject://nbproject/nbproject.properties to edit this template
 */
package co.unicauca.workflow;

import co.unicauca.workflow.domain.entities.User;
import co.unicauca.workflow.domain.entities.Teacher;
import co.unicauca.workflow.domain.entities.Student;
import java.net.URL;
import java.util.ResourceBundle;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.SplitPane;
import javafx.scene.Parent;

/**
 * FXML Controller class
 *
 * @author Dana Isabella
 */
public class HomeController implements Initializable {

    @FXML
    private SplitPane splitPane;

    @FXML
    private AnchorPane contentPane;

    @FXML
    private ToggleButton btnRol; // Siempre visible

    @FXML
    private ToggleButton btnAnteproyectoDocente;
    @FXML
    private ToggleButton btnFormatoDocente;
    @FXML
    private ToggleButton btnFormatoEstudiante;
    @FXML
    private ToggleButton btnAnteproyectoEstudiante;
    @FXML
    private ToggleButton btnEvaluarPropuestas;
    @FXML
    private ToggleButton btnEvaluarAnteproyectos;

    private User usuario;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Por defecto, ocultar todos menos btnRol
        btnRol.setVisible(true);
        btnAnteproyectoDocente.setVisible(false);
        btnFormatoDocente.setVisible(false);
        btnFormatoEstudiante.setVisible(false);
        btnAnteproyectoEstudiante.setVisible(false);
        btnEvaluarPropuestas.setVisible(false);
        btnEvaluarAnteproyectos.setVisible(false);
        System.out.println("HomeController inicializado");
        cargarHome(); // Cargar la interfaz inicial
    }

    public void setUsuario(User usuario) {
        this.usuario = usuario;
        cargarUsuario();
    }

    private void cargarUsuario() {
        String programa = usuario.getProgram() != null ? usuario.getProgram().toString() : "";
        System.out.println("Cargando usuario: " + usuario.getClass().getSimpleName() + ", Programa: " + programa);

        if (usuario instanceof Teacher) {
            btnRol.setText("Docente\n(" + programa + ")");
            btnAnteproyectoDocente.setVisible(true);
            btnFormatoDocente.setVisible(true);
        } else if (usuario instanceof Student) {
            btnRol.setText("Estudiante\n(" + programa + ")");
            btnFormatoEstudiante.setVisible(true);
            btnAnteproyectoEstudiante.setVisible(true);
        } else if ("coordinador".equalsIgnoreCase(usuario.getRole())) {
            btnRol.setText("Coordinador\n(" + programa + ")");
            btnEvaluarPropuestas.setVisible(true);
            btnEvaluarAnteproyectos.setVisible(true);
        }
    }

    private void cargarHome() {
        try {
            URL fxmlUrl = getClass().getResource("HomeContent.fxml");
            if (fxmlUrl == null) {
                System.err.println("Error: No se encontró HomeContent.fxml");
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("No se pudo encontrar el archivo HomeContent.fxml");
                alert.showAndWait();
                return;
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent homeContent = loader.load();
            contentPane.getChildren().setAll(homeContent);
            AnchorPane.setTopAnchor(homeContent, 0.0);
            AnchorPane.setBottomAnchor(homeContent, 0.0);
            AnchorPane.setLeftAnchor(homeContent, 0.0);
            AnchorPane.setRightAnchor(homeContent, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cargar HomeContent.fxml: " + e.getMessage());
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Error al cargar la interfaz inicial: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void onBtnFormatoDocenteClicked() {
        System.out.println("Clic en btnFormatoDocente");
        if (!(usuario instanceof Teacher)) {
            System.out.println("Usuario no es docente, no se carga GestionPropuestaDocente.fxml");
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Acceso denegado");
            alert.setHeaderText(null);
            alert.setContentText("Solo los docentes pueden acceder a esta funcionalidad.");
            alert.showAndWait();
            return;
        }

        try {
            URL fxmlUrl = getClass().getResource("GestionPropuestaDocente.fxml");
            if (fxmlUrl == null) {
                System.err.println("Error: No se encontró GestionPropuestaDocente.fxml");
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("No se pudo encontrar el archivo GestionPropuestaDocente.fxml");
                alert.showAndWait();
                return;
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            GestionPropuestaDocenteController controller = new GestionPropuestaDocenteController(usuario, contentPane);
            loader.setController(controller);
            Parent newContent = loader.load();
            contentPane.getChildren().setAll(newContent);
            AnchorPane.setTopAnchor(newContent, 0.0);
            AnchorPane.setBottomAnchor(newContent, 0.0);
            AnchorPane.setLeftAnchor(newContent, 0.0);
            AnchorPane.setRightAnchor(newContent, 0.0);
            System.out.println("Contenido cargado exitosamente");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cargar GestionPropuestaDocente.fxml: " + e.getMessage());
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Error al cargar la interfaz de gestión de propuestas: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void onBtnAnteproyectoDocenteClicked() {
        System.out.println("Clic en btnAnteproyectoDocente");
        if (!(usuario instanceof Teacher)) {
            System.out.println("Usuario no es docente, no se carga GestionPropuestaDocente.fxml");
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Acceso denegado");
            alert.setHeaderText(null);
            alert.setContentText("Solo los docentes pueden acceder a esta funcionalidad.");
            alert.showAndWait();
            return;
        }

        try {
            URL fxmlUrl = getClass().getResource("GestionPropuestaDocente.fxml"); // Usamos el mismo FXML por ahora
            if (fxmlUrl == null) {
                System.err.println("Error: No se encontró GestionPropuestaDocente.fxml");
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("No se pudo encontrar el archivo GestionPropuestaDocente.fxml");
                alert.showAndWait();
                return;
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            GestionPropuestaDocenteController controller = new GestionPropuestaDocenteController(usuario, contentPane);
            loader.setController(controller);
            Parent newContent = loader.load();
            contentPane.getChildren().setAll(newContent);
            AnchorPane.setTopAnchor(newContent, 0.0);
            AnchorPane.setBottomAnchor(newContent, 0.0);
            AnchorPane.setLeftAnchor(newContent, 0.0);
            AnchorPane.setRightAnchor(newContent, 0.0);
            System.out.println("Contenido cargado exitosamente");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cargar GestionPropuestaDocente.fxml: " + e.getMessage());
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Error al cargar la interfaz de gestión de propuestas: " + e.getMessage());
            alert.showAndWait();
        }
    }
}