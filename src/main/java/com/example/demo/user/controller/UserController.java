package com.example.demo.user.controller;

import com.example.demo.user.UserProfileRepository;
import com.example.demo.user.UserRepository;
import com.example.demo.user.data.User;
import com.example.demo.user.data.UserDTO;
import com.example.demo.user.data.UserProfile;
import com.example.demo.user.data.UserProfileDTO;
import org.modelmapper.ModelMapper; // Thêm ModelMapper nếu bạn muốn dùng để map DTO
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; // Để inject giá trị từ application.properties
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder; // Sử dụng PasswordEncoder thay vì BCryptPasswordEncoder trực tiếp
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File; // Nên tránh dùng java.io.File trực tiếp cho path, dùng java.nio.file.Path
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final ModelMapper modelMapper; // Sử dụng ModelMapper để chuyển đổi DTO <-> Entity
    private final PasswordEncoder passwordEncoder; // Sử dụng interface PasswordEncoder

    @Value("${app.upload.dir:src/main/resources/static/uploads/avatars}") // Đường dẫn upload có thể cấu hình
    private String uploadDir;


    @Autowired
    public UserController(UserRepository userRepository,
                          UserProfileRepository userProfileRepository,
                          ModelMapper modelMapper,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User user = userOptional.get();

        Optional<UserProfile> userProfileOptional = userProfileRepository.findByUser(user);
        Map<String, Object> userData = new HashMap<>();
        userData.put("user_id", user.getUserId());
        userData.put("username", user.getUsername());
        // userData.put("email", user.getEmail()); // Cân nhắc có nên trả email ở đây không

        userProfileOptional.ifPresent(profile -> userData.put("avatar", profile.getAvatar()));
        // Nếu không có avatar, trường "avatar" sẽ không có trong map, hoặc bạn có thể đặt giá trị mặc định:
        // userData.put("avatar", userProfileOptional.map(UserProfile::getAvatar).orElse(null));


        return ResponseEntity.ok(userData);
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getUsersByIds(@RequestParam List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);
        if (users.isEmpty()) {
            return ResponseEntity.ok(List.of()); // Trả về danh sách rỗng nếu không tìm thấy user nào
        }

        List<Map<String, Object>> usersData = users.stream().map(user -> {
            Optional<UserProfile> userProfileOptional = userProfileRepository.findByUser(user);
            Map<String, Object> userData = new HashMap<>();
            userData.put("user_id", user.getUserId());
            userData.put("username", user.getUsername());
            userProfileOptional.ifPresent(profile -> userData.put("avatar", profile.getAvatar()));
            return userData;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(usersData);
    }

    @PostMapping("/{userId}/profile")
    public ResponseEntity<?> updateUserProfile(
            @PathVariable Long userId,
            @RequestParam(value = "avatar", required = false) MultipartFile avatarFile,
            @RequestParam(value = "bio", required = false) String bio,
            @RequestParam(value = "birthdate", required = false) String birthdateString, // Đổi tên để parse
            @RequestParam(value = "hobbies", required = false) String hobbies,
            @RequestParam(value = "gender", required = false) String gender) {

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }
        User user = userOptional.get();

        // Lấy hoặc tạo mới UserProfile
        UserProfile userProfile = userProfileRepository.findByUser(user)
                .orElseGet(() -> {
                    UserProfile newProfile = new UserProfile();
                    newProfile.setUser(user);
                    return newProfile;
                });

        // Xử lý upload avatar
        if (avatarFile != null && !avatarFile.isEmpty()) {
            try {
                // Nên có logic xóa avatar cũ nếu có
                Path uploadPathDir = Paths.get(uploadDir);
                if (!Files.exists(uploadPathDir)) {
                    Files.createDirectories(uploadPathDir);
                }

                String originalFilename = avatarFile.getOriginalFilename();
                if (originalFilename == null || originalFilename.contains("..")) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Invalid avatar file name."));
                }
                String fileName = System.currentTimeMillis() + "_" + Paths.get(originalFilename).getFileName().toString();
                Path filePath = uploadPathDir.resolve(fileName);
                Files.write(filePath, avatarFile.getBytes());
                userProfile.setAvatar("/uploads/avatars/" + fileName); // Lưu đường dẫn tương đối, khớp với cấu hình static resource
            } catch (IOException e) {
                System.err.println("Error saving avatar file: " + e.getMessage());
                // Trả về lỗi cho client nếu không lưu được file
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Could not save avatar file."));
            }
        }

        // Cập nhật các trường thông tin khác
        if (bio != null) userProfile.setBio(bio);
        if (birthdateString != null && !birthdateString.isEmpty()) {
            try {
                userProfile.setBirthdate(java.sql.Date.valueOf(LocalDate.parse(birthdateString))); // Parse YYYY-MM-DD
            } catch (DateTimeParseException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid birthdate format. Please use YYYY-MM-DD."));
            }
        }
        if (hobbies != null) userProfile.setHobbies(hobbies);
        if (gender != null) userProfile.setGender(gender);

        UserProfile savedProfile = userProfileRepository.save(userProfile);

        // Chuyển đổi sang DTO để trả về
        UserProfileDTO userProfileDTO = modelMapper.map(savedProfile, UserProfileDTO.class);

        return ResponseEntity.ok(userProfileDTO);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody UserDTO userDTO) { // Thường PUT dùng @RequestBody
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }
        User user = userOptional.get();

        // Cập nhật thông tin User
        if (userDTO.getUsername() != null && !userDTO.getUsername().isEmpty()) {
            // Kiểm tra username mới có bị trùng không (trừ chính user hiện tại)
            Optional<User> existingUserByUsername = userRepository.findByUsernameAndUserIdNot(userDTO.getUsername(), userId);
            if (existingUserByUsername.isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Username already taken."));
            }
            user.setUsername(userDTO.getUsername());
        }

        if (userDTO.getEmail() != null && !userDTO.getEmail().isEmpty()) {
            // Kiểm tra email mới có bị trùng không (trừ chính user hiện tại)
            Optional<User> existingUserByEmail = userRepository.findByEmailAndUserIdNot(userDTO.getEmail(), userId);
            if (existingUserByEmail.isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email already taken."));
            }
            user.setEmail(userDTO.getEmail());
        }

        // Cập nhật password nếu được cung cấp và hợp lệ
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            if (userDTO.getConfirmPassword() != null && userDTO.getPassword().equals(userDTO.getConfirmPassword())) {
                user.setPassword(passwordEncoder.encode(userDTO.getPassword())); // Mã hóa password mới
            } else if (userDTO.getConfirmPassword() == null || !userDTO.getPassword().equals(userDTO.getConfirmPassword())){
                return ResponseEntity.badRequest().body(Map.of("error", "Passwords do not match."));
            }
            // Nếu chỉ có password mà không có confirmPassword, hoặc ngược lại, cũng có thể là lỗi
        }


        User updatedUser = userRepository.save(user);

        // Trả về DTO của User đã cập nhật
        UserDTO updatedUserDTO = modelMapper.map(updatedUser, UserDTO.class);
        // Không nên trả về password trong DTO response
        updatedUserDTO.setPassword(null);
        updatedUserDTO.setConfirmPassword(null);

        return ResponseEntity.ok(updatedUserDTO);
    }
}