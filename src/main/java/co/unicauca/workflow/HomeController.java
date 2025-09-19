/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbproject://nbproject/nbproject.properties to edit this template
 */
package co.unicauca.workflow;

import co.unicauca.workflow.domain.entities.User;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Dana Isabella
 */
public class HomeController implements Initializable {

    @FXML
    private Label lblNombre;
    @FXML
    private Label lblPrograma;
    @FXML
    private Button btnAnteproyectoDocente;
    @FXML
    private Button btnFormatoDocente;
    @FXML
    private Button btnFormatoEstudiante;
    @FXML
    private Button btnAnteproyectoEstudiante;
    @FXML
    private Button btnEvaluarPropuestas;
    @FXML
    private Button btnEvaluarAnteproyectos;

    private User usuario;

    
    public void initialize(URL url, ResourceBundle rb) {
        // Inicializar botones como invisibles
        btnAnteproyectoDocente.setVisible(false);
        btnFormatoDocente.setVisible(false);
        btnFormatoEstudiante.setVisible(false);
        btnAnteproyectoEstudiante.setVisible(false);
        btnEvaluarPropuestas.setVisible(false);
        btnEvaluarAnteproyectos.setVisible(false);
    }

    public void setUsuario(User usuario) {
        this.usuario = usuario;
        cargarUsuario();
    }

    private void cargarUsuario() {
        lblNombre.setText(usuario.getFirstName() + " " + usuario.getLastName());
        lblPrograma.setText(String.valueOf(usuario.getProgram()));

        // Mostrar botones según el tipo de usuario
        if (usuario instanceof co.unicauca.workflow.domain.entities.Teacher) {
            // Docente
            btnAnteproyectoDocente.setVisible(true);
            btnFormatoDocente.setVisible(true);
        } else if (usuario instanceof co.unicauca.workflow.domain.entities.Student) {
            // Estudiante
            btnFormatoEstudiante.setVisible(true);
            btnAnteproyectoEstudiante.setVisible(true);
        } else if ("coordinador".equalsIgnoreCase(usuario.getRole())) {
            // Coordinador
            btnEvaluarPropuestas.setVisible(true);
            btnEvaluarAnteproyectos.setVisible(true);
        }
    }

    @FXML
    private void onBtnFormatoDocenteClicked() {
        try {
            // Cargar el FXML de GestionPropuestaDocente
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GestionPropuestaDocente.fxml"));
            // Crear instancia del controlador con el usuario
            GestionPropuestaDocenteController controller = new GestionPropuestaDocenteController(usuario);
            loader.setController(controller);
            AnchorPane root = loader.load();

            // Crear y mostrar la nueva ventana
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestión de Propuestas Docente");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}