package com.yeahbutstill.restful.model;

import lombok.Builder;

@Builder
public record AddressResponse(
        String id,
        String street,
        String city,
        String province,
        String country,
        String postalCode
){}
