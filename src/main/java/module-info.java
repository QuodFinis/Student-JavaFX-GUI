module com.example.studentdatabase {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.studentdatabase to javafx.fxml;
    exports com.example.studentdatabase;
}