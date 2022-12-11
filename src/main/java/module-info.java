module com.example.contacttracing {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.rmi;
    requires java.desktop;
    opens com.example.contacttracing to javafx.fxml;
    exports com.example.contacttracing;
    exports com.example.contacttracing.Interfaces;
}