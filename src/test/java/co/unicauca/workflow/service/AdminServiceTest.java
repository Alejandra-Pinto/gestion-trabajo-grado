package co.unicauca.workflow.service;

import co.unicauca.workflow.access.IAdminRepository;
import co.unicauca.workflow.access.AdminSQLiteRepository;
import co.unicauca.workflow.domain.entities.SuperAdmin;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para AdminService
 *
 * Se validan los siguientes casos:
 * - Registro de SuperAdmin válido
 * - Login válido
 * - Login inválido
 * - Listar todos los SuperAdmins
 */
public class AdminServiceTest {

    public AdminServiceTest() {
    }

    @Test
    public void testRegistrarAdminValido() {
        System.out.println("registrarAdminValido");
        SuperAdmin admin = new SuperAdmin("Laura", "Gonzalez", "3112223344",
                                          "laura@unicauca.edu.co", "Laura123!");
        IAdminRepository repository = new AdminSQLiteRepository();
        AdminService service = new AdminService(repository);

        boolean expResult = true;
        boolean result = service.registerAdmin(admin);

        assertEquals(expResult, result);
    }

    @Test
    public void testLoginValido() {
        System.out.println("loginValido");
        String email = "mario@unicauca.edu.co";
        String password = "Mario123!";

        SuperAdmin admin = new SuperAdmin("Mario", "Perez", "3001112233",
                                          email, password);
        IAdminRepository repository = new AdminSQLiteRepository();
        AdminService service = new AdminService(repository);

        // Registrar primero
        service.registerAdmin(admin);

        SuperAdmin result = service.login(email, password);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
    }

    @Test
    public void testLoginInvalido() {
        System.out.println("loginInvalido");
        String email = "invalido@unicauca.edu.co";
        String password = "ClaveFalsa!";

        IAdminRepository repository = new AdminSQLiteRepository();
        AdminService service = new AdminService(repository);

        SuperAdmin result = service.login(email, password);

        assertNull(result);
    }

    @Test
    public void testListarAdmins() {
        System.out.println("listarAdmins");
        IAdminRepository repository = new AdminSQLiteRepository();
        AdminService service = new AdminService(repository);

        SuperAdmin admin1 = new SuperAdmin("Carlos", "Lopez", "3211112233",
                                           "carlos@unicauca.edu.co", "Carlos123!");
        SuperAdmin admin2 = new SuperAdmin("Ana", "Martinez", "3222223344",
                                           "ana@unicauca.edu.co", "Ana123!");

        service.registerAdmin(admin1);
        service.registerAdmin(admin2);

        List<SuperAdmin> admins = service.listAllAdmins();

        assertTrue(admins.stream().anyMatch(a -> a.getEmail().equals("carlos@unicauca.edu.co")));
        assertTrue(admins.stream().anyMatch(a -> a.getEmail().equals("ana@unicauca.edu.co")));
    }
}
