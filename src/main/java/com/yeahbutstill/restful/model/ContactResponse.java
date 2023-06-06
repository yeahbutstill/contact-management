package com.yeahbutstill.restful.model;

import lombok.Builder;

@Builder
public record ContactResponse(
        String id,
        String firstName,
        String lastName,
        String email,
        String phone
) {}
