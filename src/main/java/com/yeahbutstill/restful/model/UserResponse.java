package com.yeahbutstill.restful.model;

import lombok.Builder;

@Builder
public record UserResponse(
        String username,
        String name
){}