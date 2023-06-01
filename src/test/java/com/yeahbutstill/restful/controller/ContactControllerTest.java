package com.yeahbutstill.restful.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeahbutstill.restful.entity.Contact;
import com.yeahbutstill.restful.entity.User;
import com.yeahbutstill.restful.model.ContactResponse;
import com.yeahbutstill.restful.model.CreateContactRequest;
import com.yeahbutstill.restful.model.UpdateContactRequest;
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

import java.util.List;
import java.util.Random;
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

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static String generateRandomString(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }

        return sb.toString();
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

    @SneakyThrows
    @Test
    void updateContactBadRequest() {
        UpdateContactRequest request = new UpdateContactRequest();
        request.setFirstName(" ");
        request.setLastName("");
        request.setEmail("salah");
        request.setPhone("1");

        mockMvc.perform(
                put("/api/contacts/212wirosableng")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "yeahbutstil-30day")
        ).andExpectAll(
                status().isBadRequest(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNotNull(response.getErrors());
        });

    }

    @SneakyThrows
    @Test
    void updateContactSuccess() {
        User user = userRepository.findById("yeahbutstill").orElseThrow(() -> new RuntimeException("User not found"));

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setUser(user);
        contact.setFirstName("Dani");
        contact.setLastName("Setiawan");
        contact.setEmail("dani@yeahbutstill.com");
        contact.setPhone("+6281234567890");
        contactRepository.save(contact);

        UpdateContactRequest request = new UpdateContactRequest();
        request.setFirstName("DaniL");
        request.setLastName("SetiawanL");
        request.setEmail("daniL@yeahbutstill.com");
        request.setPhone("+6287234567890");

        mockMvc.perform(
                put("/api/contacts/" + contact.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "yeahbutstil-30day")
        ).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNull(response.getErrors());
            assertNotNull(response.getData());
            assertEquals(request.getFirstName(), response.getData().getFirstName());
            assertEquals(request.getLastName(), response.getData().getLastName());
            assertEquals(request.getEmail(), response.getData().getEmail());
            assertEquals(request.getPhone(), response.getData().getPhone());

            assertTrue(contactRepository.existsById(response.getData().getId()));
        });
    }

    @SneakyThrows
    @Test
    void deleteContactNotFound() {
        mockMvc.perform(
                delete("/api/contacts/212wirosableng")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "yeahbutstil-30day")
        ).andExpectAll(
                status().isNotFound(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNotNull(response.getErrors());
        });
    }

    @Test
    @SneakyThrows
    void deleteContactSuccess() {
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
                delete("/api/contacts/" + contact.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "yeahbutstil-30day")
        ).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNull(response.getErrors());
            assertEquals("OK", response.getData());
            assertFalse(contactRepository.existsById(contact.getId()));
        });

    }

    @SneakyThrows
    @Test
    void searchNotFound() {
        mockMvc.perform(
                get("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "yeahbutstil-30day")
        ).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNull(response.getErrors());
            assertEquals(0, response.getData().size());
            assertEquals(0, response.getPaging().getTotalPage());
            assertEquals(0, response.getPaging().getCurrentPage());
            assertEquals(10, response.getPaging().getSize());
        });
    }

    @Test
    @SneakyThrows
    void searchSuccess() {
        int length = 10;
        String randomString = generateRandomString(length);

        User user = userRepository.findById("yeahbutstill").orElseThrow(() -> new RuntimeException("User not found"));

        for (int i=0; i<100; i++) {
            Contact contact = new Contact();
            contact.setId(UUID.randomUUID().toString());
            contact.setUser(user);
            contact.setFirstName("Maya" + randomString);
            contact.setLastName("Dani" + randomString);
            contact.setEmail(randomString + "@yeahbutstill.com");
            contact.setPhone("+628123456777");
            contactRepository.save(contact);
        }

        mockMvc.perform(
                get("/api/contacts")
                        .queryParam("name", "Maya")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "yeahbutstil-30day")
        ).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNull(response.getErrors());
            assertEquals(10, response.getData().size());
            assertEquals(10, response.getPaging().getTotalPage());
            assertEquals(0, response.getPaging().getCurrentPage());
            assertEquals(10, response.getPaging().getSize());
        });

        mockMvc.perform(
                get("/api/contacts")
                        .queryParam("name", "Dani")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "yeahbutstil-30day")
        ).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNull(response.getErrors());
            assertEquals(10, response.getData().size());
            assertEquals(10, response.getPaging().getTotalPage());
            assertEquals(0, response.getPaging().getCurrentPage());
            assertEquals(10, response.getPaging().getSize());
        });

        mockMvc.perform(
                get("/api/contacts")
                        .queryParam("email", "yeahbutstill.com")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "yeahbutstil-30day")
        ).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNull(response.getErrors());
            assertEquals(10, response.getData().size());
            assertEquals(10, response.getPaging().getTotalPage());
            assertEquals(0, response.getPaging().getCurrentPage());
            assertEquals(10, response.getPaging().getSize());
        });

        mockMvc.perform(
                get("/api/contacts")
                        .queryParam("phone", "+6281234567")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "yeahbutstil-30day")
        ).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNull(response.getErrors());
            assertEquals(10, response.getData().size());
            assertEquals(10, response.getPaging().getTotalPage());
            assertEquals(0, response.getPaging().getCurrentPage());
            assertEquals(10, response.getPaging().getSize());
        });

        mockMvc.perform(
                get("/api/contacts")
                        .queryParam("phone", "+6281234567")
                        .queryParam("page", "1000")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "yeahbutstil-30day")
        ).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNull(response.getErrors());
            assertEquals(0, response.getData().size());
            assertEquals(10, response.getPaging().getTotalPage());
            assertEquals(1000, response.getPaging().getCurrentPage());
            assertEquals(10, response.getPaging().getSize());
        });

    }

}