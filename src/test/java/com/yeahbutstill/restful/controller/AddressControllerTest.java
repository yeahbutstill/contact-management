package com.yeahbutstill.restful.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeahbutstill.restful.entity.Address;
import com.yeahbutstill.restful.entity.Contact;
import com.yeahbutstill.restful.entity.User;
import com.yeahbutstill.restful.model.AddressResponse;
import com.yeahbutstill.restful.model.CreateAddressRequest;
import com.yeahbutstill.restful.model.UpdateAddressRequest;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
                post("/api/v1/contacts/" + request.getContactId() + "/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "yeahbutstill30days")

        ).andExpectAll(
                status().isBadRequest(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andDo(result -> {
            WebResponse<String> response = objectMapper
                    .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.errors());
            assertNull(response.data());
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
                post("/api/v1/contacts/" + request.getContactId() + "/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "yeahbutstill30days")
        ).andExpectAll(
                status().isCreated(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper
                    .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNull(response.errors());
            assertNotNull(response.data());
            assertNotNull(request.getContactId(), response.data().id());
            assertEquals(request.getStreet(), response.data().street());
            assertEquals(request.getCity(), response.data().city());
            assertEquals(request.getProvince(), response.data().province());
            assertEquals(request.getPostalCode(), response.data().postalCode());
            assertEquals(request.getCountry(), response.data().country());

            assertTrue(addressRepository.existsById(response.data().id()));
        });
    }

    @Test
    @SneakyThrows
    void getAddressNotFound() {
        mockMvc.perform(
                get("/api/v1/contacts/yeahbutstill/addresses/test")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "yeahbutstill30days")
        ).andExpectAll(
                status().isNotFound(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andDo(result -> {
            WebResponse<String> response = objectMapper
                    .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.errors());
            assertNull(response.data());
        });
    }

    @Test
    @SneakyThrows
    void getAddressSuccess() {
        Contact contact = contactRepository.findById("yeahbutstill").orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found"));

        Address address = new Address();
        address.setId(UUID.randomUUID().toString());
        address.setContact(contact);
        address.setStreet("Jl. Raya");
        address.setCity("Bandung");
        address.setProvince("Jawa Barat");
        address.setPostalCode("40211");
        address.setCountry("Indonesia");
        addressRepository.save(address);

        mockMvc.perform(
                get("/api/v1/contacts/" + contact.getId() + "/addresses/" + address.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "yeahbutstill30days")
        ).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper
                    .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNull(response.errors());
            assertNotNull(response.data());
            assertNotNull(response.data().id());
            assertEquals(address.getId(), response.data().id());
            assertEquals(address.getStreet(), response.data().street());
            assertEquals(address.getCity(), response.data().city());
            assertEquals(address.getProvince(), response.data().province());
            assertEquals(address.getPostalCode(), response.data().postalCode());
            assertEquals(address.getCountry(), response.data().country());
        });
    }

    @Test
    @SneakyThrows
    void updateAddressBadRequest() {
        UpdateAddressRequest request = new UpdateAddressRequest();
        request.setAddressId(null);
        request.setAddressId(null);
        request.setCountry("     ");

        mockMvc.perform(
                put("/api/v1/contacts/" + request.getContactId() + "/addresses/" + request.getAddressId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateAddressRequest()))
                        .header("X-API-TOKEN", "yeahbutstill30days")
        ).andExpectAll(
                status().isBadRequest(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andDo(result -> {
            WebResponse<String> response = objectMapper
                    .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.errors());
            assertNull(response.data());
        });
    }

    @Test
    @SneakyThrows
    void updateAddressSuccess() {
        Contact contact = contactRepository.findById("yeahbutstill").orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found"));

        Address address = new Address();
        address.setId("yeahbutstill");
        address.setContact(contact);
        address.setStreet("Jl. Raya Lama");
        address.setCity("Bandung Lama");
        address.setProvince("Jawa Barat Lama");
        address.setPostalCode("40211 Lama");
        address.setCountry("Indonesia Lama");
        addressRepository.save(address);

        UpdateAddressRequest request = new UpdateAddressRequest();
        request.setStreet("Jl. Raya");
        request.setCity("Bandung");
        request.setProvince("Jawa Barat");
        request.setPostalCode("40211");
        request.setCountry("Indonesia");

        mockMvc.perform(
                put("/api/v1/contacts/" + contact.getId() + "/addresses/" + address.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "yeahbutstill30days")
        ).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper
                    .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNull(response.errors());
            assertNotNull(response.data());
            assertEquals(request.getStreet(), response.data().street());
            assertEquals(request.getCity(), response.data().city());
            assertEquals(request.getProvince(), response.data().province());
            assertEquals(request.getPostalCode(), response.data().postalCode());
            assertEquals(request.getCountry(), response.data().country());

            assertTrue(addressRepository.existsById(response.data().id()));
        });
    }

    @Test
    @SneakyThrows
    void deleteAddressNotFound() {
        mockMvc.perform(
                delete("/api/v1/contacts/yeahbutstill/addresses/test")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "yeahbutstill30days")
        ).andExpectAll(
                status().isNotFound(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andDo(result -> {
            WebResponse<String> response = objectMapper
                    .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.errors());
            assertNull(response.data());
        });
    }

    @Test
    @SneakyThrows
    void deleteAddressSuccess() {
        Contact contact = contactRepository.findById("yeahbutstill").orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found"));

        Address address = new Address();
        address.setId("yeahbutstill");
        address.setContact(contact);
        address.setStreet("Jl. Raya Lama");
        address.setCity("Bandung Lama");
        address.setProvince("Jawa Barat Lama");
        address.setPostalCode("40211 Lama");
        address.setCountry("Indonesia Lama");
        addressRepository.save(address);

        mockMvc.perform(
                delete("/api/v1/contacts/" + contact.getId() + "/addresses/" + address.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "yeahbutstill30days")
        ).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andDo(result -> {
            WebResponse<String> response = objectMapper
                    .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNull(response.errors());
            assertEquals("OK", response.data());

            assertFalse(addressRepository.existsById(address.getId()));
        });
    }

    @Test
    @SneakyThrows
    void listAddressNotFound() {
        mockMvc.perform(
                get("/api/v1/contacts/salah/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "yeahbutstill30days")
        ).andExpectAll(
                status().isNotFound(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andDo(result -> {
            WebResponse<String> response = objectMapper
                    .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.errors());
            assertNull(response.data());
        });
    }

    @Test
    @SneakyThrows
    void listAddressSuccess() {
        Contact contact = contactRepository.findById("yeahbutstill").orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found"));

        for (int i = 0; i < 10_000; i++) {
            Address address = new Address();
            address.setId(UUID.randomUUID().toString());
            address.setContact(contact);
            address.setStreet("Jl. Raya " + i);
            address.setCity("Bandung " + i);
            address.setProvince("Jawa Barat " + i);
            address.setPostalCode("40211 " + i);
            address.setCountry("Indonesia " + i);
            addressRepository.save(address);
        }

        mockMvc.perform(
                get("/api/v1/contacts/yeahbutstill/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "yeahbutstill30days")
        ).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andDo(result -> {
            WebResponse<List<AddressResponse>> response = objectMapper
                    .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNull(response.errors());
            assertNotNull(response.data());
            assertEquals(10_000, response.data().size());
        });
    }

}