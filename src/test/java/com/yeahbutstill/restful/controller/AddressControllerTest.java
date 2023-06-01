package com.yeahbutstill.restful.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeahbutstill.restful.entity.Contact;
import com.yeahbutstill.restful.entity.User;
import com.yeahbutstill.restful.model.AddressResponse;
import com.yeahbutstill.restful.model.CreateAddressRequest;
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

import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AddressControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
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
    void createAddressBadRequest() {
        CreateAddressRequest request = new CreateAddressRequest();
        request.setCountry("     ");

        mockMvc.perform(
                post("/api/contacts/" + request.getContactId()+ "/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "yeahbutstill30days")

        ).andExpectAll(
                status().isBadRequest(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andDo(result -> {
            WebResponse<String> response = objectMapper
                    .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNotNull(response.getErrors());
            assertNull(response.getData());
        });
    }

    @Test
    @SneakyThrows
    void createAddressSuccess() {
        CreateAddressRequest request = new CreateAddressRequest();
        request.setContactId("yeahbutstill");
        request.setStreet("Jl. Raya");
        request.setCity("Bandung");
        request.setProvince("Jawa Barat");
        request.setPostalCode("40211");
        request.setCountry("Indonesia");

        mockMvc.perform(
                post("/api/contacts/" + request.getContactId() + "/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "yeahbutstill30days")
        ).andExpectAll(
                status().isCreated(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper
                    .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNull(response.getErrors());
            assertNotNull(response.getData());
            assertNotNull(request.getContactId(), response.getData().getId());
            assertEquals(request.getStreet(), response.getData().getStreet());
            assertEquals(request.getCity(), response.getData().getCity());
            assertEquals(request.getProvince(), response.getData().getProvince());
            assertEquals(request.getPostalCode(), response.getData().getPostalCode());
            assertEquals(request.getCountry(), response.getData().getCountry());

            assertTrue(addressRepository.existsById(response.getData().getId()));
        });
    }

}