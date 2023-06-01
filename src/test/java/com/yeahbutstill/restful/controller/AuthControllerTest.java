package com.yeahbutstill.restful.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeahbutstill.restful.entity.User;
import com.yeahbutstill.restful.model.LoginUserRequest;
import com.yeahbutstill.restful.model.TokenResponse;
import com.yeahbutstill.restful.model.WebResponse;
import com.yeahbutstill.restful.repository.AddressRepository;
import com.yeahbutstill.restful.repository.ContactRepository;
import com.yeahbutstill.restful.repository.UserRepository;
import com.yeahbutstill.restful.security.BCrypt;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private AddressRepository addressRepository;

    @BeforeEach
    void setup() {
        addressRepository.deleteAll();
        contactRepository.deleteAll();
        userRepository.deleteAll();
    }

    @SneakyThrows
    @Test
    void loginFailedUserNotFound() {
        LoginUserRequest request = new LoginUserRequest();
        request.setUsername("test");
        request.setPassword("rahasia");

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper
                    .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNotNull(response.getErrors());
        });
    }

    @SneakyThrows
    @Test
    void loginFailedWrongPassword() {
        User user = new User();
        user.setName("Test");
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        userRepository.save(user);

        LoginUserRequest request = new LoginUserRequest();
        request.setUsername("test");
        request.setPassword("wrong");

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper
                    .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNotNull(response.getErrors());
        });
    }

    @SneakyThrows
    @Test
    void loginSuccess() {
        User user = new User();
        user.setName("Test");
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        userRepository.save(user);

        LoginUserRequest request = new LoginUserRequest();
        request.setUsername("test");
        request.setPassword("rahasia");

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<TokenResponse> response = objectMapper
                    .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNotNull(response.getData().getToken());
            assertNotNull(response.getData().getExpiredAt());
            assertNull(response.getErrors());

            User userDb = userRepository.findById("test").orElse(null);
            assertNotNull(userDb);
            assertEquals(userDb.getToken(), response.getData().getToken());
            assertEquals(userDb.getTokenExpiredAt(), response.getData().getExpiredAt());
        });
    }

    @SneakyThrows
    @Test
    void logoutFailed() {
        mockMvc.perform(
                delete("/api/auth/logout")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper
                    .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNotNull(response.getErrors());
        });
    }

    @SneakyThrows
    @Test
    void logoutSuccess() {
        User user = new User();
        user.setName("Dani");
        user.setUsername("yeahbutstill");
        user.setPassword(BCrypt.hashpw("rahasiabanget", BCrypt.gensalt()));
        user.setToken("token-yeahbutstill-30day");
        user.setTokenExpiredAt(System.currentTimeMillis() + 1_000_000L);
        userRepository.save(user);

        mockMvc.perform(
                delete("/api/auth/logout")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", user.getToken())
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper
                    .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNotNull(response.getData());
            assertNull(response.getErrors());

            User userDb = userRepository.findById("yeahbutstill").orElse(null);
            assertNotNull(userDb);
            assertNull(userDb.getToken());
            assertNull(userDb.getTokenExpiredAt());
        });
    }

}