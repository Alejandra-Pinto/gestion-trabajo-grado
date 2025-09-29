package co.unicauca.workflow.domain.entities;

import java.util.regex.Pattern;

public abstract class User {
    private String firstName;
    private String lastName;
    private String phone; // opcional
    private String program;
    private String email;
    private String password; // cifrada
    private String role; // "STUDENT" o "PROFESSOR" o "COORDINATOR"
    private String status; 

    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public User(String firstName, String lastName, String phone, String program,
                String email, String password, String role) {
        setFirstName(firstName);
        setLastName(lastName);
        setPhone(phone);
        setProgram(program);
        setEmail(email);
        setPassword(password);
        setRole(role);
        this.status = "ACEPTADO";
    }

    public User(){}

    // 🔹 Métodos getters/setters con validaciones
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        this.firstName = firstName.trim();
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido no puede estar vacío.");
        }
        this.lastName = lastName.trim();
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        if (phone != null && !phone.matches("\\d{7,15}")) {
            throw new IllegalArgumentException("El teléfono debe tener entre 7 y 15 dígitos numéricos.");
        }
        this.phone = phone;
    }

    public String getProgram() {
        return program;
    }
    public void setProgram(String program) {
        if (program == null || program.trim().isEmpty()) {
            throw new IllegalArgumentException("El programa no puede estar vacío.");
        }
        this.program = program.trim();
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        if (email == null) {
            throw new IllegalArgumentException("El correo electrónico no es válido.");
        }
        this.email = email.trim().toLowerCase();
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.");
        }
        this.password = password; // 🔒 Aquí normalmente se cifraría
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        if (role == null) {
            throw new IllegalArgumentException("Rol inválido. Debe ser STUDENT, PROFESSOR o COORDINATOR.");
        }
        this.role = role;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado no puede estar vacío.");
        }
        this.status = status;
    }

    // 🔹 Método abstracto: comportamiento que dependerá del rol
    public abstract void showDashboard();
}
