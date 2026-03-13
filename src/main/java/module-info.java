module com.paulina.tutorials {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;


    opens com.paulina.tutorials to javafx.fxml;
    exports com.paulina.tutorials;
}