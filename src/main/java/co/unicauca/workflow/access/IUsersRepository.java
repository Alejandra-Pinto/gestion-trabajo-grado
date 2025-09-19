package co.unicauca.workflow.access;

import co.unicauca.workflow.domain.entities.*;
import java.util.List;


public interface IUsersRepository {
    boolean save(User user);

    User findByEmail(String email);

    User login(String email, String password);

    List<User> listAll();
    
    public boolean updateCoordinatorStatus(String email, String newStatus);
}
