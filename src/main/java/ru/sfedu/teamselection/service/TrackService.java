package ru.sfedu.teamselection.service;

import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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


@Slf4j
@RequiredArgsConstructor
@Service
public class TrackService {

    private final TrackRepository trackRepository;
    private final TrackCreationDtoMapper trackCreationDtoMapper;

    private final TrackDtoMapper trackDtoMapper;

    /**
     * Find Track entity by id
     * @param id track id
     * @return entity with given id
     * @throws NoSuchElementException in case there is no track with such id
     */
    public Track findByIdOrElseThrow(Long id) {
        return trackRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id.toString()));
    }

    public List<TrackDto> findAll() {
        return trackRepository.findAll().stream().map(trackDtoMapper::mapToDto).toList();
    }

    /**
     * Create a new track
     * @param dto DTO containing track data
     * @return created Track entity
     */
    @Transactional
    public Track create(TrackCreationDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("TrackCreationDto must not be null");
        }
        Track track = trackCreationDtoMapper.mapToEntity(dto);

        // проверяем дубликат
        trackRepository.findByNameIgnoreCaseAndType(track.getName(), track.getType())
                .ifPresent(t -> {
                    throw new ConstraintViolationException(
                            String.format("Track with name '%s' and type '%s' already exists",
                                    dto.getName(),
                                    dto.getType()
                            )
                    );
                });

        return trackRepository.save(track);
    }

    /**
     * Update an existing track
     * @param id id of the track to update
     * @param trackDto DTO containing updated track data
     * @return updated Track entity
     */
    @Transactional
    public Track update(Long id, TrackDto trackDto) {
        Track existingTrack = findByIdOrElseThrow(id);

        existingTrack.setName(trackDto.getName());
        existingTrack.setAbout(trackDto.getAbout());
        existingTrack.setStartDate(trackDto.getStartDate());
        existingTrack.setEndDate(trackDto.getEndDate());
        existingTrack.setType(TrackType.valueOf(trackDto.getType()));
        existingTrack.setMinConstraint(trackDto.getMinConstraint());
        existingTrack.setMaxConstraint(trackDto.getMaxConstraint());
        existingTrack.setMaxSecondCourseConstraint(trackDto.getMaxSecondCourseConstraint());

        return trackRepository.save(existingTrack);
    }

    /**
     * Delete track by id
     * @param id track id
     */
    @Transactional
    public void deleteById(Long id) {
        Track track = findByIdOrElseThrow(id);
        if (!track.getStudents().isEmpty()) {
            log.error(
                    "Track delete failed with BusinessException. Tried to delete track {}, but it has students",
                    id
            );
            throw new BusinessException(
                    "Нельзя удалить трек, в котором уже есть участники. "
                    + "Сначала нужно удалить из него всех студентов и команды."
            );
        }
        trackRepository.delete(track);
    }
}
