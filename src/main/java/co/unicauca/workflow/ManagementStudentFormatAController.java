package co.unicauca.workflow;

import co.unicauca.workflow.access.Factory;
import co.unicauca.workflow.access.IDegreeWorkRepository;
import co.unicauca.workflow.domain.entities.DegreeWork;
import co.unicauca.workflow.domain.entities.User;
import co.unicauca.workflow.service.DegreeWorkService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ManagementStudentFormatAController implements Initializable {

    @FXML
    private Label lblTitulo;
    @FXML
    private Label lblModalidad;
    @FXML
    private Label lblFecha;
    @FXML
    private Label lblDirector;
    @FXML
    private Label lblCodirector;
    @FXML
    private TextArea txtObjetivoGeneral;
    @FXML
    private TextArea txtObjetivosEspecificos;
    @FXML
    private Label lblEstado;

    private DegreeWorkService service;
    private User usuarioActual;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Instanciar servicio igual que en Teacher
        IDegreeWorkRepository repo = Factory.getInstance().getDegreeWorkRepository("sqlite");
        service = new DegreeWorkService(repo);
    }

    public void setUsuario(User usuario) {
        this.usuarioActual = usuario;
        cargarFormatoA();
    }

    private void cargarFormatoA() {
        if (usuarioActual == null) return;

        try {
            // Consultar todos los formatos
            List<DegreeWork> formatos = service.listarDegreeWorks();

            // Buscar el formato que corresponda a este estudiante
            DegreeWork formato = formatos.stream()
                    .filter(f -> f.getIdEstudiante().equalsIgnoreCase(usuarioActual.getEmail())) // ajusta aquí según tu User
                    .findFirst()
                    .orElse(null);

            if (formato != null) {
                lblTitulo.setText(formato.getTituloProyecto());
                lblModalidad.setText(formato.getModalidad().toString());
                lblFecha.setText(formato.getFechaActual().toString());
                lblDirector.setText(formato.getDirectorProyecto());
                lblCodirector.setText(formato.getCodirectorProyecto() != null ? formato.getCodirectorProyecto() : "-");
                txtObjetivoGeneral.setText(formato.getObjetivoGeneral());
                txtObjetivosEspecificos.setText(String.join("; ", formato.getObjetivosEspecificos()));
                lblEstado.setText(formato.getEstado().toString());
            } else {
                lblTitulo.setText("No hay formato registrado");
                lblEstado.setText("Pendiente");
            }

        } catch (Exception e) {
            e.printStackTrace();
            lblEstado.setText("Error cargando datos");
        }
    }
}
