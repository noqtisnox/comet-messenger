package com.comet;

import com.comet.config.DatabaseManager;
import com.comet.network.client.CometClient;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;

public class App extends Application {

    private static Scene scene;
    public static CometClient cometClient;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("login-view"), 800, 600);
        stage.setTitle("Comet Messenger");
        stage.setScene(scene);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void connectToServer() {
        try {
            cometClient = new CometClient(new URI("ws://localhost:8887"));
            cometClient.connect();
        } catch (Exception e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
        }
    }

    @Override
    public void stop() throws SQLException {
        System.out.println("Shutting down Comet Messenger...");
        if (cometClient != null && cometClient.isOpen()) {
            cometClient.close();
        }
        DatabaseManager.closeConnection();
    }

    public static void main(String[] args) {
        launch();
    }
}