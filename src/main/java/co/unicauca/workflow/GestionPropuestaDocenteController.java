/*
 * Click nbproject://nbproject/nbproject.properties to edit this template
 */
package co.unicauca.workflow;

import co.unicauca.workflow.domain.entities.User;
import co.unicauca.workflow.domain.entities.Teacher;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author Dana Isabella
 */
public class GestionPropuestaDocenteController implements Initializable {

    @FXML
    private TableView<FormatoEstado> tblEstadosFormato;

    @FXML
    private TableColumn<FormatoEstado, String> colNumeroFormato;

    @FXML
    private TableColumn<FormatoEstado, String> colEstado;

    @FXML
    private ComboBox<String> comboClasificar;

    @FXML
    private ToggleButton btnRol;

    @FXML
    private ToggleButton btnFormatoDocente;

    @FXML
    private ToggleButton btnAnteproyectoDocente;

    private User usuario;
    private AnchorPane contentPane; // Campo para el contentPane recibido

    // Clase simple para los datos de la tabla
    public static class FormatoEstado {
        private final String numeroFormato;
        private final String estado;

        public FormatoEstado(String numeroFormato, String estado) {
            this.numeroFormato = numeroFormato;
            this.estado = estado;
        }

        public String getNumeroFormato() {
            return numeroFormato;
        }

        public String getEstado() {
            return estado;
        }
    }

    // Constructor que recibe el usuario y el contentPane
    public GestionPropuestaDocenteController(User usuario, AnchorPane contentPane) {
        this.usuario = usuario;
        this.contentPane = contentPane;
    }

    // Constructor por defecto necesario para FXML (aunque no se usará directamente)
    public GestionPropuestaDocenteController() {
        this.usuario = null;
        this.contentPane = null;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("Inicializando GestionPropuestaDocenteController");
        // Configurar visibilidad de botones según el rol
        if (usuario instanceof Teacher) {
            btnRol.setVisible(true);
            btnFormatoDocente.setVisible(true);
            btnAnteproyectoDocente.setVisible(true);
            System.out.println("Usuario es Docente, mostrando botones");
        } else {
            System.out.println("Usuario no es Docente, botones ocultos");
        }

        // Configurar el ComboBox con opciones de clasificación
        comboClasificar.getItems().addAll("Todos", "Pendiente", "Aprobado", "Rechazado");
        comboClasificar.setValue("Todos"); // Valor por defecto

        // Configurar las columnas de la TableView
        colNumeroFormato.setCellValueFactory(new PropertyValueFactory<>("numeroFormato"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

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
        // Crear una lista observable para la TableView
        ObservableList<FormatoEstado> data = FXCollections.observableArrayList();

        // Ejemplo: agregamos 5 estados de Formatos A con estados variados
        String[] estados = {"Pendiente", "Aprobado", "Rechazado", "Pendiente", "Aprobado"};
        for (int i = 1; i <= 5; i++) {
            String estadoActual = estados[i - 1];
            if (filtro.equals("Todos") || estadoActual.equals(filtro)) {
                data.add(new FormatoEstado("Formato A #" + i, estadoActual));
            }
        }

        // Aplicar el estilo a las filas según el estado
        tblEstadosFormato.setRowFactory(tv -> new javafx.scene.control.TableRow<>() {
            @Override
            protected void updateItem(FormatoEstado item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else {
                    String color = switch (item.getEstado()) {
                        case "Aprobado" -> "-fx-background-color: #4CAF50;";
                        case "Rechazado" -> "-fx-background-color: #F44336;";
                        default -> "-fx-background-color: #e0e0e0;";
                    };
                    setStyle(color + " -fx-padding: 10; -fx-font-size: 14px; -fx-text-fill: white; -fx-border-radius: 5; -fx-background-radius: 5;");
                }
            }
        });

        // Actualizar la TableView con los datos
        tblEstadosFormato.setItems(data);
    }

    // Método para establecer el usuario desde HomeController (opcional, ya que se usa el constructor)
    public void setUsuario(User usuario) {
        this.usuario = usuario;
        // Actualizar visibilidad de botones si ya está inicializado
        if (btnRol != null && btnFormatoDocente != null && btnAnteproyectoDocente != null) {
            boolean esDocente = usuario instanceof Teacher;
            btnRol.setVisible(esDocente);
            btnFormatoDocente.setVisible(esDocente);
            btnAnteproyectoDocente.setVisible(esDocente);
            System.out.println("setUsuario: esDocente=" + esDocente);
        }
    }

    @FXML
    private void goToHome() {
        System.out.println("Clic en el escudo, regresando a Home");
        if (contentPane != null) {
            try {
                URL fxmlUrl = getClass().getResource("HomeContent.fxml");
                if (fxmlUrl == null) {
                    System.err.println("Error: No se encontró HomeContent.fxml");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
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
                System.out.println("Regreso a Home exitoso");
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error al cargar HomeContent.fxml: " + e.getMessage());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Error al cargar la interfaz inicial: " + e.getMessage());
                alert.showAndWait();
            }
        } else {
            System.err.println("Error: contentPane es null en goToHome");
        }
    }
}