package ru.sfedu.teamselection.mapper;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.sfedu.teamselection.domain.Technology;
import ru.sfedu.teamselection.dto.TechnologyDto;

class TechnologyDtoMapperTest {
    private final TechnologyMapper underTest = TechnologyMapper.INSTANCE;

    @Test
    void mapToEntity() {
        Technology expected = Technology.builder()
                .id(12L)
                .name("Name")
                .build();

        TechnologyDto dto = new TechnologyDto()
                .id(12L)
                .name("Name");

        Technology actual = underTest.mapToEntity(dto);
        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getName(), actual.getName());
    }

    @Test
    void mapNullToEntity() {
        Technology actual = underTest.mapToEntity(null);
        Assertions.assertNull(actual);
    }

    @Test
    void mapToDto() {
        TechnologyDto expected = new TechnologyDto()
                .id(12L)
                .name("Name");

        Technology entity = Technology.builder()
                .id(12L)
                .name("Name")
                .build();
        TechnologyDto actual = underTest.mapToDto(entity);

        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getName(), actual.getName());
    }

    @Test
    void mapNullToDto() {
        TechnologyDto actual = underTest.mapToDto(null);
        // then
        Assertions.assertNull(actual);
    }

    @Test
    void mapListToEntity() {
        Technology expected1 = Technology.builder()
                .id(12L)
                .name("Name")
                .build();
        Technology expected2 = Technology.builder()
                .id(2L)
                .name("Another name")
                .build();
        List<Technology> expected = List.of(expected1, expected2);

        TechnologyDto dto1 = new TechnologyDto()
                .id(12L)
                .name("Name");
        TechnologyDto dto2 = new TechnologyDto()
                .id(2L)
                .name("Another name");
        List<TechnologyDto> dtoList = List.of(dto1, dto2);

        List<Technology> actual = underTest.mapListToEntity(dtoList);
        Assertions.assertEquals(expected.size(), dtoList.size());
        for (int i = 0; i < expected.size(); i++) {
            Assertions.assertEquals(expected.get(i).getId(), actual.get(i).getId());
            Assertions.assertEquals(expected.get(i).getName(), actual.get(i).getName());
        }
    }

    @Test
    void mapNullListToEntity() {
        List<Technology> actual = underTest.mapListToEntity(null);
        // then
        Assertions.assertNull(actual);
    }

    @Test
    void mapListToDto() {
        TechnologyDto expected1 = new TechnologyDto()
                .id(12L)
                .name("Name");
        TechnologyDto expected2 = new TechnologyDto()
                .id(2L)
                .name("Another name");
        List<TechnologyDto> expected = List.of(expected1, expected2);

        Technology entity1 = Technology.builder()
                .id(12L)
                .name("Name")
                .build();
        Technology entity2 = Technology.builder()
                .id(2L)
                .name("Another name")
                .build();
        List<Technology> entityList = List.of(entity1, entity2);

        List<TechnologyDto> actual = underTest.mapListToDto(entityList);
        Assertions.assertEquals(expected.size(), entityList.size());
        for (int i = 0; i < expected.size(); i++) {
            Assertions.assertEquals(expected.get(i).getId(), actual.get(i).getId());
            Assertions.assertEquals(expected.get(i).getName(), actual.get(i).getName());
        }
    }

    @Test
    void mapNullListToDto() {
        List<TechnologyDto> actual = underTest.mapListToDto(null);
        // then
        Assertions.assertNull(actual);
    }
}