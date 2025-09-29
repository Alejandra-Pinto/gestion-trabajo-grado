package co.unicauca.workflow.domain.entities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la clase User
 * 
 * Se validan los siguientes casos:
 * - Creación de usuario válido
 * - Nombre vacío
 * - Apellido vacío
 * - Teléfono inválido
 * - Programa vacío
 * - Email inválido
 * - Contraseña corta
 * - Rol inválido
 * - Estado vacío
 */
public class UserTest {

    // Implementamos User como clase anónima porque es abstracta
    private User buildUser(String firstName, String lastName, String phone, String program,
                           String email, String password, String role) {
        return new User(firstName, lastName, phone, program, email, password, role) {
            @Override
            public void showDashboard() {}
        };
    }

    @Test
    public void testCrearUsuarioValido() {
        User user = buildUser("Pedro", "Perez", "3216549870", "Ingeniería de Sistemas",
                              "pedro@unicauca.edu.co", "Pedro123", "STUDENT");
        assertEquals("Pedro", user.getFirstName());
        assertEquals("Perez", user.getLastName());
        assertEquals("3216549870", user.getPhone());
        assertEquals("Ingeniería de Sistemas", user.getProgram());
        assertEquals("pedro@unicauca.edu.co", user.getEmail());
        assertEquals("Pedro123", user.getPassword());
        assertEquals("STUDENT", user.getRole());
        assertEquals("ACEPTADO", user.getStatus());
    }

    @Test
    public void testNombreInvalido() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            buildUser("", "Perez", "3216549870", "Ingeniería de Sistemas",
                      "pedro@unicauca.edu.co", "Pedro123", "STUDENT");
        });
        assertEquals("El nombre no puede estar vacío.", ex.getMessage());
    }

    @Test
    public void testApellidoInvalido() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            buildUser("Pedro", null, "3216549870", "Ingeniería de Sistemas",
                      "pedro@unicauca.edu.co", "Pedro123", "STUDENT");
        });
        assertEquals("El apellido no puede estar vacío.", ex.getMessage());
    }

    @Test
    public void testTelefonoInvalido() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            buildUser("Pedro", "Perez", "ABC123", "Ingeniería de Sistemas",
                      "pedro@unicauca.edu.co", "Pedro123", "STUDENT");
        });
        assertEquals("El teléfono debe tener entre 7 y 15 dígitos numéricos.", ex.getMessage());
    }

    @Test
    public void testProgramaInvalido() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            buildUser("Pedro", "Perez", "3216549870", "   ",
                      "pedro@unicauca.edu.co", "Pedro123", "STUDENT");
        });
        assertEquals("El programa no puede estar vacío.", ex.getMessage());
    }

    @Test
    public void testEmailInvalido() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            buildUser("Pedro", "Perez", "3216549870", "Ingeniería de Sistemas",
                      "correo-invalido", "Pedro123", "STUDENT");
        });
        assertEquals("El correo electrónico no es válido.", ex.getMessage());
    }

    @Test
    public void testPasswordCorta() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            buildUser("Pedro", "Perez", "3216549870", "Ingeniería de Sistemas",
                      "pedro@unicauca.edu.co", "123", "STUDENT");
        });
        assertEquals("La contraseña debe tener al menos 6 caracteres.", ex.getMessage());
    }

    @Test
    public void testRolInvalido() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            buildUser("Pedro", "Perez", "3216549870", "Ingeniería de Sistemas",
                      "pedro@unicauca.edu.co", "Pedro123", "ADMIN");
        });
        assertEquals("Rol inválido. Debe ser STUDENT, PROFESSOR o COORDINATOR.", ex.getMessage());
    }

    @Test
    public void testEstadoInvalido() {
        User user = buildUser("Pedro", "Perez", "3216549870", "Ingeniería de Sistemas",
                              "pedro@unicauca.edu.co", "Pedro123", "STUDENT");

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            user.setStatus("   ");
        });
        assertEquals("El estado no puede estar vacío.", ex.getMessage());
    }
}
