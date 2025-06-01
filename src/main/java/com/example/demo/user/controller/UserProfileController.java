package com.example.demo.user.controller;

import com.example.demo.user.data.UserDTO;
import com.example.demo.user.data.UserProfileDTO;
import com.example.demo.user.data.User;
import com.example.demo.user.data.UserProfile;
import com.example.demo.user.UserRepository;
import com.example.demo.user.UserProfileRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional; // Import Optional

@Controller
public class UserProfileController {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${upload.dir:/uploads}") // Default to /uploads if property not found
    private String uploadDir;

    @Autowired
    public UserProfileController(UserRepository userRepository,
                                 UserProfileRepository userProfileRepository,
                                 ModelMapper modelMapper,
                                 BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/profile")
    public String showProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userRepository.findByUsername(username);
        if (user == null) {
            // It's good practice to explicitly invalidate session or logout if user not found
            // SecurityContextHolder.clearContext(); // Example
            return "redirect:/login?error=UserNotFound";
        }

        // Use Optional to handle UserProfile
        Optional<UserProfile> userProfileOptional = userProfileRepository.findByUser(user);

        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        // Map UserProfileDTO if present, otherwise create a new one
        UserProfileDTO userProfileDTO = userProfileOptional
                .map(profile -> modelMapper.map(profile, UserProfileDTO.class))
                .orElseGet(UserProfileDTO::new); // Use orElseGet for new object creation

        model.addAttribute("userDTO", userDTO);
        model.addAttribute("userProfileDTO", userProfileDTO);
        return "user_setting"; // Thymeleaf template name
    }

    @PostMapping("/profile/save")
    @Transactional
    public String saveProfile(
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam(value = "newPassword", required = false) String newPassword,
            @RequestParam("gender") String gender,
            @RequestParam("birthdate") String birthdate, // Consider using @DateTimeFormat or parsing carefully
            @RequestParam("hobbies") String hobbies,
            @RequestParam("bio") String bio,
            @RequestParam(value = "avatar", required = false) MultipartFile avatarFile) throws IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        User user = userRepository.findByUsername(currentUsername);

        if (user == null) {
            return "redirect:/login?error=UserNotFoundForUpdate";
        }

        // Update user details
        // Consider checking if the new username or email is already taken by another user
        user.setUsername(username);
        user.setEmail(email);
        if (newPassword != null && !newPassword.isEmpty()) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        // Retrieve or create UserProfile using Optional
        Optional<UserProfile> userProfileOptional = userProfileRepository.findByUser(user);
        UserProfile userProfile = userProfileOptional.orElseGet(() -> {
            System.out.println("Creating new UserProfile for user: " + user.getUsername());
            UserProfile newProfile = new UserProfile();
            newProfile.setUser(user); // Associate with the user
            return newProfile;
        });

        if (userProfileOptional.isPresent()) {
            System.out.println("Updating existing UserProfile for user: " + user.getUsername());
        }

        // Update profile details
        userProfile.setGender(gender);
        try {
            // Ensure date parsing is robust
            userProfile.setBirthdate(java.sql.Date.valueOf(birthdate));
        } catch (IllegalArgumentException e) {
            // Handle invalid date format, e.g., add error to model and return to form
            System.err.println("Invalid date format for birthdate: " + birthdate);
            // return "redirect:/profile?error=InvalidDateFormat"; // Example error handling
        }
        userProfile.setHobbies(hobbies);
        userProfile.setBio(bio);

        String existingAvatarPath = userProfile.getAvatar(); // Get current avatar path before potential update

        if (avatarFile != null && !avatarFile.isEmpty()) {
            // Potentially delete old avatar file if it exists and is different
            // if (existingAvatarPath != null && !existingAvatarPath.isEmpty()) {
            //     try {
            //         Path oldAvatarFilePath = Paths.get(uploadDir, Paths.get(existingAvatarPath).getFileName().toString());
            //         Files.deleteIfExists(oldAvatarFilePath);
            //         System.out.println("Deleted old avatar: " + oldAvatarFilePath);
            //     } catch (IOException e) {
            //         System.err.println("Could not delete old avatar: " + existingAvatarPath + " - " + e.getMessage());
            //     }
            // }
            String fileName = saveFile(avatarFile);
            userProfile.setAvatar("/uploads/" + fileName); // Store relative path
        }
        // No 'else if (existingAvatar != null)' needed here,
        // if avatarFile is empty, existingAvatarPath (if any) will remain on userProfile object

        userRepository.save(user);
        userProfileRepository.save(userProfile);

        // If username changed, the security context needs to be updated
        // This is a more advanced topic involving re-authenticating the user or updating Authentication object
        // For simplicity, redirecting to home. User might need to log in again if username is part of principal's identity.
        System.out.println("Profile saved successfully for user: " + user.getUsername());
        return "redirect:/home?profileUpdated=true";
    }

    private String saveFile(MultipartFile file) throws IOException {
        // Sanitize filename to prevent directory traversal issues
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.contains("..")) {
            throw new IOException("Invalid file name or path traversal attempt.");
        }
        String fileName = System.currentTimeMillis() + "_" + Paths.get(originalFilename).getFileName().toString();

        Path uploadPathDir = Paths.get(uploadDir);

        if (!Files.exists(uploadPathDir)) {
            Files.createDirectories(uploadPathDir);
            System.out.println("Created upload directory: " + uploadPathDir.toAbsolutePath());
        }

        Path filePath = uploadPathDir.resolve(fileName);
        Files.write(filePath, file.getBytes());

        System.out.println("Avatar saved at: " + filePath.toAbsolutePath());
        return fileName;
    }
}