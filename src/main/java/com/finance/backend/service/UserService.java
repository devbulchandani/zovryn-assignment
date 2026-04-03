package com.finance.backend.service;

import com.finance.backend.dto.UserRequest;
import com.finance.backend.dto.UserResponse;
import com.finance.backend.entity.User;
import com.finance.backend.exception.DuplicateResourceException;
import com.finance.backend.exception.ResourceNotFoundException;
import com.finance.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional
    public UserResponse createUser(UserRequest request) {
        log.info("Creating user with username: {}", request.getUsername());
        
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists: " + request.getUsername());
        }
        
        User user = User.builder()
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .isActive(true)
                .build();
        
        User savedUser = userRepository.save(user);
        log.info("User created successfully with ID: {}", savedUser.getId());
        
        return mapToResponse(savedUser);
    }
    
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }
    
    @Transactional
    public UserResponse toggleUserStatus(Long userId) {
        log.info("Toggling status for user ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        user.setIsActive(!user.getIsActive());
        User updatedUser = userRepository.save(user);
        
        log.info("User status toggled. New status: {}", updatedUser.getIsActive());
        return mapToResponse(updatedUser);
    }
    
    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .build();
    }
}
