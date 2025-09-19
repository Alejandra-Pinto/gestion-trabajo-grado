/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbproject://nbproject/nbproject.properties to edit this template
 */
package co.unicauca.workflow;

import co.unicauca.workflow.domain.entities.User;
import co.unicauca.workflow.domain.entities.Teacher;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Dana Isabella
 */
public class GestionPropuestaDocenteController implements Initializable {

    @FXML
    private VBox pnEstadoFormato;

    @FXML
    private ComboBox<String> comboClasificar;

    @FXML
    private ToggleButton btnRol;

    @FXML
    private ToggleButton btnFormatoDocente;

    @FXML
    private ToggleButton btnAnteproyectoDocente;

    private User usuario;

    // Constructor que recibe el usuario
    public GestionPropuestaDocenteController(User usuario) {
        this.usuario = usuario;
    }

    // Constructor por defecto necesario para FXML
    public GestionPropuestaDocenteController() {
        this.usuario = null;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configurar visibilidad de botones según el rol
        if (usuario instanceof Teacher) {
            btnRol.setVisible(true);
            btnFormatoDocente.setVisible(true);
            btnAnteproyectoDocente.setVisible(true);
        }

        // Configurar el ComboBox con opciones de clasificación
        comboClasificar.getItems().addAll("Todos", "Pendiente", "Aprobado", "Rechazado");
        comboClasificar.setValue("Todos"); // Valor por defecto

        // Cargar los estados al iniciar
        cargarEstados();

        // Listener para el ComboBox
        comboClasificar.setOnAction(event -> {
            String filtro = comboClasificar.getValue();
            cargarEstados(filtro);
        });
    }

    private void cargarEstados() {
        cargarEstados("Todos"); // Cargar todos los estados por defecto
    }

    private void cargarEstados(String filtro) {
        // Limpio lo que haya antes
        pnEstadoFormato.getChildren().clear();

        // Ejemplo: agregamos 5 estados de Formatos A con estados variados
        String[] estados = {"Pendiente", "Aprobado", "Rechazado", "Pendiente", "Aprobado"};
        for (int i = 1; i <= 5; i++) {
            String estadoActual = estados[i - 1];
            // Filtrar según el ComboBox
            if (filtro.equals("Todos") || estadoActual.equals(filtro)) {
                Label estado = new Label("Formato A #" + i + " - Estado: " + estadoActual);
                // Estilo mejorado
                String color = switch (estadoActual) {
                    case "Aprobado" -> "#4CAF50"; // Verde
                    case "Rechazado" -> "#F44336"; // Rojo
                    default -> "#e0e0e0"; // Gris para Pendiente
                };
                estado.setStyle("-fx-background-color: " + color + "; " +
                                "-fx-padding: 10; " +
                                "-fx-font-size: 14px; " +
                                "-fx-text-fill: white; " +
                                "-fx-border-radius: 5; " +
                                "-fx-background-radius: 5;");
                pnEstadoFormato.getChildren().add(estado);
            }
        }
    }

    // Método para establecer el usuario desde HomeController
    public void setUsuario(User usuario) {
        this.usuario = usuario;
        // Actualizar visibilidad de botones si ya está inicializado
        if (btnRol != null && btnFormatoDocente != null && btnAnteproyectoDocente != null) {
            boolean esDocente = usuario instanceof Teacher;
            btnRol.setVisible(esDocente);
            btnFormatoDocente.setVisible(esDocente);
            btnAnteproyectoDocente.setVisible(esDocente);
        }
    }
}