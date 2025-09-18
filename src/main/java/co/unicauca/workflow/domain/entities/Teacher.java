package co.unicauca.workflow.domain.entities;


public class Teacher extends User {

    
    public Teacher(String firstName, String lastName, String phone, String program,
                   String email, String password) {
        super(firstName, lastName, phone, program, email, password, "TEACHER");
        this.setStatus("ACEPTADO");
    }

    @Override
    public void showDashboard() {
        System.out.println("Acceso al panel de docente:");
        System.out.println(" - Evaluar anteproyectos");
        System.out.println(" - Evaluar monografías");
    }

    // Métodos adicionales específicos del docente
    public void uploadFormatA() {
        System.out.println("Subiendo formato A...");
    }

    public void listStudents() {
        System.out.println("El coordinador está listando todos los estudiantes de trabajos de grado...");
    }
}

