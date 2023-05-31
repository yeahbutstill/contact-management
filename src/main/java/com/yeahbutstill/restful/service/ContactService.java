package com.yeahbutstill.restful.service;

import com.yeahbutstill.restful.entity.User;
import com.yeahbutstill.restful.model.ContactResponse;
import com.yeahbutstill.restful.model.CreateContactRequest;
import com.yeahbutstill.restful.model.SearchContactRequest;
import com.yeahbutstill.restful.model.UpdateContactRequest;
import org.springframework.data.domain.Page;

public interface ContactService {

    ContactResponse create(User user, CreateContactRequest request);
    ContactResponse get(User user, String id);
    ContactResponse update(User user, UpdateContactRequest request);
    void delete(User user, String contactId);
    Page<ContactResponse> search(User user, SearchContactRequest request);
}
