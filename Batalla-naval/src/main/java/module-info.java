module edu.univalle.battleship {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;



    opens edu.univalle.battleship.controller to javafx.fxml;
    opens edu.univalle.battleship to javafx.fxml;
    exports edu.univalle.battleship;
}