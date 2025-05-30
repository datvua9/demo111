package com.example.demo.user.controller;

import com.example.demo.user.UserProfileRepository;
import com.example.demo.user.UserRepository;
import com.example.demo.user.data.User;
import com.example.demo.user.data.UserDTO;
import com.example.demo.user.data.UserProfile;
import com.example.demo.user.data.UserProfileDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        UserProfile userProfile = userProfileRepository.findByUser(user);
        Map<String, Object> userData = new HashMap<>();
        userData.put("user_id", user.getUser_id());
        userData.put("username", user.getUsername());

        if (userProfile != null) {
            userData.put("avatar", userProfile.getAvatar());
        }

        return ResponseEntity.ok(userData);
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getUsersByIds(@RequestParam List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);
        List<Map<String, Object>> usersData = users.stream().map(user -> {
            UserProfile userProfile = userProfileRepository.findByUser(user);
            Map<String, Object> userData = new HashMap<>();
            userData.put("user_id", user.getUser_id());
            userData.put("username", user.getUsername());
            if (userProfile != null) {
                userData.put("avatar", userProfile.getAvatar());
            }
            return userData;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(usersData);
    }

    @PostMapping("/{userId}/profile")
    public ResponseEntity<UserProfileDTO> updateUserProfile(
            @PathVariable Long userId,
            @RequestParam(value = "avatar", required = false) MultipartFile avatarFile,
            @RequestParam(value = "bio", required = false) String bio,
            @RequestParam(value = "birthdate", required = false) String birthdate,
            @RequestParam(value = "hobbies", required = false) String hobbies,
            @RequestParam(value = "gender", required = false) String gender) throws IOException {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        UserProfile userProfile = userProfileRepository.findByUser(user);
        if (userProfile == null) {
            userProfile = new UserProfile();
            userProfile.setUser(user);
        }

        if (avatarFile != null && !avatarFile.isEmpty()) {
            String uploadDir = "src/main/resources/static/uploads/";
            File uploadDirFile = new File(uploadDir);
            if (!uploadDirFile.exists()) {
                uploadDirFile.mkdirs();
            }

            String fileName = System.currentTimeMillis() + "_" + avatarFile.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + fileName);
            Files.write(filePath, avatarFile.getBytes());
            userProfile.setAvatar("/uploads/" + fileName);
        }

        if (bio != null) userProfile.setBio(bio);
        if (birthdate != null) userProfile.setBirthdate(java.sql.Date.valueOf(LocalDate.parse(birthdate)));
        if (hobbies != null) userProfile.setHobbies(hobbies);
        if (gender != null) userProfile.setGender(gender);

        userProfileRepository.save(userProfile);

        UserProfileDTO userProfileDTO = new UserProfileDTO();
        userProfileDTO.setProfileId(userProfile.getProfileId());
        userProfileDTO.setAvatar(userProfile.getAvatar());
        userProfileDTO.setBio(userProfile.getBio());
        userProfileDTO.setBirthdate(userProfile.getBirthdate());
        userProfileDTO.setHobbies(userProfile.getHobbies());
        userProfileDTO.setGender(userProfile.getGender());

        return ResponseEntity.ok(userProfileDTO);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long userId, @RequestBody UserDTO userDTO) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        if (userDTO.getUsername() != null) user.setUsername(userDTO.getUsername());
        if (userDTO.getEmail() != null) user.setEmail(userDTO.getEmail());
        if (userDTO.getPassword() != null && userDTO.getConfirmPassword() != null &&
                userDTO.getPassword().equals(userDTO.getConfirmPassword())) {
            user.setPassword(userDTO.getPassword());
        }

        userRepository.save(user);

        // Trả về DTO
        UserDTO updatedUserDTO = new UserDTO();
        updatedUserDTO.convertToEntity(user);
        return ResponseEntity.ok(updatedUserDTO);
    }
}