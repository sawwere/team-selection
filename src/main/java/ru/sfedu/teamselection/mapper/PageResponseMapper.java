package ru.sfedu.teamselection.mapper;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import ru.sfedu.teamselection.dto.PageResponse;

@Component
public class PageResponseMapper {
    public <T> PageResponse<T> toDto(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
