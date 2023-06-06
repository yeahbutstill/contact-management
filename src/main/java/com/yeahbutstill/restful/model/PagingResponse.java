package com.yeahbutstill.restful.model;

import lombok.Builder;

@Builder
public record PagingResponse(
        Integer currentPage,
        Integer totalPage,
        Integer size
) {
}
