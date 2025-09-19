package co.unicauca.workflow.access;

import co.unicauca.workflow.domain.entities.SuperAdmin;
import java.util.List;

public interface IAdminRepository {
    boolean save(SuperAdmin admin);
    SuperAdmin findByEmail(String email);
    SuperAdmin login(String email, String password);
    List<SuperAdmin> listAll();
}
