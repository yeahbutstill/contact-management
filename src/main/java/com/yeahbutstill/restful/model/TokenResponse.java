package com.yeahbutstill.restful.model;

import lombok.Builder;

@Builder
public record TokenResponse(
        String token,
        Long expiredAt
) {}
