package com.yeahbutstill.restful.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeahbutstill.restful.entity.Contact;
import com.yeahbutstill.restful.entity.User;
import com.yeahbutstill.restful.model.RegisterUserRequest;
import com.yeahbutstill.restful.model.UpdateUserRequest;
import com.yeahbutstill.restful.model.UserResponse;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

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

        User user = new User();
        user.setUsername("yeahbutstill");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("Dani");
        user.setToken("yeahbutstill30days");
        user.setTokenExpiredAt(System.currentTimeMillis() + 1_000_000);
        userRepository.save(user);

        Contact contact = new Contact();
        contact.setId("yeahbutstill");
        contact.setUser(user);
        contact.setFirstName("Maya");
        contact.setLastName("Setiawan");
        contact.setEmail("maya@yeahbutstill.com");
        contact.setPhone("081234567890");
        contactRepository.save(contact);
    }

    @SneakyThrows
    @Test
    void registerSuccess() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("test");
        request.setPassword("rahasia");
        request.setName("Test");

        mockMvc.perform(
                post("/api/v1/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper
                    .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertEquals("OK", response.data());
        });
    }

    @SneakyThrows
    @Test
    void registerBadRequest() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("    ");
        request.setPassword("    ");
        request.setName("    ");

        mockMvc.perform(
                post("/api/v1/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper
                    .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.errors());
        });
    }

    @SneakyThrows
    @Test
    void registerDuplicate() {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("Test");
        userRepository.save(user);

        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("test");
        request.setPassword("rahasia");
        request.setName("Test");

        mockMvc.perform(
                post("/api/v1/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper
                    .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.errors());
        });
    }

    @SneakyThrows
    @Test
    void getUserUnauthorized() {
        mockMvc.perform(
                get("/api/v1/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "notfound")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse()
                    .getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.errors());
        });
    }

    @SneakyThrows
    @Test
    void getUserUnauthorizedTokenNotSend() {
        mockMvc.perform(
                get("/api/v1/users/current")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse()
                    .getContentAsString(), new TypeReference<>() {
            });
        });
    }

    @SneakyThrows
    @Test
    void getUserSuccess() {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("Test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 1_000_000L);
        userRepository.save(user);

        mockMvc.perform(
                get("/api/v1/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse()
                    .getContentAsString(), new TypeReference<>() {
            });

            assertNull(response.errors());
            assertNotNull(response.data());
            assertEquals(user.getUsername(), response.data().username());
            assertEquals(user.getName(), response.data().name());
        });
    }

    @SneakyThrows
    @Test
    void getUserTokenExpired() {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("Test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() - 1_000_000L);
        userRepository.save(user);

        mockMvc.perform(
                get("/api/v1/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse()
                    .getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.errors());
            assertEquals("X-API-TOKEN is expired", response.errors().get(0));
            assertTrue(response.errors().get(0).contains("X-API-TOKEN is expired"));
        });
    }

    @SneakyThrows
    @Test
    void updateUserUnauthorized() {
        UpdateUserRequest request = new UpdateUserRequest();

        mockMvc.perform(
                patch("/api/v1/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse()
                    .getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.errors());
        });
    }

    @SneakyThrows
    @Test
    void updateUserSuccess() {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("Test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 1_000_000L);
        userRepository.save(user);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("Dani");
        request.setPassword("rahasiabanget");

        mockMvc.perform(
                patch("/api/v1/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse()
                    .getContentAsString(), new TypeReference<>() {
            });

            assertNull(response.errors());
            assertNotNull(response.data());
            assertEquals(user.getUsername(), response.data().username());
            assertEquals(request.getName(), response.data().name());

            User userDB = userRepository.findById(user.getUsername()).orElse(null);
            assertNotNull(userDB);
            assertTrue(BCrypt.checkpw(request.getPassword(), userDB.getPassword()));
        });
    }

}