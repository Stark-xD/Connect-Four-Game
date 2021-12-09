module com.internshala.connectfour {
        requires javafx.controls;
        requires javafx.fxml;


        opens com.project.connectfour to javafx.fxml;
        exports com.project.connectfour;
}