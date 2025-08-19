package co.unicauca.workflow;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;


/**
 * JavaFX App
 */
public class App extends Application {
    //private Stage primaryStage;
    //private static App instance;
    
    @Override
    public void start(Stage primaryStage)throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        
        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    

    public static void main(String[] args) {
        launch(args);
    }

}