package com.yeahbutstill.restful.service;

import com.yeahbutstill.restful.entity.User;
import com.yeahbutstill.restful.model.ContactResponse;
import com.yeahbutstill.restful.model.CreateContactRequest;
import com.yeahbutstill.restful.model.UpdateContactRequest;

public interface ContactService {

    ContactResponse create(User user, CreateContactRequest request);
    ContactResponse get(User user, String id);
    ContactResponse update(User user, UpdateContactRequest request);

}
