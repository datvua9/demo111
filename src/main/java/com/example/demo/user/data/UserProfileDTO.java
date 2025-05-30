package com.example.demo.user.data;

import lombok.Data;

import java.util.Date;

@Data
public class UserProfileDTO {
    public UserProfileDTO() {}
    private Long profileId;
    private Date birthdate;
    private String hobbies;
    private String gender;
    private String bio;
    private String avatar;

}
