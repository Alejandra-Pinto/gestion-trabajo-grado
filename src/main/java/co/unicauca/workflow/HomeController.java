package co.unicauca.workflow;

import co.unicauca.workflow.domain.entities.User;
import co.unicauca.workflow.domain.entities.Teacher;
import co.unicauca.workflow.domain.entities.Student;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ToggleButton;

public class HomeController implements Initializable {

    @FXML
    private ToggleButton btnRol; // 🔹 este es el que siempre será visible

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
        // por defecto ocultar todos menos btnRol
        btnRol.setVisible(true);
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
        String programa = usuario.getProgram() != null ? usuario.getProgram().toString() : "";

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
}
