package ai.pepperorg.happynews.service;

import ai.pepperorg.happynews.model.User;
import ai.pepperorg.happynews.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registers a new user in the system.
     *
     * @param username the username of the user
     * @param password the raw password of the user
     * @param role     the role of the user (e.g., ROLE_USER, ROLE_ADMIN)
     * @return the saved User entity
     */
    public User registerUser(String username, String password, String role) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("User with username " + username + " already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);

        return userRepository.save(user);
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username the username of the user
     * @return the User entity
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Updates the profile of an existing user.
     *
     * @param username    the username of the user
     * @param updatedUser the User object containing updated fields
     */
    public void updateUser(String username, User updatedUser) {
        User existingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        if (updatedUser.getRole() != null && !updatedUser.getRole().isEmpty()) {
            existingUser.setRole(updatedUser.getRole());
        }

        userRepository.save(existingUser);
    }
}
