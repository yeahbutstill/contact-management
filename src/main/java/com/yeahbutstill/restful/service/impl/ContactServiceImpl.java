package com.yeahbutstill.restful.service.impl;

import com.yeahbutstill.restful.entity.Contact;
import com.yeahbutstill.restful.entity.User;
import com.yeahbutstill.restful.model.ContactResponse;
import com.yeahbutstill.restful.model.CreateContactRequest;
import com.yeahbutstill.restful.repository.ContactRepository;
import com.yeahbutstill.restful.service.ContactService;
import com.yeahbutstill.restful.service.ValidationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        contact.setFirstname(request.getFirstName());
        contact.setLastname(request.getLastName());
        contact.setEmail(request.getEmail());
        contact.setPhone(request.getPhone());
        contact.setUser(user);

        contactRepository.save(contact);

        return toContactResponse(contact);
    }

    private ContactResponse toContactResponse(Contact contact) {
        return ContactResponse.builder()
                .id(contact.getId())
                .firstName(contact.getFirstname())
                .lastName(contact.getLastname())
                .email(contact.getEmail())
                .phone(contact.getPhone())
                .build();
    }

}
