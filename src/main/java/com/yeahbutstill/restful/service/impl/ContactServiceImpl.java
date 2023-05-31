package com.yeahbutstill.restful.service.impl;

import com.yeahbutstill.restful.entity.Contact;
import com.yeahbutstill.restful.entity.User;
import com.yeahbutstill.restful.model.ContactResponse;
import com.yeahbutstill.restful.model.CreateContactRequest;
import com.yeahbutstill.restful.model.UpdateContactRequest;
import com.yeahbutstill.restful.repository.ContactRepository;
import com.yeahbutstill.restful.service.ContactService;
import com.yeahbutstill.restful.service.ValidationService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;
    private final ValidationService validationService;

    public ContactServiceImpl(ContactRepository contactRepository, ValidationService validationService) {
        this.contactRepository = contactRepository;
        this.validationService = validationService;
    }


    /**
     * @param request 
     * @return
     */
    @Transactional
    @Override
    public ContactResponse create(User user, CreateContactRequest request) {
        validationService.validate(request);

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setFirstName(request.getFirstName());
        contact.setLastName(request.getLastName());
        contact.setEmail(request.getEmail());
        contact.setPhone(request.getPhone());
        contact.setUser(user);

        contactRepository.save(contact);

        return toContactResponse(contact);
    }

    /**
     * @param user 
     * @param id
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public ContactResponse get(User user, String id) {
        Contact contact = contactRepository.findFirstByUserAndId(user, id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        return toContactResponse(contact);
    }

    /**
     * @param user 
     * @param request
     * @return
     */
    @Override
    @Transactional
    public ContactResponse update(User user, UpdateContactRequest request) {
        validationService.validate(request);

        Contact contact = contactRepository.findFirstByUserAndId(user, request.getId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        contact.setFirstName(request.getFirstName());
        contact.setLastName(request.getLastName());
        contact.setEmail(request.getEmail());
        contact.setPhone(request.getPhone());
        contactRepository.save(contact);

        return toContactResponse(contact);
    }

    /**
     * @param user
     * @param contactId
     */
    @Override
    @Transactional
    public void delete(User user, String contactId) {
        Contact contact = contactRepository.findFirstByUserAndId(user, contactId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        contactRepository.delete(contact);
    }

    private ContactResponse toContactResponse(Contact contact) {
        return ContactResponse.builder()
                .id(contact.getId())
                .firstName(contact.getFirstName())
                .lastName(contact.getLastName())
                .email(contact.getEmail())
                .phone(contact.getPhone())
                .build();
    }

}
