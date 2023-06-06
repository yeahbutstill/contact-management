package com.yeahbutstill.restful.model;

import lombok.Builder;

import java.util.List;

@Builder
public record WebResponse<T>(
        T data,
        List<String> errors,
        PagingResponse paging
) {
}
