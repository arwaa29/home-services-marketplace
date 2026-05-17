package com.homeservices.userservice.service;

import com.homeservices.userservice.config.JwtUtil;
import com.homeservices.userservice.entity.User;
import com.homeservices.userservice.entity.Role;
import com.homeservices.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public User registerCustomer(String username, String password, Double initialBalance)
    {
        if (userRepository.existsByUsername(username))
        {
            throw new RuntimeException("Username already exists");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setBalance(initialBalance);
        user.setRole(Role.CUSTOMER);
        return userRepository.save(user);
    }

    public User registerProvider(String username, String password, String professionType) {
        if (userRepository.existsByUsername(username))
        {
            throw new RuntimeException("Username already exists");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setBalance(0.0);
        user.setProfessionType(professionType);
        user.setRole(Role.PROVIDER);
        return userRepository.save(user);
    }

    public String login(String username, String password)
    {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword()))
        {
            throw new RuntimeException("Invalid password");
        }

        return jwtUtil.generateToken(
                user.getUsername(),
                user.getId(),
                user.getRole().name()
        );
    }

    public User addFunds(Long userId, Double amount)
    {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        user.setBalance(user.getBalance() + amount);
        return userRepository.save(user);
    }

    public Double getBalance(Long userId)
    {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        return user.getBalance();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User deductBalance(Long userId, Double amount)
    {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance");
        }
        user.setBalance(user.getBalance() - amount);
        return userRepository.save(user);
    }
}