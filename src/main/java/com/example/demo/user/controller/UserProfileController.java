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

@Controller
public class UserProfileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Value("${upload.dir:/uploads}")
    private String uploadDir;

    @GetMapping("/profile")
    public String showProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userRepository.findByUsername(username);
        if (user == null) {
            return "redirect:/login";
        }

        UserProfile userProfile = userProfileRepository.findByUser(user);

        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        UserProfileDTO userProfileDTO = userProfile != null ? modelMapper.map(userProfile, UserProfileDTO.class) : new UserProfileDTO();

        model.addAttribute("userDTO", userDTO);
        model.addAttribute("userProfileDTO", userProfileDTO);
        return "user_setting";
    }

    @PostMapping("/profile/save")
    @Transactional
    public String saveProfile(
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam(value = "newPassword", required = false) String newPassword,
            @RequestParam("gender") String gender,
            @RequestParam("birthdate") String birthdate,
            @RequestParam("hobbies") String hobbies,
            @RequestParam("bio") String bio,
            @RequestParam(value = "avatar", required = false) MultipartFile avatarFile) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        User user = userRepository.findByUsername(currentUsername);

        user.setUsername(username);
        user.setEmail(email);
        if (newPassword != null && !newPassword.isEmpty()) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        UserProfile userProfile = userProfileRepository.findByUser(user);
        String existingAvatar = null;
        if (userProfile == null) {
            System.out.println("Creating new UserProfile for user: " + user.getUsername());
            userProfile = new UserProfile();
            userProfile.setUser(user);
        } else {
            System.out.println("Updating existing UserProfile for user: " + user.getUsername());
            existingAvatar = userProfile.getAvatar();
        }
        userProfile.setGender(gender);
        userProfile.setBirthdate(java.sql.Date.valueOf(birthdate));
        userProfile.setHobbies(hobbies);
        userProfile.setBio(bio);

        if (avatarFile != null && !avatarFile.isEmpty()) {
            String fileName = saveFile(avatarFile);
            userProfile.setAvatar("/uploads/" + fileName);
        } else if (existingAvatar != null) {
            userProfile.setAvatar(existingAvatar);
        }

        userRepository.save(user);
        userProfileRepository.save(userProfile);

        return "redirect:/home";
    }

    private String saveFile(MultipartFile file) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.write(filePath, file.getBytes());

        System.out.println("Avatar saved at: " + filePath.toAbsolutePath());
        return fileName;
    }
}