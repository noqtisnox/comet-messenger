package com.comet.service;

import com.comet.model.User;
import com.comet.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Attempts to register a new user.
     * @return Optional containing the new User if successful, or Optional.empty() if the username is taken.
     */
    public Optional<User> signup(String username, String rawPassword) {
        if (userRepository.findByUsername(username).isPresent()) {
            return Optional.empty();
        }

        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt(12));

        return userRepository.create(username, hashedPassword);
    }

    /**
     * Attempts to log a user in.
     * @return Optional containing the User if credentials match, or Optional.empty() if they fail.
     */
    public Optional<User> login(String username, String rawPassword) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            return Optional.empty();
        }

        User user = userOpt.get();

        if (BCrypt.checkpw(rawPassword, user.passwordHash())) {
            return Optional.of(user);
        } else {
            return Optional.empty();
        }
    }
}