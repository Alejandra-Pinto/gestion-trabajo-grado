package co.unicauca.workflow.domain.entities;

public class Coordinator extends User {

    public Coordinator(String firstName, String lastName, String phone, String program,
                       String email, String password) {
        super(firstName, lastName, phone, program, email, password, "COORDINATOR");
    }

    @Override
    public void showDashboard() {
        // Aquí se define lo que hace un coordinador al iniciar sesión
        System.out.println("Mostrando panel del Coordinador para el programa: ");
        // ⚡️ En JavaFX, aquí podrías cargar CoordinatorDashboard.fxml
    }

    // 🔹 Métodos adicionales específicos del coordinador
    public void reviewFormatoA() {
        System.out.println("El coordinador está revisando un Formato A...");
    }

    public void listStudents() {
        System.out.println("El coordinador está listando todos los estudiantes de trabajos de grado...");
    }
    
}
