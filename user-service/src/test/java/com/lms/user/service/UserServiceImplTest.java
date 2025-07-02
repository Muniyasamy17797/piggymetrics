package com.lms.user.service;

import com.lms.user.dto.CreateUserDTO;
import com.lms.user.dto.UserDTO;
import com.lms.user.exception.ResourceNotFoundException;
import com.lms.user.exception.UserAlreadyExistsException;
import com.lms.user.mapper.UserMapper;
import com.lms.user.model.Role;
import com.lms.user.model.User;
import com.lms.user.model.UserStatus;
import com.lms.user.repository.UserRepository;
import com.lms.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDTO userDTO;
    private CreateUserDTO createUserDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encoded_password");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setStatus(UserStatus.ACTIVE);

        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("testuser");
        userDTO.setEmail("test@example.com");
        userDTO.setFirstName("Test");
        userDTO.setLastName("User");
        userDTO.setStatus(UserStatus.ACTIVE);

        createUserDTO = new CreateUserDTO();
        createUserDTO.setUsername("testuser");
        createUserDTO.setEmail("test@example.com");
        createUserDTO.setPassword("password");
        createUserDTO.setFirstName("Test");
        createUserDTO.setLastName("User");
    }

    @Test
    void createUser_Success() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userMapper.toEntity(any(CreateUserDTO.class))).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDTO(any(User.class))).thenReturn(userDTO);

        UserDTO result = userService.createUser(createUserDTO);

        assertNotNull(result);
        assertEquals(userDTO.getUsername(), result.getUsername());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_UsernameExists() {
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> 
            userService.createUser(createUserDTO)
        );
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        UserDTO result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(userDTO.getId(), result.getId());
    }

    @Test
    void getUserById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            userService.getUserById(1L)
        );
    }

    @Test
    void getAllUsers_Success() {
        List<User> users = Arrays.asList(user);
        Page<User> userPage = new PageImpl<>(users);
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userMapper.toDTO(any(User.class))).thenReturn(userDTO);

        Page<UserDTO> result = userService.getAllUsers(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void updateUserStatus_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDTO(any(User.class))).thenReturn(userDTO);

        UserDTO result = userService.updateUserStatus(1L, UserStatus.INACTIVE);

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void searchUsers_Success() {
        List<User> users = Arrays.asList(user);
        Page<User> userPage = new PageImpl<>(users);
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.searchUsers(anyString(), any(Pageable.class))).thenReturn(userPage);
        when(userMapper.toDTO(any(User.class))).thenReturn(userDTO);

        Page<UserDTO> result = userService.searchUsers("test", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getUsersByDateRange_Success() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        List<User> users = Arrays.asList(user);

        when(userRepository.findUsersByDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(users);
        when(userMapper.toDTO(any(User.class))).thenReturn(userDTO);

        List<UserDTO> result = userService.getUsersByDateRange(startDate, endDate);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void addRole_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDTO(any(User.class))).thenReturn(userDTO);

        UserDTO result = userService.addRole(1L, Role.STUDENT);

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }
}