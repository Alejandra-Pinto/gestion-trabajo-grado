package co.unicauca.workflow.access;

import co.unicauca.workflow.domain.entities.FormatoA;
import java.util.List;

public interface IFormatoARepository {
    boolean save(FormatoA formato);

    FormatoA findById(int id);

    List<FormatoA> listAllFormatoA();

    boolean update(FormatoA formato);

    boolean delete(int id);
}
