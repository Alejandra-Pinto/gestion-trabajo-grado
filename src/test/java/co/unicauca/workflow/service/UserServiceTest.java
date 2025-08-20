/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package co.unicauca.workflow.service;

import co.unicauca.workflow.access.IUsersRepository;
import co.unicauca.workflow.access.SQLiteRepository;
import co.unicauca.workflow.domain.entities.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Dana Isabella
 */
public class UserServiceTest {
    
    public UserServiceTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of register method, of class UserService.
     */
    @Test
    public void testRegister() {
        System.out.println("register");
        User user = null;
        UserService instance = null;
        boolean expResult = false;
        boolean result = instance.register(user);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of login method, of class UserService.
     */
    @Test
    public void testLogin() {
        System.out.println("login");
        String email = "";
        String password = "";
        UserService instance = null;
        User expResult = null;
        User result = instance.login(email, password);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
    @Test
    public void testRegistrarUsuarioValido() throws Exception {
        System.out.println("registrarUsuarioValido");
        User nuevoUsuario = new User("Pedro", "Perez", "3216549870", "Ingeniería de Sistemas", 
                                     "pedro@unicauca.edu.co", "Pedro123!", "Estudiante") {
            @Override
            public void showDashboard() {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }
        };
        IUsersRepository repository = new SQLiteRepository();
        UserService instance = new UserService(repository);
        boolean expResult = true;
        boolean result = instance.register(nuevoUsuario);
        assertEquals(expResult, result);
    }
    
}
