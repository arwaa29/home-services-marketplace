package com.homeservices.userservice.dto;
import lombok.Data;

@Data
public class UserRequestDTO
{
    private String username;
    private String password;
    private Double balance;
    private String professionType;
}
