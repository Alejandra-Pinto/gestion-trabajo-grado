module co.unicauca.workflow {
    requires javafx.controls;
    requires javafx.fxml;
    
    requires java.sql;
    requires java.base;

    opens co.unicauca.workflow to javafx.fxml;
    exports co.unicauca.workflow;
    exports co.unicauca.workflow.access;
    exports co.unicauca.workflow.domain.entities;
    exports co.unicauca.workflow.service;    
}
