package com.homeservices.userservice.dto;

import com.homeservices.userservice.entity.Role;
import lombok.Data;

@Data
public class UserResponseDTO
{
    private long id;
    private String username;
    private Role role;
    private Double balance;
    private String professionType;
}