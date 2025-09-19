package co.unicauca.workflow.domain.entities;

public class SuperAdmin extends User {

    public SuperAdmin(String firstName, String lastName, String phone,
                      String email, String password) {
        super(firstName, lastName, phone, "ADMIN", email, password, "SUPER_ADMIN");
    }

    @Override
    public void showDashboard() {
        System.out.println("Mostrando panel de Super Administrador...");
    }

    public void approveCoordinator(Coordinator coordinator) {
        coordinator.setStatus("APROBADO");
    }

    public void rejectCoordinator(Coordinator coordinator) {
        coordinator.setStatus("RECHAZADO");
    }
}
