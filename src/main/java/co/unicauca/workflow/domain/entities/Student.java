package co.unicauca.workflow.domain.entities;


public class Student extends User {
    
    public Student(String firstName, String lastName, String phone, String program,
                   String email, String password) {
        super(firstName, lastName, phone, program, email, password, "STUDENT");
        this.setStatus("ACEPTADO");
    }

    

    @Override
    public void showDashboard() {
        System.out.println("Acceso al panel de estudiante:");
        System.out.println(" - Ver estado de trabajo de grado");
        System.out.println(" - Iniciar un nuevo trabajo de grado");
    }

    // Métodos adicionales específicos del estudiante
    public void startThesis() {
        System.out.println("Iniciando nuevo trabajo de grado...");
    }

    public void viewThesisStatus() {
        System.out.println("Mostrando estado actual del trabajo de grado...");
    }
}
