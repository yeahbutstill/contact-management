package com.yeahbutstill.restful.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeahbutstill.restful.entity.Contact;
import com.yeahbutstill.restful.entity.User;
import com.yeahbutstill.restful.model.ContactResponse;
import com.yeahbutstill.restful.model.CreateContactRequest;
import com.yeahbutstill.restful.model.WebResponse;
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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        contactRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setUsername("yeahbutstill");
        user.setPassword(BCrypt.hashpw("rahasiabanget", BCrypt.gensalt()));
        user.setName("Dani Setiawan");
        user.setToken("yeahbutstil-30day");
        user.setTokenExpiredAt(System.currentTimeMillis() + 1_000_000L);
        userRepository.save(user);
    }

    @SneakyThrows
    @Test
    void createContactBadRequest() {
        CreateContactRequest request = new CreateContactRequest();
        request.setFirstName("Dani");
        request.setLastName("Setiawan");
        request.setEmail("salah");
        request.setPhone("081234567890");

        mockMvc.perform(
                post("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "yeahbutstil-30day")
        ).andExpectAll(
                status().isBadRequest(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE),
                jsonPath("$.errors").exists()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNotNull(response.getErrors());
        });
    }

    @SneakyThrows
    @Test
    void createContactSuccess() {
        CreateContactRequest request = new CreateContactRequest();
        request.setFirstName("Dani");
        request.setLastName("Setiawan");
        request.setEmail("dani@yeahbutstill.com");
        request.setPhone("+6281234567890");

        mockMvc.perform(
                post("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "yeahbutstil-30day")
        ).andExpectAll(
                status().isCreated(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNull(response.getErrors());
            assertNotNull(response.getData());
            assertNotNull(response.getData().getId());
            assertEquals("Dani", response.getData().getFirstName());
            assertEquals("Setiawan", response.getData().getLastName());
            assertEquals("dani@yeahbutstill.com", response.getData().getEmail());
            assertEquals("+6281234567890", response.getData().getPhone());

            assertTrue(contactRepository.existsById(response.getData().getId()));
        });
    }

    @SneakyThrows
    @Test
    void getContactNotFound() {
        mockMvc.perform(
                get("/api/contacts/212wirosableng")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "yeahbutstil-30day")
        ).andExpectAll(
                status().isNotFound(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE),
                content().json("{\"errors\":[\"Contact not found\"]}")
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNotNull(response.getErrors());
            assertEquals("Contact not found", response.getErrors().get(0));
        });
    }

    @SneakyThrows
    @Test
    void getContactSuccess() {
        User user = userRepository.findById("yeahbutstill").orElseThrow(() -> new RuntimeException("User not found"));

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setUser(user);
        contact.setFirstName("Dani");
        contact.setLastName("Setiawan");
        contact.setEmail("dani@yeahbutstill.com");
        contact.setPhone("+6281234567890");
        contactRepository.save(contact);

        mockMvc.perform(
                get("/api/contacts/" + contact.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "yeahbutstil-30day")
        ).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNull(response.getErrors());
            assertNotNull(response.getData());
            assertEquals(contact.getId(), response.getData().getId());
            assertEquals(contact.getFirstName(), response.getData().getFirstName());
            assertEquals(contact.getLastName(), response.getData().getLastName());
            assertEquals(contact.getEmail(), response.getData().getEmail());
            assertEquals(contact.getPhone(), response.getData().getPhone());
        });
    }

}