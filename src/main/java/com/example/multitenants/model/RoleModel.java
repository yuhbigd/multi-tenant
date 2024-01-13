package com.example.multitenants.model;

import com.example.multitenants.util.Constants.USER_ROLE;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleModel {
    private USER_ROLE role;
}
