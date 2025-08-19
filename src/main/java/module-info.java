module co.unicauca.workflow {
    requires javafx.controls;
    requires javafx.fxml;
    
    requires java.sql;

    opens co.unicauca.workflow to javafx.fxml;
    exports co.unicauca.workflow;
}
