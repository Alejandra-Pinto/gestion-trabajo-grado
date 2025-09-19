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

    // 🔹 Aquí declaramos el campo estático
    private static HostServices hostServices;

    @Override
    public void start(Stage stage) throws IOException {
        // ✅ Asignamos hostServices al arrancar la app
        hostServices = getHostServices();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/unicauca/workflow/Login.fxml"));
        Parent root = loader.load();

        scene = new Scene(root, 1540, 800);
        stage.setScene(scene);
        stage.setTitle("Login - Workflow");
        stage.show();
    }

    private Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        Parent root = fxmlLoader.load();

        // ✅ Inyectar HostServices si el controller lo soporta
        Object controller = fxmlLoader.getController();
        if (controller instanceof Hostable) {
            ((Hostable) controller).setHostServices(hostServices);
        }

        return root;
    }

    // 🔹 Método público para otros controladores
    public static HostServices getHostServicesInstance() {
        return hostServices;
    }

    public static void main(String[] args) {
        launch();
    }
}
