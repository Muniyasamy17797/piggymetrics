package com.lms.user.service;

import com.lms.user.dto.CreateUserDTO;
import com.lms.user.dto.UserDTO;
import com.lms.user.model.Role;
import com.lms.user.model.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface UserService {
    UserDTO createUser(CreateUserDTO createUserDTO);
    UserDTO updateUser(Long id, UserDTO userDTO);
    UserDTO getUserById(Long id);
    UserDTO getUserByUsername(String username);
    Page<UserDTO> getAllUsers(Pageable pageable);
    void deleteUser(Long id);
    UserDTO updateUserStatus(Long id, UserStatus status);
    UserDTO addRole(Long id, Role role);
    UserDTO removeRole(Long id, Role role);
    Page<UserDTO> searchUsers(String searchTerm, Pageable pageable);
    List<UserDTO> getUsersByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    List<UserDTO> getUsersByRole(Role role);
    Page<UserDTO> getUsersByStatus(UserStatus status, Pageable pageable);
}