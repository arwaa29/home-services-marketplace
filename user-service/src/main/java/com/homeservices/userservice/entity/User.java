package com.homeservices.userservice.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data //for getters and setters automatically
@Entity
@Table(name = "users")
public class User {

    @Id //marks the column as primary key
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column
    private Double balance;// ll customer

    @Column
    private String professionType; //ll provider

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

}
