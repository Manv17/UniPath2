module org.example.unipath2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires java.desktop;
    requires java.net.http;
    requires jdk.crypto.ec;

    opens org.example.unipath2.ui.controllers to javafx.fxml;

    opens org.example.unipath2.domain.career to javafx.fxml, com.fasterxml.jackson.databind;

    opens org.example.unipath2.application.statistics to javafx.fxml;
    opens org.example.unipath2.application.statistics.numeric to javafx.fxml;

    exports org.example.unipath2.domain.enums;
    exports org.example.unipath2.application.exception;
    exports org.example.unipath2.application.statistics;
    exports org.example.unipath2.application.statistics.numeric;
    exports org.example.unipath2.ui.controllers;
    exports org.example.unipath2.domain.career;
    exports org.example.unipath2.infrastructure;
    exports org.example.unipath2.application.statistics.minmax;
    opens org.example.unipath2.application.statistics.minmax to javafx.fxml;
    exports org.example.unipath2.application.statistics.courseList;
    opens org.example.unipath2.application.statistics.courseList to javafx.fxml;
    exports org.example.unipath2.ui.controllers.pages;
    opens org.example.unipath2.ui.controllers.pages to javafx.fxml;
    exports org.example.unipath2.ui.controllers.windows;
    opens org.example.unipath2.ui.controllers.windows to javafx.fxml;
    exports org.example.unipath2.ui.controllers.windows.adds;
    opens org.example.unipath2.ui.controllers.windows.adds to javafx.fxml;
    exports org.example.unipath2.ui.controllers.windows.edits;
    opens org.example.unipath2.ui.controllers.windows.edits to javafx.fxml;
    exports org.example.unipath2.domain.course;
    opens org.example.unipath2.domain.course to com.fasterxml.jackson.databind, javafx.fxml;
    exports org.example.unipath2.ui;
    opens org.example.unipath2.ui to javafx.fxml;
    exports org.example.unipath2.ui.controllers.windows.simulations;
    opens org.example.unipath2.ui.controllers.windows.simulations to javafx.fxml;
}