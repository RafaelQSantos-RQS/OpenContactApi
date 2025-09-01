package br.com.personal.opencontact.api.common.dto;

import org.springframework.data.domain.Page;
import java.util.List;

public record PageResponseDTO<T>(
        List<T> content,
        int currentPage,
        int pageSize,
        long totalElements,
        int totalPages
) {

    /**
     * Factory method to create a PageResponseDTO from a Spring Data Page object.
     *
     * @param page the page object to be converted
     * @return a PageResponseDTO with the data from the page object
     */
    public static <T> PageResponseDTO<T> fromPage(Page<T> page) {
        return new PageResponseDTO<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
