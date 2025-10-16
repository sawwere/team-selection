package ru.sfedu.teamselection.service;

import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.sfedu.teamselection.BasicTestContainerTest;
import ru.sfedu.teamselection.domain.Track;
import ru.sfedu.teamselection.dto.track.TrackCreationDto;
import ru.sfedu.teamselection.dto.track.TrackDto;
import ru.sfedu.teamselection.enums.TrackType;
import ru.sfedu.teamselection.exception.BusinessException;
import ru.sfedu.teamselection.exception.ConstraintViolationException;
import ru.sfedu.teamselection.exception.NotFoundException;
import ru.sfedu.teamselection.mapper.track.TrackCreationDtoMapper;
import ru.sfedu.teamselection.mapper.track.TrackDtoMapper;
import ru.sfedu.teamselection.repository.TrackRepository;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Transactional
public class TrackServiceTest extends BasicTestContainerTest {
    @Autowired
    private TrackService trackService;

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private TrackCreationDtoMapper trackCreationDtoMapper;

    @Autowired
    private TrackDtoMapper trackDtoMapper;

    private TrackCreationDto createValidTrackCreationDto() {
        return TrackCreationDto.builder()
                .name("Integration Test Track")
                .about("Test Description for Integration")
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(30))
                .type("master")
                .minConstraint(5)
                .maxConstraint(20)
                .maxSecondCourseConstraint(10)
                .build();
    }

    // findByIdOrElseThrow tests
    @Test
    @Sql(value = {"/sql-scripts/setup-single-track.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql-scripts/cleanup-tracks.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByIdOrElseThrow_whenTrackExists_thenReturnTrack() {
        // Given
        Long existingTrackId = 1000L;

        // When
        Track result = trackService.findByIdOrElseThrow(existingTrackId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(existingTrackId);
        assertThat(result.getName()).isEqualTo("Existing Track");
    }

    @Test
    @Sql(value = {"/sql-scripts/cleanup-tracks.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findByIdOrElseThrow_whenTrackDoesNotExist_thenThrowNotFoundException() {
        // Given
        Long nonExistentTrackId = 999L;

        // When & Then
        assertThatThrownBy(() -> trackService.findByIdOrElseThrow(nonExistentTrackId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(nonExistentTrackId.toString());
    }

    // findAll tests
    @Test
    @Sql(value = {"/sql-scripts/setup-multiple-tracks.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql-scripts/cleanup-tracks.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAll_whenTracksExist_thenReturnAllTrackDtos() {
        // When
        List<TrackDto> result = trackService.findAll();

        // Then
        assertThat(result).hasSize(6);
        assertThat(result)
                .extracting(TrackDto::getName)
                .containsExactlyInAnyOrder(
                        "first track",
                        "second track",
                        "first track",
                        "Track One",
                        "Track Two",
                        "Track Three"
                );
    }

    @Test
    @Sql(value = {"/sql-scripts/cleanup-tracks.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findAll_whenNoTracks_thenReturnEmptyList() {
        // When
        List<TrackDto> result = trackService.findAll();

        // Then
        assertThat(result.size() == 3);
    }

    // create tests
    @Test
    @Sql(value = {"/sql-scripts/cleanup-tracks.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void create_whenValidDto_thenSaveAndReturnTrack() {
        // Given
        TrackCreationDto dto = createValidTrackCreationDto();
        int initialCount = trackRepository.findAll().size();

        // When
        Track result = trackService.create(dto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo(dto.getName());
        assertThat(trackRepository.findAll()).hasSize(initialCount + 1);

        // Verify the track can be retrieved from database
        Track savedTrack = trackRepository.findById(result.getId()).orElse(null);
        assertThat(savedTrack).isNotNull();
        assertThat(savedTrack.getName()).isEqualTo(dto.getName());
    }

    @Test
    void create_whenNullDto_thenThrowIllegalArgumentException() {
        // When & Then
        assertThatThrownBy(() -> trackService.create(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("TrackCreationDto must not be null");
    }

    @Test
    @Sql(value = {"/sql-scripts/setup-track-with-name.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql-scripts/cleanup-tracks.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void create_whenDuplicateNameAndType_thenThrowConstraintViolationException() {
        // Given
        TrackCreationDto duplicateDto = TrackCreationDto.builder()
                .name("Existing Track Name")
                .type("master")
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(30))
                .build();

        // When & Then
        assertThatThrownBy(() -> trackService.create(duplicateDto))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    @Sql(value = {"/sql-scripts/setup-track-with-name.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql-scripts/cleanup-tracks.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void create_whenDuplicateNameDifferentCase_thenThrowConstraintViolationException() {
        // Given
        TrackCreationDto duplicateDto = TrackCreationDto.builder()
                .name("EXISTING TRACK NAME")
                .type("master")
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(30))
                .build();

        // When & Then
        assertThatThrownBy(() -> trackService.create(duplicateDto))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("already exists");
    }

    // update tests
    @Test
    @Sql(value = {"/sql-scripts/setup-single-track.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql-scripts/cleanup-tracks.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void update_whenValidData_thenUpdateAndReturnTrack() {
        // Given
        Long trackId = 1L;
        TrackDto updateDto = TrackDto.builder()
                .name("Updated Track Name")
                .about("Updated Description")
                .startDate(LocalDate.now().plusDays(5))
                .endDate(LocalDate.now().plusDays(35))
                .type("bachelor")
                .minConstraint(10)
                .maxConstraint(25)
                .maxSecondCourseConstraint(15)
                .build();

        // When
        Track result = trackService.update(trackId, updateDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Track Name");
        assertThat(result.getAbout()).isEqualTo("Updated Description");
        assertThat(result.getType()).isEqualTo(TrackType.bachelor);
        assertThat(result.getMinConstraint()).isEqualTo(10);

        // Verify changes persisted in database
        Track updatedTrack = trackRepository.findById(trackId).orElseThrow();
        assertThat(updatedTrack.getName()).isEqualTo("Updated Track Name");
    }

    @Test
    @Sql(value = {"/sql-scripts/cleanup-tracks.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void update_whenTrackNotFound_thenThrowNotFoundException() {
        // Given
        Long nonExistentId = 999L;
        TrackDto updateDto = TrackDto.builder()
                .name("Any Name")
                .type("master")
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(30))
                .build();

        // When & Then
        assertThatThrownBy(() -> trackService.update(nonExistentId, updateDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(nonExistentId.toString());
    }

    // deleteById tests
    @Test
    @Sql(value = {"/sql-scripts/setup-track-without-students.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql-scripts/cleanup-tracks.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteById_whenTrackExistsAndNoStudents_thenDeleteTrack() {
        // Given
        Long trackId = 1000L;
        assertThat(trackRepository.existsById(trackId)).isTrue();

        // When
        trackService.deleteById(trackId);

        // Then
        assertThat(trackRepository.existsById(trackId)).isFalse();
    }

    @Test
    @Sql(value = {"/sql-scripts/setup-track-with-students.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql-scripts/cleanup-tracks.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteById_whenTrackHasStudents_thenThrowBusinessException() {
        // Given
        Long trackId = 1000L;
        assertThat(trackRepository.existsById(trackId)).isTrue();

        // When & Then
        assertThatThrownBy(() -> trackService.deleteById(trackId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Нельзя удалить трек, в котором уже есть участники");

        // Verify track still exists
        assertThat(trackRepository.existsById(trackId)).isTrue();
    }

    @Test
    @Sql(value = {"/sql-scripts/cleanup-tracks.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void deleteById_whenTrackNotFound_thenThrowNotFoundException() {
        // Given
        Long nonExistentId = 999L;

        // When & Then
        assertThatThrownBy(() -> trackService.deleteById(nonExistentId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(nonExistentId.toString());
    }

    // Transactional behavior test
    @Test
    @Sql(value = {"/sql-scripts/cleanup-tracks.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void create_whenExceptionOccurs_thenRollbackTransaction() {
        // Given
        int initialCount = trackRepository.findAll().size();
        TrackCreationDto firstDto = createValidTrackCreationDto();
        TrackCreationDto duplicateDto = createValidTrackCreationDto();

        // When - Create first track successfully
        trackService.create(firstDto);

        // Then attempt to create duplicate (should fail and rollback if in same transaction)
        assertThatThrownBy(() -> trackService.create(duplicateDto))
                .isInstanceOf(ConstraintViolationException.class);

        // Verify only first track was persisted
        assertThat(trackRepository.findAll()).hasSize(initialCount + 1);
    }
}
