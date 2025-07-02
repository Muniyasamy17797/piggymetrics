package com.lms.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.user.dto.CreateUserDTO;
import com.lms.user.dto.UserDTO;
import com.lms.user.model.Role;
import com.lms.user.model.UserStatus;
import com.lms.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDTO userDTO;
    private CreateUserDTO createUserDTO;

    @BeforeEach
    void setUp() {
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
    void createUser_Success() throws Exception {
        when(userService.createUser(any(CreateUserDTO.class))).thenReturn(userDTO);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(userDTO.getUsername()));
    }

    @Test
    void getUserById_Success() throws Exception {
        when(userService.getUserById(1L)).thenReturn(userDTO);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDTO.getId()))
                .andExpect(jsonPath("$.username").value(userDTO.getUsername()));
    }

    @Test
    void getAllUsers_Success() throws Exception {
        Page<UserDTO> page = new PageImpl<>(Arrays.asList(userDTO));
        when(userService.getAllUsers(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value(userDTO.getUsername()));
    }

    @Test
    void updateUserStatus_Success() throws Exception {
        when(userService.updateUserStatus(eq(1L), any(UserStatus.class))).thenReturn(userDTO);

        mockMvc.perform(patch("/api/users/1/status")
                .param("status", "INACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(userDTO.getUsername()));
    }

    @Test
    void searchUsers_Success() throws Exception {
        Page<UserDTO> page = new PageImpl<>(Arrays.asList(userDTO));
        when(userService.searchUsers(eq("test"), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/users/search")
                .param("query", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value(userDTO.getUsername()));
    }

    @Test
    void addRole_Success() throws Exception {
        when(userService.addRole(eq(1L), any(Role.class))).thenReturn(userDTO);

        mockMvc.perform(post("/api/users/1/roles")
                .param("role", "STUDENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(userDTO.getUsername()));
    }

    @Test
    void getUsersByStatus_Success() throws Exception {
        Page<UserDTO> page = new PageImpl<>(Arrays.asList(userDTO));
        when(userService.getUsersByStatus(any(UserStatus.class), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/users/by-status")
                .param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value(userDTO.getUsername()));
    }
}