package co.unicauca.workflow.domain.entities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la clase SuperAdmin
 *
 * Casos validados:
 * - Creación de SuperAdmin válido
 * - Nombre vacío
 * - Apellido vacío
 * - Teléfono inválido
 * - Email inválido
 * - Contraseña corta
 * - Id inválido
 * - Aprobar y rechazar coordinador
 */
public class SuperAdminTest {

    @Test
    public void testCrearSuperAdminValido() {
        SuperAdmin admin = new SuperAdmin(1, "Laura", "Gomez",
                "3216549870", "laura@unicauca.edu.co", "Secure123");

        assertEquals(1, admin.getId());
        assertEquals("Laura", admin.getFirstName());
        assertEquals("Gomez", admin.getLastName());
        assertEquals("3216549870", admin.getPhone());
        assertEquals("laura@unicauca.edu.co", admin.getEmail());
        assertEquals("Secure123", admin.getPassword());
    }

    @Test
    public void testNombreInvalido() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new SuperAdmin("", "Gomez", "3216549870",
                    "laura@unicauca.edu.co", "Secure123");
        });
        assertEquals("El nombre no puede estar vacío.", ex.getMessage());
    }

    @Test
    public void testApellidoInvalido() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new SuperAdmin("Laura", null, "3216549870",
                    "laura@unicauca.edu.co", "Secure123");
        });
        assertEquals("El apellido no puede estar vacío.", ex.getMessage());
    }

    @Test
    public void testTelefonoInvalido() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new SuperAdmin("Laura", "Gomez", "123ABC",
                    "laura@unicauca.edu.co", "Secure123");
        });
        assertEquals("El teléfono debe tener entre 7 y 15 dígitos numéricos.", ex.getMessage());
    }

    @Test
    public void testEmailInvalido() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new SuperAdmin("Laura", "Gomez", "3216549870",
                    "correo-invalido", "Secure123");
        });
        assertEquals("El correo electrónico no es válido.", ex.getMessage());
    }

    @Test
    public void testPasswordCorta() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new SuperAdmin("Laura", "Gomez", "3216549870",
                    "laura@unicauca.edu.co", "123");
        });
        assertEquals("La contraseña debe tener al menos 6 caracteres.", ex.getMessage());
    }

    @Test
    public void testIdInvalido() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new SuperAdmin(-5, "Laura", "Gomez",
                    "3216549870", "laura@unicauca.edu.co", "Secure123");
        });
        assertEquals("El id no puede ser negativo.", ex.getMessage());
    }

    @Test
    public void testApproveCoordinator() {
        Coordinator coordinator = new Coordinator("Ana", "Ruiz", "3123456789",
                "Ingeniería de Sistemas", "ana@unicauca.edu.co", "Ana123");
        SuperAdmin admin = new SuperAdmin("Laura", "Gomez", "3216549870",
                "laura@unicauca.edu.co", "Secure123");

        admin.approveCoordinator(coordinator);
        assertEquals("ACEPTADO", coordinator.getStatus());
    }

    @Test
    public void testRejectCoordinator() {
        Coordinator coordinator = new Coordinator("Carlos", "Lopez", "3139876543",
                "Ingeniería de Sistemas", "carlos@unicauca.edu.co", "Carlos123");
        SuperAdmin admin = new SuperAdmin("Laura", "Gomez", "3216549870",
                "laura@unicauca.edu.co", "Secure123");

        admin.rejectCoordinator(coordinator);
        assertEquals("RECHAZADO", coordinator.getStatus());
    }
}
