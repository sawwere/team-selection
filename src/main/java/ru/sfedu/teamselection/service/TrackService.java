package ru.sfedu.teamselection.service;

import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sfedu.teamselection.domain.Track;
import ru.sfedu.teamselection.dto.track.TrackCreationDto;
import ru.sfedu.teamselection.dto.track.TrackDto;
import ru.sfedu.teamselection.enums.TrackType;
import ru.sfedu.teamselection.mapper.track.TrackCreationDtoMapper;
import ru.sfedu.teamselection.mapper.track.TrackDtoMapper;
import ru.sfedu.teamselection.repository.TrackRepository;


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
    public Track findByIdOrElseThrow(Long id) throws NoSuchElementException {
        return trackRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Track with id " + id + " does not exist"));
    }

    public List<TrackDto> findAll()
    {
        return trackRepository.findAll().stream().map(trackDtoMapper::mapToDto).toList();
    }

    /**
     * Create a new track
     * @param trackDto DTO containing track data
     * @return created Track entity
     */
    @Transactional
    public Track create(TrackCreationDto trackDto) {
        Track track = trackCreationDtoMapper.mapToEntity(trackDto);

        if (track == null) {
            throw new IllegalArgumentException("Track cannot be null");
        }

        if (trackRepository.findByNameIgnoreCaseAndType(track.getName(), track.getType()).isPresent()) {
            throw new RuntimeException("A track with this name and type already exists");
        }

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
        if (!trackRepository.existsById(id)) {
            throw new NoSuchElementException("Track with id " + id + " does not exist");
        }
        trackRepository.deleteById(id);
    }
}
