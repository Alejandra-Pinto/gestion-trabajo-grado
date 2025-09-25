package co.unicauca.workflow;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import java.io.IOException;
import javafx.scene.Parent;

public class BaseLayoutController {
    @FXML
    private VBox mainContainer;

    @FXML
    private void handleLogout() {
        try {
            co.unicauca.workflow.service.SessionManager.clearSession();
            App.setRoot("Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método para cargar contenido manteniendo la estructura del Home
     */
    public void loadContent(String fxmlName) {
        try {
            Parent content = App.loadFXML(fxmlName);
            mainContainer.getChildren().clear();
            mainContainer.getChildren().add(content);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cargar el contenido: " + fxmlName);
        }
    }

    public VBox getMainContainer() {
        return mainContainer;
    }
}