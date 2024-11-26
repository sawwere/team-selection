package ru.sfedu.teamselection.dto;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.sfedu.teamselection.domain.Technology;

/**
 * DTO for {@link Technology}
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TechnologyDto {
    private Long id;
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TechnologyDto that = (TechnologyDto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
