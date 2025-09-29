package co.unicauca.workflow.domain.entities;

import java.util.regex.Pattern;

public class SuperAdmin {
    private int id;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String password;

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public SuperAdmin(int id, String firstName, String lastName,
                      String phone, String email, String password) {
        setId(id);
        setFirstName(firstName);
        setLastName(lastName);
        setPhone(phone);
        setEmail(email);
        setPassword(password);
    }

    // Constructor sin id (para registro)
    public SuperAdmin(String firstName, String lastName,
                      String phone, String email, String password) {
        this(-1, firstName, lastName, phone, email, password);
    }

    // Getters y Setters con validaciones
    public int getId() { return id; }
    public void setId(int id) {
        if (id < -1) {
            throw new IllegalArgumentException("El id no puede ser negativo.");
        }
        this.id = id;
    }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        this.firstName = firstName.trim();
    }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido no puede estar vacío.");
        }
        this.lastName = lastName.trim();
    }

    public String getPhone() { return phone; }
    public void setPhone(String phone) {
        if (phone != null && !phone.matches("\\d{7,15}")) {
            throw new IllegalArgumentException("El teléfono debe tener entre 7 y 15 dígitos numéricos.");
        }
        this.phone = phone;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("El correo electrónico no es válido.");
        }
        this.email = email.trim().toLowerCase();
    }

    public String getPassword() { return password; }
    public void setPassword(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.");
        }
        this.password = password;
    }

    // Funcionalidades propias
    public void approveCoordinator(Coordinator coordinator) {
        coordinator.setStatus("ACEPTADO");
        System.out.println("Coordinador " + coordinator.getEmail() + " aprobado.");
    }

    public void rejectCoordinator(Coordinator coordinator) {
        coordinator.setStatus("RECHAZADO");
        System.out.println("Coordinador " + coordinator.getEmail() + " rechazado.");
    }
}
