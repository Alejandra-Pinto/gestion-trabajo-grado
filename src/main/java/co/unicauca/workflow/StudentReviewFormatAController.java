package co.unicauca.workflow;

import co.unicauca.workflow.domain.entities.User;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

public class StudentReviewFormatAController implements Initializable {

    private User usuarioActual;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Aquí puedes inicializar componentes si es necesario
    }

    /**
     * Método para recibir el usuario actual desde la ventana anterior
     */
    public void setUsuario(User usuario) {
        this.usuarioActual = usuario;
        // Aquí puedes cargar la información de las correcciones según el usuario
        cargarCorrecciones();
    }

    private void cargarCorrecciones() {
        // TODO: implementar la lógica para traer las correcciones del formato A
        // usando usuarioActual
        System.out.println("Cargando correcciones para: " + usuarioActual.getEmail());
    }
}
