package ru.sfedu.teamselection.mapper;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.sfedu.teamselection.domain.ProjectType;
import ru.sfedu.teamselection.dto.ProjectTypeDto;

class ProjectTypeDtoMapperTest {
    private final ProjectTypeMapper underTest = ProjectTypeMapper.INSTANCE;

    @Test
    void mapToEntity() {
        ProjectType expected = ProjectType.builder()
                .id(12L)
                .name("Name")
                .build();

        ProjectTypeDto dto = new ProjectTypeDto()
                .id(12L)
                .name("Name");

        ProjectType actual = underTest.mapToEntity(dto);
        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getName(), actual.getName());
    }

    @Test
    void mapNullToEntity() {
        ProjectType actual = underTest.mapToEntity(null);
        Assertions.assertNull(actual);
    }

    @Test
    void mapToDto() {
        ProjectTypeDto expected = new ProjectTypeDto()
                .id(12L)
                .name("Name");

        ProjectType entity = ProjectType.builder()
                .id(12L)
                .name("Name")
                .build();
        ProjectTypeDto actual = underTest.mapToDto(entity);

        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getName(), actual.getName());
    }

    @Test
    void mapNullToDto() {
        ProjectTypeDto actual = underTest.mapToDto(null);
        // then
        Assertions.assertNull(actual);
    }

    @Test
    void mapListToEntity() {
        ProjectType expected1 = ProjectType.builder()
                .id(12L)
                .name("Name")
                .build();
        ProjectType expected2 = ProjectType.builder()
                .id(2L)
                .name("Another name")
                .build();
        List<ProjectType> expected = List.of(expected1, expected2);

        ProjectTypeDto dto1 = new ProjectTypeDto()
                .id(12L)
                .name("Name");
        ProjectTypeDto dto2 = new ProjectTypeDto()
                .id(2L)
                .name("Another name");
        List<ProjectTypeDto> dtoList = List.of(dto1, dto2);

        List<ProjectType> actual = underTest.mapListToEntity(dtoList);
        Assertions.assertEquals(expected.size(), dtoList.size());
        for (int i = 0; i < expected.size(); i++) {
            Assertions.assertEquals(expected.get(i).getId(), actual.get(i).getId());
            Assertions.assertEquals(expected.get(i).getName(), actual.get(i).getName());
        }
    }

    @Test
    void mapNullListToEntity() {
        List<ProjectType> actual = underTest.mapListToEntity(null);
        // then
        Assertions.assertNull(actual);
    }

    @Test
    void mapListToDto() {
        ProjectTypeDto expected1 = new ProjectTypeDto()
                .id(12L)
                .name("Name");
        ProjectTypeDto expected2 = new ProjectTypeDto()
                .id(2L)
                .name("Another name");
        List<ProjectTypeDto> expected = List.of(expected1, expected2);

        ProjectType entity1 = ProjectType.builder()
                .id(12L)
                .name("Name")
                .build();
        ProjectType entity2 = ProjectType.builder()
                .id(2L)
                .name("Another name")
                .build();
        List<ProjectType> entityList = List.of(entity1, entity2);

        List<ProjectTypeDto> actual = underTest.mapListToDto(entityList);
        Assertions.assertEquals(expected.size(), entityList.size());
        for (int i = 0; i < expected.size(); i++) {
            Assertions.assertEquals(expected.get(i).getId(), actual.get(i).getId());
            Assertions.assertEquals(expected.get(i).getName(), actual.get(i).getName());
        }
    }

    @Test
    void mapNullListToDto() {
        List<ProjectTypeDto> actual = underTest.mapListToDto(null);
        // then
        Assertions.assertNull(actual);
    }
}