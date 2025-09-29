package co.unicauca.workflow.service;

import co.unicauca.workflow.access.IUsersRepository;
import co.unicauca.workflow.access.UserSQLiteRepository;
import co.unicauca.workflow.domain.entities.User;
import co.unicauca.workflow.service.UserService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

/**
 * Pruebas unitarias para UserService
 * 
 * Se validan los siguientes casos:
 * - Registro con datos válidos
 * - Registro inválido por email
 * - Registro inválido por password (corta, sin número, sin mayúscula, sin carácter especial)
 * - Login válido
 * - Login inválido
 * - Listar usuarios por rol
 * - Verificación de que la contraseña se almacena encriptada
 */
public class UserServiceTest {

    public UserServiceTest() {
    }

    @Test
    public void testRegistrarUsuarioValido() throws Exception {
        System.out.println("registrarUsuarioValido");
        User nuevoUsuario = new User("Pedro", "Perez", "3216549870", "Ingeniería de Sistemas",
                                     "pedro6@unicauca.edu.co", "@Pedro123!", "STUDENT") {
            @Override
            public void showDashboard() {}
        };
        IUsersRepository repository = new UserSQLiteRepository();
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
            public void showDashboard() {}
        };
        IUsersRepository repository = new UserSQLiteRepository();
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
            public void showDashboard() {}
        };
        IUsersRepository repository = new UserSQLiteRepository();
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
            public void showDashboard() {}
        };
        IUsersRepository repository = new UserSQLiteRepository();
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
            public void showDashboard() {}
        };
        IUsersRepository repository = new UserSQLiteRepository();
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
            public void showDashboard() {}
        };
        IUsersRepository repository = new UserSQLiteRepository();
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
            public void showDashboard() {}
        };

        IUsersRepository repository = new UserSQLiteRepository();
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
        IUsersRepository repository = new UserSQLiteRepository();
        UserService instance = new UserService(repository);
        User expResult = null;
        User result = instance.login(email, password);
        assertEquals(expResult, result);
    }

    @Test
    public void testListarPorRol() throws Exception {
        System.out.println("listarPorRol");
        IUsersRepository repository = new UserSQLiteRepository();
        UserService instance = new UserService(repository);

        User estudiante = new User("Carlos", "Ruiz", "3201112233", "Ingeniería de Sistemas",
                                   "carlos4@unicauca.edu.co", "Carlos123!", "STUDENT") {
            @Override
            public void showDashboard() {}
        };
        User profesor = new User("Ana", "Torres", "3211112233", "Ingeniería de Sistemas",
                                  "ana4@unicauca.edu.co", "Ana123!", "TEACHER") {
            @Override
            public void showDashboard() {}
        };

        instance.register(estudiante);
        instance.register(profesor);

        List<User> estudiantes = instance.listarPorRol("STUDENT");

        assertTrue(estudiantes.stream().anyMatch(u -> u.getEmail().equals("carlos4@unicauca.edu.co")));
        assertTrue(estudiantes.stream().noneMatch(u -> u.getEmail().equals("ana4@unicauca.edu.co")));
    }

    @Test
    public void testPasswordSeEncripta() throws Exception {
        System.out.println("passwordSeEncripta");
        String email = "sofia@unicauca.edu.co";
        String password = "Sofia123!";

        User nuevoUsuario = new User("Sofia", "Mora", "3223334455", "Ingeniería de Sistemas",
                                     email, password, "STUDENT") {
            @Override
            public void showDashboard() {}
        };

        IUsersRepository repository = new UserSQLiteRepository();
        UserService instance = new UserService(repository);

        instance.register(nuevoUsuario);

        // Verificar que la contraseña almacenada en BD no es igual a la original
        User result = instance.login(email, password);
        assertNotNull(result);
        assertNotEquals(password, result.getPassword());
    }
}
