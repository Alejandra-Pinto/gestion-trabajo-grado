package co.unicauca.workflow;

import java.io.IOException;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    private static Scene scene;
    private static Stage primaryStage;
    private static HostServices hostServices;

    @Override
    public void start(Stage stage) throws IOException {
        hostServices = getHostServices();
        primaryStage = stage;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/unicauca/workflow/Login.fxml"));
        Parent root = loader.load();

        scene = new Scene(root, 1540, 800);
        stage.setScene(scene);
        stage.setTitle("Login - Workflow");
        stage.show();
    }

    public static HostServices getHostServicesInstance() {
        return hostServices;
    }
    
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        Parent root = fxmlLoader.load();

        Object controller = fxmlLoader.getController();
        if (controller instanceof Hostable) {
            ((Hostable) controller).setHostServices(hostServices);
        }

        return root;
    }

    /**
     * MÉTODO CORREGIDO: Cargar una vista dentro del BaseLayout
     */
    public static void loadViewInBaseLayout(String fxmlName) throws IOException {
        // Cargar el BaseLayout
        FXMLLoader baseLoader = new FXMLLoader(App.class.getResource("/co/unicauca/workflow/BaseLayout.fxml"));
        Parent root = baseLoader.load();
        BaseLayoutController baseController = baseLoader.getController();
        
        // Cargar la vista específica dentro del BaseLayout
        baseController.loadContent(fxmlName); // Método corregido
        
        // Actualizar la escena
        scene.setRoot(root);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch();
    }
}