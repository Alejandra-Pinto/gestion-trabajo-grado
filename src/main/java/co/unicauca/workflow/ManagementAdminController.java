package co.unicauca.workflow;

import co.unicauca.workflow.access.UserSQLiteRepository;
import co.unicauca.workflow.domain.entities.Coordinator;
import co.unicauca.workflow.domain.entities.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class ManagementAdminController {

    @FXML
    private TableView<Coordinator> tblCoordinadores;

    @FXML
    private TableColumn<Coordinator, String> colCorreo;

    @FXML
    private TableColumn<Coordinator, String> colNombre;

    @FXML
    private TableColumn<Coordinator, String> colEstado;

    @FXML
    private Button btnModificarEstado;

    private UserSQLiteRepository repo;
    private ObservableList<Coordinator> coordinadores;

    @FXML
    public void initialize() {
        repo = new UserSQLiteRepository();

        // Configurar columnas
        colCorreo.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));
        colNombre.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getFirstName() + " " + data.getValue().getLastName()
        ));

        // Columna de estado con ComboBox
        colEstado.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus()));
        colEstado.setCellFactory(col -> {
            return new TableCell<Coordinator, String>() {
                private final ComboBox<String> combo = new ComboBox<>();

                {
                    combo.getItems().addAll("PENDIENTE", "ACEPTADO", "RECHAZADO");
                    combo.setOnAction(e -> {
                        Coordinator coord = getTableView().getItems().get(getIndex());
                        coord.setStatus(combo.getValue()); // actualizar objeto
                    });
                }

                @Override
                protected void updateItem(String estado, boolean empty) {
                    super.updateItem(estado, empty);
                    if (empty || estado == null) {
                        setGraphic(null);
                    } else {
                        combo.setValue(estado);
                        setGraphic(combo);
                    }
                }
            };
        });

        cargarCoordinadores();
    }

    private void cargarCoordinadores() {
        List<User> users = repo.listAll();
        coordinadores = FXCollections.observableArrayList();

        for (User u : users) {
            if (u instanceof Coordinator) {
                coordinadores.add((Coordinator) u);
            }
        }
        tblCoordinadores.setItems(coordinadores);
        tblCoordinadores.refresh();
    }

    @FXML
    private void onGuardarCambios() {
        boolean huboCambios = false;
        for (Coordinator c : coordinadores) {
            if (repo.updateCoordinatorStatus(c.getEmail(), c.getStatus())) {
                huboCambios = true;
            }
        }

        if (huboCambios) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Estados actualizados con éxito.", ButtonType.OK);
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "No se realizaron cambios en los estados.", ButtonType.OK);
            alert.showAndWait();
        }

        // 🔄 Recargar tabla desde BD para reflejar los cambios reales
        cargarCoordinadores();
    }
}
