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
 * @author USUARIO
 */
public class UserServiceTest {
    
    public UserServiceTest() {
    }
    
    @Test
    public void testRegistrarUsuarioValido() throws Exception {
        System.out.println("registrarUsuarioValido");
        User nuevoUsuario = new User("Pedro", "Perez", "3216549870", "Ingeniería de Sistemas",
                                     "pedro3@unicauca.edu.co", "@Pedro123!", "STUDENT") {
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

    @Test
    public void testRegistrarUsuarioEmailInvalido() throws Exception {
        System.out.println("registrarUsuarioEmailInvalido");
        User nuevoUsuario = new User("Maria", "Lopez", "3149876543", "Ingeniería de Sistemas",
                                     "maria@gmail.com", "Maria123!", "STUDENT") {
            @Override
            public void showDashboard() {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }
        };
        IUsersRepository repository = new SQLiteRepository();
        UserService instance = new UserService(repository);
        boolean expResult = false;
        boolean result = instance.register(nuevoUsuario);
        assertEquals(expResult, result);
    }

    @Test
    public void testRegistrarUsuarioPasswordCorta() throws Exception {
        System.out.println("registrarUsuarioPasswordCorta");
        User nuevoUsuario = new User("Laura", "Rodriguez", "3001112233", "Ingeniería de Sistemas",
                                     "laura@unicauca.edu.co", "L1!", "STUDENT") {
            @Override
            public void showDashboard() {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }
        };
        IUsersRepository repository = new SQLiteRepository();
        UserService instance = new UserService(repository);
        boolean expResult = false;
        boolean result = instance.register(nuevoUsuario);
        assertEquals(expResult, result);
    }

    @Test
    public void testRegistrarUsuarioPasswordSinNumero() throws Exception {
        System.out.println("registrarUsuarioPasswordSinNumero");
        User nuevoUsuario = new User("Andres", "Gomez", "3012223344", "Ingeniería de Sistemas",
                                     "andres@unicauca.edu.co", "Password!", "STUDENT") {
            @Override
            public void showDashboard() {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }
        };
        IUsersRepository repository = new SQLiteRepository();
        UserService instance = new UserService(repository);
        boolean expResult = false;
        boolean result = instance.register(nuevoUsuario);
        assertEquals(expResult, result);
    }

    @Test
    public void testRegistrarUsuarioPasswordSinMayuscula() throws Exception {
        System.out.println("registrarUsuarioPasswordSinMayuscula");
        User nuevoUsuario = new User("Camilo", "Diaz", "3023334455", "Ingeniería de Sistemas",
                                     "camilo@unicauca.edu.co", "password123!", "STUDENT") {
            @Override
            public void showDashboard() {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }
        };
        IUsersRepository repository = new SQLiteRepository();
        UserService instance = new UserService(repository);
        boolean expResult = false;
        boolean result = instance.register(nuevoUsuario);
        assertEquals(expResult, result);
    }

    @Test
    public void testRegistrarUsuarioPasswordSinEspecial() throws Exception {
        System.out.println("registrarUsuarioPasswordSinEspecial");
        User nuevoUsuario = new User("Luisa", "Martinez", "3034445566", "Ingeniería de Sistemas",
                                     "luisa@unicauca.edu.co", "Luisa123", "STUDENT") {
            @Override
            public void showDashboard() {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }
        };
        IUsersRepository repository = new SQLiteRepository();
        UserService instance = new UserService(repository);
        boolean expResult = false;
        boolean result = instance.register(nuevoUsuario);
        assertEquals(expResult, result);
    }

    @Test
    public void testLoginUsuarioValido() throws Exception {
        System.out.println("loginUsuarioValido");
        String email = "juan@unicauca.edu.co";
        String password = "Juan123!";
        User nuevoUsuario = new User("Juan", "Lopez", "3112223344", "Ingeniería de Sistemas",
                                     email, password, "STUDENT") {
            @Override
            public void showDashboard() {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }
        };

        IUsersRepository repository = new SQLiteRepository();
        UserService instance = new UserService(repository);

        // Registrar primero al usuario
        instance.register(nuevoUsuario);

        User result = instance.login(email, password);
        assertNotNull(result);
        assertEquals(email, result.getEmail());
    }

    @Test
    public void testLoginUsuarioInvalido() throws Exception {
        System.out.println("loginUsuarioInvalido");
        String email = "invalido@unicauca.edu.co";
        String password = "NoExiste123!";
        IUsersRepository repository = new SQLiteRepository();
        UserService instance = new UserService(repository);
        User expResult = null;
        User result = instance.login(email, password);
        assertEquals(expResult, result);
    }
}

