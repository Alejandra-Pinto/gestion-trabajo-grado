package co.unicauca.workflow.access;

import co.unicauca.workflow.domain.entities.DegreeWork;
import java.util.List;

public interface IDegreeWorkRepository {
    boolean save(DegreeWork formato);

    DegreeWork findById(int id);

    List<DegreeWork> listAllDegreeWork();

    boolean update(DegreeWork formato);

    boolean delete(int id);
}
