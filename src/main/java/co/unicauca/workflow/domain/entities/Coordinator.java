package co.unicauca.workflow.domain.entities;

public class Coordinator extends User {

    public Coordinator(String firstName, String lastName, String phone, String program,
                       String email, String password) {
        super(firstName, lastName, phone, program, email, password, "COORDINATOR");
        this.setStatus("PENDIENTE"); 
    }

    @Override
    public void showDashboard() {
        System.out.println("Mostrando panel del Coordinador para el programa: ");
    }
    
    public void reviewFormatoA() {
        System.out.println("El coordinador está revisando un Formato A...");
    }

    public void listStudents() {
        System.out.println("El coordinador está listando todos los estudiantes de trabajos de grado...");
    }
    
}
