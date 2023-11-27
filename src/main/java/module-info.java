module com.hitori {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.sat4j.core;

    opens com.hitori to javafx.fxml;
    exports com.hitori;
}
