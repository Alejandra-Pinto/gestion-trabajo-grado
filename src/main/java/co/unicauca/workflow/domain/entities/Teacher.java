package co.unicauca.workflow.domain.entities;


public class Teacher extends User {

    public Teacher(String firstName, String lastName, String phone, String program,
                   String email, String password) {
        super(firstName, lastName, phone, program, email, password, "TEACHER");
    }

    @Override
    public void showDashboard() {
        System.out.println("Acceso al panel de docente:");
        System.out.println(" - Evaluar anteproyectos");
        System.out.println(" - Evaluar monografías");
    }

    // Métodos adicionales específicos del docente
    public void evaluateProposal() {
        System.out.println("Evaluando anteproyecto...");
    }

    public void evaluateMonograph() {
        System.out.println("Evaluando monografía...");
    }
}

