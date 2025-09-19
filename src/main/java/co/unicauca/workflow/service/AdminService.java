package co.unicauca.workflow.service;

import co.unicauca.workflow.access.IAdminRepository;
import co.unicauca.workflow.domain.entities.SuperAdmin;

import java.util.List;

public class AdminService {
    private IAdminRepository repo;

    public AdminService(IAdminRepository repo) {
        this.repo = repo;
    }

    public boolean registerAdmin(SuperAdmin admin) {
        return repo.save(admin);
    }

    public SuperAdmin login(String email, String password) {
        return repo.login(email, password);
    }

    public List<SuperAdmin> listAllAdmins() {
        return repo.listAll();
    }
}
