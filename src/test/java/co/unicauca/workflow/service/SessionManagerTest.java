package co.unicauca.workflow.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SessionManagerTest {

    @Test
    public void testSesionInicialVacia() {
        System.out.println("sesionInicialVacia");
        SessionManager.clearSession();
        assertFalse(SessionManager.isLoggedIn());
        assertNull(SessionManager.getCurrentUser());
        assertNull(SessionManager.getUserType());
    }

    @Test
    public void testEstablecerUsuario() {
        System.out.println("establecerUsuario");
        Object user = new Object();
        String type = "SUPERADMIN";

        SessionManager.setCurrentUser(user, type);

        assertTrue(SessionManager.isLoggedIn());
        assertEquals(user, SessionManager.getCurrentUser());
        assertEquals(type, SessionManager.getUserType());
    }

    @Test
    public void testLimpiarSesion() {
        System.out.println("limpiarSesion");
        Object user = new Object();
        SessionManager.setCurrentUser(user, "ADMIN");

        SessionManager.clearSession();

        assertFalse(SessionManager.isLoggedIn());
        assertNull(SessionManager.getCurrentUser());
        assertNull(SessionManager.getUserType());
    }
}
