module com.comet {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires transitive java.sql;
    requires transitive javafx.graphics;
    requires com.zaxxer.hikari;
    requires jbcrypt;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires org.java_websocket;

    opens com.comet.ui.controller to javafx.fxml;

    exports com.comet.ui.controller;
    exports com.comet.network.client;
    exports com.comet.network.server;
    exports com.comet;
    exports com.comet.config;
}
