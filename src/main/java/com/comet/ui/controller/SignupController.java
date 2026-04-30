package com.comet.ui.controller;

import com.comet.service.AuthService;
import com.comet.repository.UserRepository;
import com.comet.model.User;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.util.Optional;

public class SignupController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    private final AuthService authService = new AuthService(new UserRepository());

    @FXML
    protected void onSignupButtonClick(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Username and password cannot be empty.");
            return;
        }

        Optional<User> newUser = authService.signup(username, password);

        if (newUser.isPresent()) {
            statusLabel.setTextFill(Color.GREEN);
            statusLabel.setText("Account created! Redirecting to login...");

            javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1.5));
            delay.setOnFinished(e -> {
                try {
                    com.comet.App.setRoot("login-view");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Failed to load the login screen.");
                }
            });
            delay.play();

        } else {
            showError("Username is already taken.");
        }
    }

    @FXML
    protected void onLoginRedirectClick() {
        try {
            com.comet.App.setRoot("login-view");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        statusLabel.setTextFill(Color.RED);
        statusLabel.setText(message);
    }
}