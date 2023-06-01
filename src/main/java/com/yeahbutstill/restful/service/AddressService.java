package com.yeahbutstill.restful.service;

import com.yeahbutstill.restful.entity.User;
import com.yeahbutstill.restful.model.AddressResponse;
import com.yeahbutstill.restful.model.CreateAddressRequest;

public interface AddressService {

    AddressResponse create(User user, CreateAddressRequest request);
}
