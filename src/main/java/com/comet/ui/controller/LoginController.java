package com.comet.ui.controller;

import com.comet.App;
import com.comet.model.User;
import com.comet.model.UserSession;
import com.comet.repository.UserRepository;
import com.comet.service.AuthService;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    private final AuthService authService = new AuthService(
        new UserRepository()
    );

    @FXML
    protected void onLoginButtonClick(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter your credentials.");
            return;
        }

        Optional<User> loggedInUser = authService.login(username, password);

        if (loggedInUser.isPresent()) {
            UserSession.getInstance().setCurrentUser(loggedInUser.get());

            App.connectToServer();

            try {
                App.setRoot("main-view");
            } catch (Exception e) {
                e.printStackTrace();
                showError("Failed to load the chat screen.");
            }
        } else {
            showError("Invalid username or password.");
        }
    }

    @FXML
    protected void onSignupRedirectClick() {
        try {
            com.comet.App.setRoot("signup-view");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load the signup screen.");
        }
    }

    private void showError(String message) {
        statusLabel.setTextFill(Color.RED);
        statusLabel.setText(message);
    }
}
