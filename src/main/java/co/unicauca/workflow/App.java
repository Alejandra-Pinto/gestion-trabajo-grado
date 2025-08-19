package co.unicauca.workflow;

import co.unicauca.workflow.LoginController;
import co.unicauca.workflow.RegisterController;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {
    private Stage primaryStage;
    private static App instance;
    
    @Override
    public void start(Stage primaryStage) {
        try {
            this.primaryStage = primaryStage;
            instance = this;
            
            // Configuración básica del stage
            primaryStage.setTitle("Sistema de Gestión de Trabajos de Grado - Unicauca");
            primaryStage.setResizable(false);
            
            // Mostrar la pantalla de login al iniciar
            mostrarLogin();
            
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Muestra la pantalla de login
     */
    public void mostrarLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/unicauca/proyectotrabajogrado/views/Login.fxml"));
            Parent root = loader.load();
            
            // Configurar el controlador
            LoginController controller = loader.getController();
            controller.setMainApp(this);
            
            // Configurar la escena
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.sizeToScene();
            
        } catch (Exception e) {
            System.err.println("Error al cargar la pantalla de login: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Muestra la pantalla de registro
     */
    public void mostrarRegistro() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/unicauca/proyectotrabajogrado/views/Register1.fxml"));
            Parent root = loader.load();
            
            // Configurar el controlador
            RegisterController controller = loader.getController();
            controller.setMainApp(this);
            
            // Configurar la escena
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.sizeToScene();
            
        } catch (Exception e) {
            System.err.println("Error al cargar la pantalla de registro: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Método para acceder a la instancia de la aplicación principal
     */
    public static App getInstance() {
        return instance;
    }
    
    /**
     * Retorna el stage principal
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    public static void main(String[] args) {
        launch(args);
    }

}