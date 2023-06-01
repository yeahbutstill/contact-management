package com.yeahbutstill.restful.service.impl;

import com.yeahbutstill.restful.entity.Address;
import com.yeahbutstill.restful.entity.Contact;
import com.yeahbutstill.restful.entity.User;
import com.yeahbutstill.restful.model.AddressResponse;
import com.yeahbutstill.restful.model.CreateAddressRequest;
import com.yeahbutstill.restful.repository.AddressRepository;
import com.yeahbutstill.restful.repository.ContactRepository;
import com.yeahbutstill.restful.service.AddressService;
import com.yeahbutstill.restful.service.ValidationService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class AddressServiceImpl implements AddressService {

    private final ContactRepository contactRepository;
    private final AddressRepository addressRepository;
    private final ValidationService validationService;

    public AddressServiceImpl(ContactRepository contactRepository, AddressRepository addressRepository, ValidationService validationService) {
        this.contactRepository = contactRepository;
        this.addressRepository = addressRepository;
        this.validationService = validationService;
    }


    /**
     * @param user
     * @param request
     * @return
     */
    @Override
    @Transactional
    public AddressResponse create(User user, CreateAddressRequest request) {
        validationService.validate(request);

        Contact contact = contactRepository.findFirstByUserAndId(user, request.getContactId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        Address address = new Address();
        address.setId(UUID.randomUUID().toString());
        address.setContact(contact);
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setProvince(request.getProvince());
        address.setCountry(request.getCountry());
        address.setPostalCode(request.getPostalCode());
        addressRepository.save(address);

        return toAddressResponse(address);
    }

    /**
     * @param user 
     * @param contactId
     * @param addressId
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public AddressResponse get(User user, String contactId, String addressId) {
        Contact contact = contactRepository.findFirstByUserAndId(user, contactId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        Address address = addressRepository.findFirstByContactAndId(contact, addressId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));


        return toAddressResponse(address);
    }

    private AddressResponse toAddressResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .street(address.getStreet())
                .city(address.getCity())
                .province(address.getProvince())
                .country(address.getCountry())
                .postalCode(address.getPostalCode())
                .build();
    }
}
