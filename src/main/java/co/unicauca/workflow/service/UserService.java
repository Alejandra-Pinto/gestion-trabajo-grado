package co.unicauca.workflow.service;

import co.unicauca.workflow.access.*;
import co.unicauca.workflow.domain.entities.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

public class UserService {

    private final IUsersRepository repository;

    public UserService(IUsersRepository repository) {
        this.repository = repository;
    }

    /**
     * Registrar un nuevo usuario en el sistema
     * @param user el usuario a registrar
     * @return true si el registro fue exitoso
     */
    public boolean register(User user) {
        if (!isValidEmail(user.getEmail())) {
            System.out.println("Email inválido. Debe ser institucional @unicauca.edu.co");
            return false;
        }

        if (!isValidPassword(user.getPassword())) {
            System.out.println("Contraseña insegura. Debe tener al menos 6 caracteres, un número, un caracter especial y una mayúscula.");
            return false;
        }

        // Cifrar la contraseña antes de guardarla
        user.setPassword(encryptPassword(user.getPassword()));

        return repository.save(user);
    }

    /**
     * Validar login de un usuario
     * @param email correo
     * @param password contraseña ingresada (se cifra y compara con BD)
     * @return el usuario autenticado o null si falla
     */
    public User login(String email, String password) {
        String encrypted = encryptPassword(password);
        return repository.login(email, encrypted);
    }

    /**
     * Valida que el email sea institucional
     */
    private boolean isValidEmail(String email) {
        return email != null && email.endsWith("@unicauca.edu.co");
    }

    /**
     * Valida que la contraseña cumpla con las reglas:
     * - mínimo 6 caracteres
     * - al menos un dígito
     * - al menos una mayúscula
     * - al menos un carácter especial
     */
    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 6) return false;

        String regex = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[!@#$%^&*]).+$";
        return Pattern.matches(regex, password);
    }

    /**
     * Encripta la contraseña con SHA-256
     */
    private String encryptPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error en cifrado de contraseña", e);
        }
    }
}

