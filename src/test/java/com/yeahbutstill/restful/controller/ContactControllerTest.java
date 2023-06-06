package com.yeahbutstill.restful.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeahbutstill.restful.entity.Contact;
import com.yeahbutstill.restful.entity.User;
import com.yeahbutstill.restful.model.ContactResponse;
import com.yeahbutstill.restful.model.CreateContactRequest;
import com.yeahbutstill.restful.model.UpdateContactRequest;
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

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ContactControllerTest {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AddressRepository addressRepository;

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

    @BeforeEach
    void setup() {
        addressRepository.deleteAll();
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
                post("/api/v1/contacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "yeahbutstil-30day")
        ).andExpectAll(
                status().isBadRequest(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE),
                jsonPath("$.errors").exists()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.errors());
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
                post("/api/v1/contacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "yeahbutstil-30day")
        ).andExpectAll(
                status().isCreated(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.errors());
            assertNotNull(response.data());
            assertNotNull(response.data().id());
            assertEquals("Dani", response.data().firstName());
            assertEquals("Setiawan", response.data().lastName());
            assertEquals("dani@yeahbutstill.com", response.data().email());
            assertEquals("+6281234567890", response.data().phone());

            assertTrue(contactRepository.existsById(response.data().id()));
        });
    }

    @SneakyThrows
    @Test
    void getContactNotFound() {
        mockMvc.perform(
                get("/api/v1/contacts/212wirosableng")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "yeahbutstil-30day")
        ).andExpectAll(
                status().isNotFound(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE),
                content().json("{\"errors\":[\"Contact not found\"]}")
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.errors());
            assertEquals("Contact not found", response.errors().get(0));
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
                get("/api/v1/contacts/" + contact.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "yeahbutstil-30day")
        ).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.errors());
            assertNotNull(response.data());
            assertEquals(contact.getId(), response.data().id());
            assertEquals(contact.getFirstName(), response.data().firstName());
            assertEquals(contact.getLastName(), response.data().lastName());
            assertEquals(contact.getEmail(), response.data().email());
            assertEquals(contact.getPhone(), response.data().phone());
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
                put("/api/v1/contacts/212wirosableng")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "yeahbutstil-30day")
        ).andExpectAll(
                status().isBadRequest(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.errors());
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
                put("/api/v1/contacts/" + contact.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "yeahbutstil-30day")
        ).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.errors());
            assertNotNull(response.data());
            assertEquals(request.getFirstName(), response.data().firstName());
            assertEquals(request.getLastName(), response.data().lastName());
            assertEquals(request.getEmail(), response.data().email());
            assertEquals(request.getPhone(), response.data().phone());

            assertTrue(contactRepository.existsById(response.data().id()));
        });
    }

    @SneakyThrows
    @Test
    void deleteContactNotFound() {
        mockMvc.perform(
                delete("/api/v1/contacts/212wirosableng")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "yeahbutstil-30day")
        ).andExpectAll(
                status().isNotFound(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.errors());
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
                delete("/api/v1/contacts/" + contact.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "yeahbutstil-30day")
        ).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.errors());
            assertEquals("OK", response.data());
            assertFalse(contactRepository.existsById(contact.getId()));
        });

    }

    @SneakyThrows
    @Test
    void searchNotFound() {
        mockMvc.perform(
                get("/api/v1/contacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "yeahbutstil-30day")
        ).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.errors());
            assertEquals(0, response.data().size());
            assertEquals(0, response.paging().totalPage());
            assertEquals(0, response.paging().currentPage());
            assertEquals(10, response.paging().size());
        });
    }

    @Test
    @SneakyThrows
    void searchSuccess() {
        int length = 10;
        String randomString = generateRandomString(length);

        User user = userRepository.findById("yeahbutstill").orElseThrow(() -> new RuntimeException("User not found"));

        for (int i = 0; i < 100; i++) {
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
                get("/api/v1/contacts")
                        .queryParam("name", "Maya")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "yeahbutstil-30day")
        ).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.errors());
            assertEquals(10, response.data().size());
            assertEquals(10, response.paging().totalPage());
            assertEquals(0, response.paging().currentPage());
            assertEquals(10, response.paging().size());
        });

        mockMvc.perform(
                get("/api/v1/contacts")
                        .queryParam("name", "Dani")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "yeahbutstil-30day")
        ).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.errors());
            assertEquals(10, response.data().size());
            assertEquals(10, response.paging().totalPage());
            assertEquals(0, response.paging().currentPage());
            assertEquals(10, response.paging().size());
        });

        mockMvc.perform(
                get("/api/v1/contacts")
                        .queryParam("email", "yeahbutstill.com")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "yeahbutstil-30day")
        ).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.errors());
            assertEquals(10, response.data().size());
            assertEquals(10, response.paging().totalPage());
            assertEquals(0, response.paging().currentPage());
            assertEquals(10, response.paging().size());
        });

        mockMvc.perform(
                get("/api/v1/contacts")
                        .queryParam("phone", "+6281234567")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "yeahbutstil-30day")
        ).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.errors());
            assertEquals(10, response.data().size());
            assertEquals(10, response.paging().totalPage());
            assertEquals(0, response.paging().currentPage());
            assertEquals(10, response.paging().size());
        });

        mockMvc.perform(
                get("/api/v1/contacts")
                        .queryParam("phone", "+6281234567")
                        .queryParam("page", "1000")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "yeahbutstil-30day")
        ).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.errors());
            assertEquals(0, response.data().size());
            assertEquals(10, response.paging().totalPage());
            assertEquals(1000, response.paging().currentPage());
            assertEquals(10, response.paging().size());
        });

    }

}