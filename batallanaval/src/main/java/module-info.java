module com.example.batallanaval {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens com.example.batallanaval to javafx.fxml;
    opens com.example.batallanaval.controller to javafx.fxml;
    exports com.example.batallanaval.controller to javafx.fxml;
    exports com.example.batallanaval;
}