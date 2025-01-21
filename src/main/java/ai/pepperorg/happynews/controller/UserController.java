package ai.pepperorg.happynews.controller;

import ai.pepperorg.happynews.model.User;
import ai.pepperorg.happynews.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Fetch the user's profile
    @GetMapping("/profile")
    public User getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.getUserByUsername(userDetails.getUsername());
    }

    // Update the user's profile
    @PutMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                @Valid @RequestBody User updatedUser) {
        userService.updateUser(userDetails.getUsername(), updatedUser);
        return "Profile updated successfully!";
    }
}
