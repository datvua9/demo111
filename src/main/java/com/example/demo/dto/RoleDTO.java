package com.example.demo.dto;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import lombok.Data;

@Data
public class RoleDTO {
    private Long roleId;
    private String roleName;

    public void convertToEntity(Role role) {
        this.roleId = role.getRole_id();
        this.roleName = role.getRoleName();
    }
}
