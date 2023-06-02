package com.yeahbutstill.restful.controller;

import com.yeahbutstill.restful.entity.User;
import com.yeahbutstill.restful.model.AddressResponse;
import com.yeahbutstill.restful.model.CreateAddressRequest;
import com.yeahbutstill.restful.model.UpdateAddressRequest;
import com.yeahbutstill.restful.model.WebResponse;
import com.yeahbutstill.restful.service.AddressService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping(
            path = "/{contactId}/addresses",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public WebResponse<AddressResponse> create(User user,
                                               @RequestBody CreateAddressRequest request,
                                               @PathVariable("contactId") String contactId) {
        request.setContactId(contactId);
        AddressResponse addressResponse = addressService.create(user, request);
        return WebResponse.<AddressResponse>builder()
                .data(addressResponse)
                .build();
    }

    @GetMapping(
            path = "/{contactId}/addresses/{addressId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AddressResponse> get(User user,
                                            @PathVariable("contactId") String contactId,
                                            @PathVariable("addressId") String addressId) {
        AddressResponse addressResponse = addressService.get(user, contactId, addressId);

        return WebResponse.<AddressResponse>builder()
                .data(addressResponse)
                .build();
    }

    @PutMapping(
            path = "/{contactId}/addresses/{addressId}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AddressResponse> update(User user,
                                               @PathVariable("contactId") String contactId,
                                               @PathVariable("addressId") String addressId,
                                               @RequestBody UpdateAddressRequest request) {
        request.setContactId(contactId);
        request.setAddressId(addressId);
        AddressResponse addressResponse = addressService.update(user, request);

        return WebResponse.<AddressResponse>builder()
                .data(addressResponse)
                .build();
    }

    @DeleteMapping(
            path = "/{contactId}/addresses/{addressId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> remove(User user,
                                               @PathVariable("contactId") String contactId,
                                               @PathVariable("addressId") String addressId) {
        addressService.remove(user, contactId, addressId);

        return WebResponse.<String>builder()
                .data("OK")
                .build();
    }

    @GetMapping(
            path = "/{contactId}/addresses",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<AddressResponse>> list(User user,
                                                   @PathVariable("contactId") String contactId) {
        List<AddressResponse> addressResponses = addressService.list(user, contactId);

        return WebResponse.<List<AddressResponse>>builder()
                .data(addressResponses)
                .build();
    }

}
