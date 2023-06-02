package com.yeahbutstill.restful.service;

import com.yeahbutstill.restful.entity.User;
import com.yeahbutstill.restful.model.AddressResponse;
import com.yeahbutstill.restful.model.CreateAddressRequest;
import com.yeahbutstill.restful.model.UpdateAddressRequest;

import java.util.List;

public interface AddressService {

    AddressResponse create(User user, CreateAddressRequest request);
    AddressResponse get(User user, String contactId, String addressId);
    AddressResponse update(User user, UpdateAddressRequest request);
    void remove(User user, String contactId, String addressId);
    List<AddressResponse> list(User user, String contactId);

}
