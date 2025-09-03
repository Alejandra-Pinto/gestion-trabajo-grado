/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package co.unicauca.workflow;

import co.unicauca.workflow.domain.entities.User;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Dana Isabella
 */
public class HomeController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private Label lblNombre;
    @FXML
    private Label lblPrograma;
    
    
    private User usuario;
    
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
     public void setUsuario(User usuario) {
        this.usuario = usuario;
        cargarUsuario();
    }
     
    private void cargarUsuario(){
        lblNombre.setText(usuario.getFirstName() +" "+ usuario.getLastName());
        lblPrograma.setText(String.valueOf(usuario.getProgram()));
        
    }
}
