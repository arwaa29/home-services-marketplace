package com.homeservices.userservice.controller;
import com.homeservices.userservice.config.JwtUtil;
import com.homeservices.userservice.entity.User;
import com.homeservices.userservice.dto.UserResponseDTO;
import com.homeservices.userservice.dto.UserRequestDTO;
import com.homeservices.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register/customer")
    public ResponseEntity<UserResponseDTO> registerCustomer(@RequestBody UserRequestDTO request)
    {
        User user = userService.registerCustomer(
                request.getUsername(),
                request.getPassword(),
                request.getBalance()
        );
        return ResponseEntity.ok(mapToDTO(user));
    }

    @PostMapping("/register/provider")
    public ResponseEntity<UserResponseDTO> registerProvider(@RequestBody UserRequestDTO request)
    {
        User user = userService.registerProvider(
                request.getUsername(),
                request.getPassword(),
                request.getProfessionType()
        );
        return ResponseEntity.ok(mapToDTO(user));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserRequestDTO request)
    {
        String token = userService.login(
                request.getUsername(),
                request.getPassword()
        );
        return ResponseEntity.ok(token);
    }

    @PutMapping("/add-funds")
    public ResponseEntity<UserResponseDTO> addFunds(@RequestHeader("Authorization") String authHeader,
                                                    @RequestParam Double amount)
    {
        Long userId = jwtUtil.extractUserId(authHeader.substring(7));
        User user = userService.addFunds(userId, amount);
        return ResponseEntity.ok(mapToDTO(user));
    }

    @GetMapping("/balance")
    public ResponseEntity<Double> getBalance(@RequestHeader("Authorization") String authHeader)
    {
        Long userId = jwtUtil.extractUserId(authHeader.substring(7));
        return ResponseEntity.ok(userService.getBalance(userId));
    }

//booking
    @GetMapping("/{userId}/balance-internal")
    public ResponseEntity<Double> getBalanceInternal(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getBalance(userId));
    }
//booking
    @PutMapping("/{userId}/deduct")
    public ResponseEntity<UserResponseDTO> deductBalance(
            @PathVariable Long userId,
            @RequestParam Double amount) {
        User user = userService.deductBalance(userId, amount);
        return ResponseEntity.ok(mapToDTO(user));
    }
//booking
    @PutMapping("/{userId}/add-funds-internal")
    public ResponseEntity<UserResponseDTO> addFundsInternal(
            @PathVariable Long userId,
            @RequestParam Double amount) {
        User user = userService.addFunds(userId, amount);
        return ResponseEntity.ok(mapToDTO(user));
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers()
    {
        return ResponseEntity.ok(userService.getAllUsers()
                        .stream()
                        .map(this::mapToDTO)
                        .toList()
        );
    }

    private UserResponseDTO mapToDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        dto.setBalance(user.getBalance());
        dto.setProfessionType(user.getProfessionType());
        return dto;
    }
}