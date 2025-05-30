package com.example.demo.user.data;

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
